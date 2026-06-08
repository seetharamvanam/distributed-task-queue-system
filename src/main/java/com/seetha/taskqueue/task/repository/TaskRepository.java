package com.seetha.taskqueue.task.repository;

import com.seetha.taskqueue.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<?> findTaskByTaskStatus_Pending();
}
