package com.taskmanager.taskservice.web.controller;

import com.taskmanager.taskservice.application.dto.CreateTaskRequest;
import com.taskmanager.taskservice.application.service.TaskService;
import com.taskmanager.common.dto.TaskDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        if ("ADMIN".equals(role)) {
            return ResponseEntity.ok(taskService.getAllTasks(page, size, sortBy, direction));
        } else {
            return ResponseEntity.ok(taskService.getTasksByOwner(userId, status, priority, page, size, sortBy, direction));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TaskDTO task = taskService.getTaskById(id, userId);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody CreateTaskRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        TaskDTO task = taskService.createTask(request, userId);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody CreateTaskRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        TaskDTO task = taskService.updateTask(id, request, userId);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestParam String status, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        TaskDTO task = taskService.updateTaskStatus(id, status, userId);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }
}

