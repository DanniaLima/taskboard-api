package com.dannialima.taskboard.controller;

import com.dannialima.taskboard.dto.TaskRequestDTO;
import com.dannialima.taskboard.dto.TaskResponseDTO;
import com.dannialima.taskboard.exception.ResourceNotFoundException;
import com.dannialima.taskboard.model.Task;
import com.dannialima.taskboard.model.TaskPriority;
import com.dannialima.taskboard.model.TaskStatus;
import com.dannialima.taskboard.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private TaskService taskService;

    private Task task;
    private TaskResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setDueDate(LocalDateTime.now().plusDays(1));

        responseDTO = new TaskResponseDTO(
                1L,
                "Test Task",
                "Description",
                TaskStatus.PENDING,
                TaskPriority.HIGH,
                task.getDueDate(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /api/tasks should return 200 with the task list")
    void shouldReturnAllTasks() throws Exception {
        when(taskService.findAll()).thenReturn(List.of(task));
        when(taskService.toResponseDTO(task)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} should return 200 when task exists")
    void shouldReturnTaskById() throws Exception {
        when(taskService.findByIdOrThrow(1L)).thenReturn(task);
        when(taskService.toResponseDTO(task)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} should return 404 when task does not exist")
    void shouldReturn404WhenTaskNotFound() throws Exception {
        when(taskService.findByIdOrThrow(99L))
                .thenThrow(new ResourceNotFoundException("Task not found with id: 99"));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.parseMediaType("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    @DisplayName("POST /api/tasks should return 201 when request is valid")
    void shouldCreateTaskWhenValid() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "New Task", "Description", TaskStatus.PENDING, TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(2)
        );

        when(taskService.save(any(Task.class))).thenReturn(task);
        when(taskService.toResponseDTO(task)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @DisplayName("POST /api/tasks should return 400 when title is blank")
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        TaskRequestDTO invalidDTO = new TaskRequestDTO(
                "", "Description", TaskStatus.PENDING, TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(2)
        );

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalidFields.title").exists());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} should return 200 when update is valid")
    void shouldUpdateTaskWhenValid() throws Exception {
        TaskRequestDTO requestDTO = new TaskRequestDTO(
                "Updated Task", "Updated Description", TaskStatus.IN_PROGRESS, TaskPriority.LOW,
                LocalDateTime.now().plusDays(3)
        );

        when(taskService.findByIdOrThrow(1L)).thenReturn(task);
        when(taskService.save(any(Task.class))).thenReturn(task);
        when(taskService.toResponseDTO(task)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} should return 204 when task is deleted")
    void shouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
