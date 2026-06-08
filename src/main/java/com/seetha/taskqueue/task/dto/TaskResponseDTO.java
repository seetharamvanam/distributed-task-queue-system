package com.seetha.taskqueue.task.dto;

import com.seetha.taskqueue.task.enums.TaskStatus;
import com.seetha.taskqueue.task.enums.TaskType;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        TaskType taskType,
        String payload,
        TaskStatus taskStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        String errorMessage
) {
}
