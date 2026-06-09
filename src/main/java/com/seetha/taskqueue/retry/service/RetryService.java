package com.seetha.taskqueue.retry.service;

import com.seetha.taskqueue.retry.entity.TaskRetry;
import com.seetha.taskqueue.retry.enums.RetryStatus;
import com.seetha.taskqueue.retry.policy.RetryPolicy;
import com.seetha.taskqueue.retry.repository.TaskRetryRepository;
import com.seetha.taskqueue.task.entity.Task;
import com.seetha.taskqueue.task.enums.TaskStatus;
import com.seetha.taskqueue.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RetryService {

    private final TaskRepository taskRepository;
    private final TaskRetryRepository taskRetryRepository;
    private final RetryPolicy retryPolicy;

    public RetryService(TaskRepository taskRepository, TaskRetryRepository taskRetryRepository, RetryPolicy retryPolicy) {
        this.taskRepository = taskRepository;
        this.taskRetryRepository = taskRetryRepository;
        this.retryPolicy = retryPolicy;
    }

    public void handleFailedTask(Task task, String errorMessage){
        Integer currentRetryCount = task.getRetryCount();
        Integer maxRetryCount = task.getMaxRetries();

        if( currentRetryCount < maxRetryCount ){
            Integer nextRetryCount = currentRetryCount + 1;
            LocalDateTime nextRetryAt = retryPolicy.calculateNextRetryAt(task.getTaskType(), nextRetryCount);
            LocalDateTime now = LocalDateTime.now();

            TaskRetry taskRetry = TaskRetry.builder()
                    .taskId(task.getId())
                    .attemptNumber(nextRetryCount)
                    .status(RetryStatus.SCHEDULED)
                    .errorMessage(errorMessage)
                    .nextRetryAt(nextRetryAt)
                    .createdAt(now)
                    .updatedAt(now).build();
            taskRetryRepository.save(taskRetry);

            task.setRetryCount(nextRetryCount);
            task.setTaskStatus(TaskStatus.RETRY_SCHEDULED);
            task.setNextRetryAt(nextRetryAt);
            task.setLockedBy(null);
            task.setLockedAt(null);
            task.setErrorMessage(errorMessage);
            task.setUpdatedAt(now);
            taskRepository.save(task);
        }else{
            task.setTaskStatus(TaskStatus.FAILED);
            task.setErrorMessage(errorMessage);
            task.setLockedBy(null);
            task.setLockedAt(null);
            task.setCompletedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);
        }
    }

    public void promoteRetryTasks(){
        LocalDateTime now = LocalDateTime.now();
        List<Task> retryScheduledTasks = taskRepository.findByTaskStatusAndNextRetryAtLessThanEqual
                (TaskStatus.RETRY_SCHEDULED,now);
        retryScheduledTasks.forEach(task -> {
            task.setTaskStatus(TaskStatus.PENDING);
            task.setNextRetryAt(null);
            task.setUpdatedAt(now);
            taskRepository.save(task);
        });
    }
}
