package com.dannialima.taskboard.repository;

import com.dannialima.taskboard.model.Task;
import com.dannialima.taskboard.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
}
