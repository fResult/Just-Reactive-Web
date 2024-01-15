package dev.fresult.reactiveweb

import dev.fresult.reactiveweb.entities.Task
import dev.fresult.reactiveweb.repositories.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KFunction1

@Service
class TaskService(private val repository: TaskRepository) {
  fun all(): Flux<Task> {
    return repository.findAll().map(Task::fromModel)
  }

  fun byId(id: Long): Mono<Task> {
    return repository.findById(id).map(Task::fromModel)
  }

  fun create(task: Task): Mono<Task> {
    val createdTask = repository.save(task.toModel())
    return createdTask.map(Task::fromModel)
  }

  /**
   * @param action "next" | "previous"
   * @return updateTaskStatusById :: (TaskID) -> Mono<TaskDTO>
   */
  fun updateByAction(action: String): KFunction1<Long, Mono<Task>> =
    if (action == "next") ::nextStatus else ::previousStatus

  fun updateById(id: Long, task: Task): Mono<Task> {
    return byId(id).flatMap { existingTask ->
      val taskFromBody = task.toModel()
      val taskToUpdate = existingTask.toModel().copy(
        id = id,
        title = taskFromBody.title,
        status = taskFromBody.status,
      )
      repository.save(taskToUpdate).map(Task::fromModel)
    }
  }

  fun deleteById(id: Long) {
    return repository.deleteById(id)
  }

  private fun nextStatus(id: Long): Mono<Task> {
    return byId(id).flatMap { existingTask ->
      val taskToUpdate = when (existingTask) {
        is Task.Todo -> Task.Doing(existingTask.id, existingTask.title)
        is Task.Doing -> Task.Done(existingTask.id, existingTask.title)
        else -> Task.Done(existingTask.id, existingTask.title)
      }

      repository.save(taskToUpdate.toModel()).map(Task::fromModel)
    }
  }

  private fun previousStatus(id: Long): Mono<Task> {
    return byId(id).flatMap { existingTask ->
      val taskToUpdate = when (existingTask) {
        is Task.Done -> Task.Doing(existingTask.id, existingTask.title)
        is Task.Doing -> Task.Todo(existingTask.id, existingTask.title)
        else -> Task.Todo(existingTask.id, existingTask.title)
      }

      repository.save(taskToUpdate.toModel()).map(Task::fromModel)
    }
  }
}
