package com.seetha.taskqueue.metrics.dto;

public record MetricsResponseDTO(
        Long totalTasks,
        Long pendingTasks,
        Long claimedTasks,
        Long runningTasks,
        Long successfulTasks,
        Long failedTasks,
        Long retryScheduledTasks,
        Double averageExecutionTimeMillis
) {
}
