package com.seetha.taskqueue.retry.scheduler;

import com.seetha.taskqueue.retry.service.RetryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RetryScheduler {

    private final RetryService retryService;

    public RetryScheduler(RetryService retryService) {
        this.retryService = retryService;
    }

   @Scheduled(fixedDelay = 5000)
   public void promoteRetryTasks(){
        retryService.promoteRetryTasks();
   }
}
