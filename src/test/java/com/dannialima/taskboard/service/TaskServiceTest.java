package com.dannialima.taskboard.service;

import com.dannialima.taskboard.dto.TaskResponseDTO;
import com.dannialima.taskboard.exception.ResourceNotFoundException;
import com.dannialima.taskboard.model.Task;
import com.dannialima.taskboard.model.TaskPriority;
import com.dannialima.taskboard.model.TaskStatus;
import com.dannialima.taskboard.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Description Test");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setDueDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("Should save task successfully")
    void shouldSaveTaskSuccessfully() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.save(task);

        assertNotNull(savedTask);
        assertEquals(task.getId(), savedTask.getId());
        assertEquals("Test Task", savedTask.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    @DisplayName("Should return task when ID exists")
    void shouldReturnTaskWhenIdExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task foundTask = taskService.findByIdOrThrow(1L);

        assertNotNull(foundTask);
        assertEquals(1L, foundTask.getId());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task ID does not exist")
    void shouldThrowExceptionWhenIdDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.findByIdOrThrow(99L));
        verify(taskRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should return tasks filtered by status")
    void shouldReturnTasksFilteredByStatus() {
        when(taskRepository.findByStatus(TaskStatus.PENDING)).thenReturn(List.of(task));

        List<Task> result = taskService.findByStatus(TaskStatus.PENDING);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(TaskStatus.PENDING, result.get(0).getStatus());
        verify(taskRepository, times(1)).findByStatus(TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Should convert entity Task to TaskResponseDTO correctly")
    void shouldConvertTaskToResponseDTO() {
        TaskResponseDTO dto = taskService.toResponseDTO(task);

        assertNotNull(dto);
        assertEquals(task.getId(), dto.id());
        assertEquals(task.getTitle(), dto.title());
        assertEquals(task.getStatus(), dto.status());
        assertEquals(task.getPriority(), dto.priority());
    }

    @Test
    @DisplayName("Should delete task by ID successfully")
    void shouldDeleteTaskByIdSuccessfully() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteById(1L);

        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }
}
