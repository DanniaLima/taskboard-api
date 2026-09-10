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
@Tag(name = "Tasks", description = "Endpoints para gerenciamento de tarefas do quadro")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Listar tarefas", description = "Retorna todas as tarefas ou filtra pelo status fornecido.")
    @ApiResponse(responseCode = "200", description = "Lista obtida com sucesso")
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(@RequestParam(required = false) TaskStatus status) {
        List<Task> tasks = (status != null) ? taskService.findByStatus(status) : taskService.findAll();
        List<TaskResponseDTO> response = tasks.stream()
                .map(taskService::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar tarefa por ID", description = "Retorna os detalhes de uma tarefa específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.findByIdOrThrow(id);
        return ResponseEntity.ok(taskService.toResponseDTO(task));
    }

    @Operation(summary = "Criar uma nova tarefa", description = "Cadastra uma nova tarefa no quadro com validação de campos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (erro de validação DTO)")
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

    @Operation(summary = "Atualizar uma tarefa", description = "Atualiza os dados de uma tarefa existente pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
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

    @Operation(summary = "Deletar uma tarefa", description = "Remove uma tarefa do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
