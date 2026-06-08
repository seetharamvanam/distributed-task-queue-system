package com.seetha.taskqueue.task.controller;

import com.seetha.taskqueue.task.dto.CreateTaskRequestDTO;
import com.seetha.taskqueue.task.dto.TaskResponseDTO;
import com.seetha.taskqueue.task.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponseDTO createNewTask(@RequestBody CreateTaskRequestDTO createTaskRequestDTO) {
        return taskService.createNewTask(createTaskRequestDTO);
    }

    @GetMapping
    public List<TaskResponseDTO> getAllTasks(){
        return  taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }
}
