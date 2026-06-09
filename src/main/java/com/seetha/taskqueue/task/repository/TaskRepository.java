package com.seetha.taskqueue.task.repository;

import com.seetha.taskqueue.task.entity.Task;
import com.seetha.taskqueue.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByTaskStatus(TaskStatus taskStatus);
    List<Task> findByTaskStatusAndNextRetryAtLessThanEqual(TaskStatus taskStatus, LocalDateTime currentTime);
}
