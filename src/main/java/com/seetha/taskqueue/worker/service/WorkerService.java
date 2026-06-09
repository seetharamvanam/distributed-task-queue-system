package com.seetha.taskqueue.worker.service;

import com.seetha.taskqueue.retry.service.RetryService;
import com.seetha.taskqueue.task.entity.Task;
import com.seetha.taskqueue.task.enums.TaskStatus;
import com.seetha.taskqueue.task.repository.TaskRepository;
import com.seetha.taskqueue.task.service.TaskService;
import com.seetha.taskqueue.worker.executor.TaskExecutor;
import com.seetha.taskqueue.worker.identity.WorkerIdentity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkerService {

    private final TaskRepository taskRepository;
    private final TaskExecutor taskExecutor;
    private final RetryService retryService;
    private final TaskService taskService;
    private final WorkerIdentity workerIdentity;

    public WorkerService(TaskRepository taskRepository, TaskExecutor taskExecutor
    , RetryService retryService, TaskService taskService, WorkerIdentity workerIdentity) {
        this.taskRepository = taskRepository;
        this.taskExecutor = taskExecutor;
        this.retryService = retryService;
        this.taskService = taskService;
        this.workerIdentity = workerIdentity;
    }

    public void processPendingTasks(){
        List<Task> claimedTasks = taskService.claimPendingTasks(10, workerIdentity.getWorkerId());
        for (Task task : claimedTasks) {
            task.setTaskStatus(TaskStatus.RUNNING);
            task.setStartedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);
            Boolean status = taskExecutor.executeTask(task);
            if(status){
                task.setTaskStatus(TaskStatus.SUCCESS);
                task.setCompletedAt(LocalDateTime.now());
                task.setUpdatedAt(LocalDateTime.now());
                task.setLockedBy(null);
                task.setLockedAt(null);
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

    public void recoverStuckTasks(){
        LocalDateTime timeout = LocalDateTime.now().minusSeconds(60);
        List<TaskStatus> listOfStatuses = new ArrayList<>();
        listOfStatuses.add(TaskStatus.RUNNING);
        listOfStatuses.add(TaskStatus.CLAIMED);

        List<Task> listOfStuckTasks = taskRepository.findByTaskStatusInAndLockedAtBefore(listOfStatuses, timeout);
        for (Task task : listOfStuckTasks) {
            task.setTaskStatus(TaskStatus.PENDING);
            task.setLockedBy(null);
            task.setLockedAt(null);
            task.setUpdatedAt(LocalDateTime.now());
            task.setErrorMessage("Task recovered after worker timeout");
        }
        taskRepository.saveAll(listOfStuckTasks);
    }
}
