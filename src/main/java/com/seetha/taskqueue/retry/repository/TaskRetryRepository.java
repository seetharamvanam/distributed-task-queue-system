package com.seetha.taskqueue.retry.repository;

import com.seetha.taskqueue.retry.entity.TaskRetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRetryRepository extends JpaRepository<TaskRetry, Long> {

}
