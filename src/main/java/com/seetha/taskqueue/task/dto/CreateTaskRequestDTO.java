package com.seetha.taskqueue.task.dto;

import com.seetha.taskqueue.task.enums.TaskType;

public record CreateTaskRequestDTO(
        TaskType taskType,
        String payload
) {
}
