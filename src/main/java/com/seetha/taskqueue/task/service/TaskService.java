package com.seetha.taskqueue.task.service;

import com.seetha.taskqueue.retry.policy.RetryPolicy;
import com.seetha.taskqueue.task.dto.CreateTaskRequestDTO;
import com.seetha.taskqueue.task.dto.TaskResponseDTO;
import com.seetha.taskqueue.task.entity.Task;
import com.seetha.taskqueue.task.enums.TaskStatus;
import com.seetha.taskqueue.task.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final RetryPolicy retryPolicy;

    public TaskService(TaskRepository taskRepository, RetryPolicy retryPolicy) {
        this.taskRepository = taskRepository;
        this.retryPolicy = new RetryPolicy();
    }

    public TaskResponseDTO createNewTask(CreateTaskRequestDTO createTaskRequestDTO) {
        LocalDateTime now = LocalDateTime.now();
        Task task = Task.builder()
                .taskType(createTaskRequestDTO.taskType())
                .payload(createTaskRequestDTO.payload())
                .taskStatus(TaskStatus.PENDING)
                .retryCount(0)
                .maxRetries(retryPolicy.getMaxRetries(createTaskRequestDTO.taskType()))
                .createdAt(now)
                .updatedAt(now)
                .build();
        //Save Task to the database
        Task savedTask = taskRepository.save(task);
        //Return TaskResponse
        return getTaskResponse(savedTask);
    }

    public List<TaskResponseDTO> getAllTasks(){
        List<TaskResponseDTO> taskResponseDTOList = new ArrayList<>();
        taskRepository.findAll().forEach(task -> {
            taskResponseDTOList.add(getTaskResponse(task));
        });
        return taskResponseDTOList;
    }

    public TaskResponseDTO getTaskById(Long id){
        Task task = taskRepository.findById(id).orElse(null);
        return getTaskResponse(task);
    }

    /*Helper method to generate TaskResponseDTO*/
    private TaskResponseDTO getTaskResponse(Task savedTask) {
        return new TaskResponseDTO(
                savedTask.getId(),
                savedTask.getTaskType(),
                savedTask.getPayload(),
                savedTask.getTaskStatus(),
                savedTask.getCreatedAt(),
                savedTask.getUpdatedAt(),
                savedTask.getStartedAt(),
                savedTask.getCompletedAt(),
                savedTask.getErrorMessage()
        );
    }

    @Transactional
    public List<Task> claimPendingTasks(Integer batchSize, String workerId){
        LocalDateTime now = LocalDateTime.now();
        List<Task> claimedTasks = taskRepository.findPendingTasksForUpdate(batchSize);
        claimedTasks.forEach(task -> {
            task.setTaskStatus(TaskStatus.CLAIMED);
            task.setLockedBy(workerId);
            task.setLockedAt(now);
            task.setUpdatedAt(now);
        });
        taskRepository.saveAll(claimedTasks);
        return claimedTasks;
    }
}
