package dev.fresult.reactiveweb

import dev.fresult.reactiveweb.entities.Task
import dev.fresult.reactiveweb.repositories.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TaskService(val repository: TaskRepository) {
  fun all(): Flux<Task> {
    TODO()
  }

  fun byId(id: Long): Mono<Task> {
    TODO()
  }

  fun create(task: Task): Mono<Task> {
    TODO()
  }

  /**
   * @param action "next" | "previous"
   * @return Mono<TaskDTO>
   */
  fun updateByAction(action: String): (Long) -> Mono<Task> =
    if (action == "next") ::nextStatus else ::previousStatus

  fun updateById(id: Long, task: Task): Mono<Task> {
    TODO()
  }

  fun deleteById(id: Long) {
    TODO()
  }

  private fun nextStatus(id: Long): Mono<Task> {
    TODO()
  }

  private fun previousStatus(id: Long): Mono<Task> {
    TODO()
  }

}
