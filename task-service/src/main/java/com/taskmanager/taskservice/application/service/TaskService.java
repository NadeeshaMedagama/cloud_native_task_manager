package com.taskmanager.taskservice.application.service;

import com.taskmanager.taskservice.domain.model.Task;
import com.taskmanager.taskservice.domain.repository.TaskRepository;
import com.taskmanager.taskservice.application.dto.CreateTaskRequest;
import com.taskmanager.taskservice.application.mapper.TaskMapper;
import com.taskmanager.common.dto.TaskDTO;
import com.taskmanager.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public Page<TaskDTO> getTasksByOwner(Long ownerId, String status, String priority, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Task> tasks;
        if (status != null && priority != null) {
            tasks = taskRepository.findByOwnerIdAndStatusAndPriority(ownerId, status, priority, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByOwnerIdAndStatus(ownerId, status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByOwnerIdAndPriority(ownerId, priority, pageable);
        } else {
            tasks = taskRepository.findByOwnerId(ownerId, pageable);
        }

        return tasks.map(taskMapper::toDTO);
    }

    public TaskDTO getTaskById(Long taskId, Long ownerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOwnerId().equals(ownerId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        return taskMapper.toDTO(task);
    }

    public TaskDTO createTask(CreateTaskRequest request, Long ownerId) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : "TODO");
        task.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        task.setDueDate(request.getDueDate());
        task.setOwnerId(ownerId);

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDTO(savedTask);
    }

    public TaskDTO updateTask(Long taskId, CreateTaskRequest request, Long ownerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOwnerId().equals(ownerId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDTO(updatedTask);
    }

    public TaskDTO updateTaskStatus(Long taskId, String status, Long ownerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOwnerId().equals(ownerId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDTO(updatedTask);
    }

    public void deleteTask(Long taskId, Long ownerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getOwnerId().equals(ownerId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        taskRepository.delete(task);
    }

    public Task getTaskByIdForAdmin(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public Page<TaskDTO> getAllTasks(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return taskRepository.findAll(pageable).map(taskMapper::toDTO);
    }
}

