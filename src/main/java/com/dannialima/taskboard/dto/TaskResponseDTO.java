package com.dannialima.taskboard.dto;

import com.dannialima.taskboard.model.TaskPriority;
import com.dannialima.taskboard.model.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
