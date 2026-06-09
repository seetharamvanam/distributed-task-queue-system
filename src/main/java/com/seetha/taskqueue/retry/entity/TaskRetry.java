package com.seetha.taskqueue.retry.entity;

import com.seetha.taskqueue.retry.enums.RetryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "task_retries")
public class TaskRetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long taskId;
    @Column(nullable = false)
    private Integer attemptNumber;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RetryStatus status;
    @Column
    private String errorMessage;
    @Column
    private LocalDateTime nextRetryAt;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime updatedAt;
}
