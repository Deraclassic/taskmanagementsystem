package com.dera.service;

import com.dera.enums.TaskStatus;
import com.dera.model.Task;

import java.util.List;

public interface TaskService {
    Task createTask(Task task, String requesterRole) throws Exception;
    Task getTaskById(Long id) throws Exception;
    List<Task> getAllTasks(TaskStatus status) throws Exception;
    Task updateTask(Long id, Task updatedTask, Long userId) throws Exception;
    void deleteTask(Long id) throws Exception;
    Task assignedToUser(Long userId, Long taskId) throws Exception;
    List<Task> assignedUsersTasks(Long userId, TaskStatus status) throws Exception;
    Task completedTask(Long taskId) throws Exception;
}
