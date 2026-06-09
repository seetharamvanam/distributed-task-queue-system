package com.seetha.taskqueue.retry.policy;

import com.seetha.taskqueue.task.enums.TaskType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RetryPolicy {

    public Integer getMaxRetries(TaskType taskType){
        switch (taskType){
            case SEND_EMAIL -> {return 3;}
            case GENERATE_REPORT ->  {return 2;}
            case PROCESS_FILE ->  {return 1;}
            default -> {throw new IllegalArgumentException("Invalid task type");}
        }
    }

    public LocalDateTime calculateNextRetryAt(TaskType taskType, Integer attemptNumber){
        int maxRetries = getMaxRetries(taskType);

        if( attemptNumber == null || attemptNumber <1 || attemptNumber > maxRetries ){
            throw new IllegalArgumentException("Invalid attempt number");
        }
        return nextRetryTime(attemptNumber);
    }

    private LocalDateTime nextRetryTime(Integer attemptNumber){
        if(attemptNumber == 1){
            return LocalDateTime.now().plusSeconds(30);
        }
        else if(attemptNumber == 2){
            return LocalDateTime.now().plusSeconds(60);
        }
        else if(attemptNumber == 3){
            return LocalDateTime.now().plusSeconds(300);
        }else{
            throw new IllegalArgumentException("Invalid attempt number: " +  attemptNumber);
        }
    }
}
