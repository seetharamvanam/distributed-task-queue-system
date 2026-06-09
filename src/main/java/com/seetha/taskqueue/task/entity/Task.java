package com.seetha.taskqueue.task.entity;

import com.seetha.taskqueue.task.enums.TaskStatus;
import com.seetha.taskqueue.task.enums.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;
    @Column(nullable = false)
    private String payload;
    @Column
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime updatedAt;
    @Column
    private LocalDateTime startedAt;
    @Column
    private LocalDateTime completedAt;
    @Column
    private String errorMessage;
    @Column
    private Integer retryCount;
    @Column
    private Integer maxRetries;
    @Column
    private LocalDateTime nextRetryAt;
    @Column
    private String lockedBy;
    @Column
    private LocalDateTime lockedAt;

    public Task(TaskType taskType, String payload) {
        this.taskType = taskType;
        this.payload = payload;
    }
}
