package com.seetha.taskqueue.worker.executor;

import com.seetha.taskqueue.task.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskExecutor {

    public Boolean executeTask(Task task){
        switch(task.getTaskType()){
            case SEND_EMAIL -> {
                return executeEmailTask(task);
            }
            case GENERATE_REPORT -> {
                return generateReport(task);
            }
            case PROCESS_FILE ->  {
                return processFile(task);
            }
        }
        return false;
    }

    private Boolean executeEmailTask(Task task){
        try{
            System.out.println("Executing email task: "+ task.getPayload());
            return true;
        }catch(Exception e){
            System.out.println("Email task execution failed");
            e.printStackTrace();
        }
        return false;
    }

    private Boolean generateReport(Task task){
        try{
            System.out.println("Generating report: "+ task.getPayload());
            return true;
        }catch(Exception e){
            System.out.println("Report generation failed");
            e.printStackTrace();
        }
        return false;
    }

    private Boolean processFile(Task task){
        try{
            System.out.println("Processing File: "+ task.getPayload());
            return true;
        }catch(Exception e){
            System.out.println("File processing failed");
            e.printStackTrace();
        }
        return false;
    }
}
