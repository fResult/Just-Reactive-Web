package dev.fresult.reactiveweb

import dev.fresult.reactiveweb.entities.Task
import dev.fresult.reactiveweb.entities.TaskDTO
import dev.fresult.reactiveweb.repositories.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TaskService(val repository: TaskRepository) {
  fun all(): Flux<Task> {
    return repository.findAll().map(Task::fromDAO)
  }

  fun byId(id: Long): Mono<TaskDTO> {
    return repository.findById(id).map { Task.fromDAO(it).toDTO() }
  }

  fun create(task: Task): Mono<TaskDTO> {
    val createdTask = repository.save(task.toDAO())
    return createdTask.map { Task.fromDAO(it).toDTO() }
  }
}
