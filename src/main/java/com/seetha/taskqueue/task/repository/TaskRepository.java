package com.seetha.taskqueue.task.repository;

import com.seetha.taskqueue.task.entity.Task;
import com.seetha.taskqueue.task.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByTaskStatus(TaskStatus taskStatus);
    List<Task> findByTaskStatusAndNextRetryAtLessThanEqual(TaskStatus taskStatus, LocalDateTime currentTime);
    @Query(
            value = """
                    SELECT *
                    FROM tasks
                    WHERE task_status = 'PENDING'
                    ORDER BY created_at
                    LIMIT :batchSize
                    FOR UPDATE SKIP LOCKED""", nativeQuery = true
    )
    List<Task> findPendingTasksForUpdate(@Param("batchSize") int batchSize);

    List<Task> findByTaskStatusInAndLockedAtBefore(List<TaskStatus> taskStatuses, LocalDateTime lockedAt);

    Long countByTaskStatus(TaskStatus taskStatus);

    @Query(
            value = """
            SELECT AVG(completed_at - started_at) AS avgTime FROM tasks 
            WHERE (task_status = 'SUCCESS' AND started_at IS NOT NULL AND completed_at IS NOT NULL)""",
            nativeQuery = true
    )
    Double findAverageExecutionTimeMillis();
}
