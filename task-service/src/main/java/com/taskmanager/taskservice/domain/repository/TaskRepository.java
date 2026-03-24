package com.taskmanager.taskservice.domain.repository;

import com.taskmanager.taskservice.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Task> findByOwnerIdAndStatus(Long ownerId, String status, Pageable pageable);
    Page<Task> findByOwnerIdAndPriority(Long ownerId, String priority, Pageable pageable);
    Page<Task> findByOwnerIdAndStatusAndPriority(Long ownerId, String status, String priority, Pageable pageable);
}

