package com.seetha.taskqueue.worker.scheduler;

import com.seetha.taskqueue.worker.service.WorkerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkerScheduler {

    private final WorkerService workerService;

    public WorkerScheduler(WorkerService workerService) {
        this.workerService = workerService;
    }

    @Scheduled(fixedDelay=5000)
    public void processTasks(){
        workerService.processPendingTasks();
    }
}
