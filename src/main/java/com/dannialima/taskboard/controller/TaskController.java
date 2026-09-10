package com.dannialima.taskboard.controller;

import com.dannialima.taskboard.dto.TaskRequestDTO;
import com.dannialima.taskboard.dto.TaskResponseDTO;
import com.dannialima.taskboard.model.Task;
import com.dannialima.taskboard.model.TaskStatus;
import com.dannialima.taskboard.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Endpoints for managing task board items")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "List tasks", description = "Retrieves all tasks or filters them by the provided status.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved task list")
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(@RequestParam(required = false) TaskStatus status) {
        List<Task> tasks = (status != null) ? taskService.findByStatus(status) : taskService.findAll();
        List<TaskResponseDTO> response = tasks.stream()
                .map(taskService::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get task by ID", description = "Returns details of a specific task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.findByIdOrThrow(id);
        return ResponseEntity.ok(taskService.toResponseDTO(task));
    }

    @Operation(summary = "Create a new task", description = "Creates a new task on the board with field validation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data (DTO validation error)")
    })
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status());
        task.setPriority(dto.priority());
        task.setDueDate(dto.dueDate());

        Task savedTask = taskService.save(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.toResponseDTO(savedTask));
    }

    @Operation(summary = "Update a task", description = "Updates an existing task by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO dto) {
        Task existingTask = taskService.findByIdOrThrow(id);

        existingTask.setTitle(dto.title());
        existingTask.setDescription(dto.description());
        existingTask.setStatus(dto.status());
        existingTask.setPriority(dto.priority());
        existingTask.setDueDate(dto.dueDate());

        Task updatedTask = taskService.save(existingTask);
        return ResponseEntity.ok(taskService.toResponseDTO(updatedTask));
    }

    @Operation(summary = "Delete a task", description = "Removes a task from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
