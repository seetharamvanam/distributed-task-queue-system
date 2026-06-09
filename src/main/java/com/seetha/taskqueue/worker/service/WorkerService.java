package com.seetha.taskqueue.worker.service;

import com.seetha.taskqueue.retry.service.RetryService;
import com.seetha.taskqueue.task.entity.Task;
import com.seetha.taskqueue.task.enums.TaskStatus;
import com.seetha.taskqueue.task.repository.TaskRepository;
import com.seetha.taskqueue.worker.executor.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkerService {

    private final TaskRepository taskRepository;
    private final TaskExecutor taskExecutor;
    private final RetryService retryService;
    public WorkerService(TaskRepository taskRepository, TaskExecutor taskExecutor
    , RetryService retryService) {
        this.taskRepository = taskRepository;
        this.taskExecutor = taskExecutor;
        this.retryService = retryService;
    }

    public void processPendingTasks(){
        List<Task> pendingTasks = taskRepository.findAllByTaskStatus(TaskStatus.PENDING);
        for (Task task : pendingTasks) {
            task.setTaskStatus(TaskStatus.RUNNING);
            task.setStartedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);
            Boolean status = taskExecutor.executeTask(task);
            if(status){
                task.setTaskStatus(TaskStatus.SUCCESS);
                task.setCompletedAt(LocalDateTime.now());
                task.setUpdatedAt(LocalDateTime.now());
                taskRepository.save(task);
            }else{
                String errorMessage = "Task execution failed for task type: "+ task.getTaskType();
                retryService.handleFailedTask(task,errorMessage);
                task.setCompletedAt(LocalDateTime.now());
                task.setUpdatedAt(LocalDateTime.now());
                taskRepository.save(task);
            }
        }

    }
}
