package com.seetha.taskqueue.metrics.service;

import com.seetha.taskqueue.metrics.dto.MetricsResponseDTO;
import com.seetha.taskqueue.task.enums.TaskStatus;
import com.seetha.taskqueue.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final TaskRepository taskRepository;

    public MetricsService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public MetricsResponseDTO getTaskMetrics(){
        Double avgTime = taskRepository.findAverageExecutionTimeMillis();
        if(avgTime == null){
            avgTime = 0.0;
        }
        return new MetricsResponseDTO(
                taskRepository.count(),
                taskRepository.countByTaskStatus(TaskStatus.PENDING),
                taskRepository.countByTaskStatus(TaskStatus.CLAIMED),
                taskRepository.countByTaskStatus(TaskStatus.RUNNING),
                taskRepository.countByTaskStatus(TaskStatus.SUCCESS),
                taskRepository.countByTaskStatus(TaskStatus.FAILED),
                taskRepository.countByTaskStatus(TaskStatus.RETRY_SCHEDULED
                ), avgTime
        );
    }
}
