package dev.fresult.reactiveweb.repositories

import dev.fresult.reactiveweb.entities.BaseEntity
import dev.fresult.reactiveweb.entities.TaskModel
import dev.fresult.reactiveweb.entities.TaskStatus
import dev.fresult.reactiveweb.entities.getId
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*
import java.util.function.Predicate.not

@Component
class TaskRepository {
  private var tasks: Flux<TaskModel> = Flux.just(
    TaskModel(777, "Kill Tanos", TaskStatus.TODO.name),
    TaskModel(888, "Catch John Wick", TaskStatus.DOING.name),
    TaskModel(999, "Destroy The Matrix", TaskStatus.DONE.name),
  )

  fun findAll(): Flux<TaskModel> = tasks

  fun findById(id: Long): Mono<TaskModel> {
    return tasks.filter(isSameId(id)).switchIfEmpty {
      throw NoSuchElementException("Task ID $id not found")
    }.next()
  }

  fun save(task: TaskModel): Mono<TaskModel> {
    val taskIdToSave = task.id ?: getId()
    val taskToSave = if (Optional.ofNullable(task.id).isEmpty) task
    else task.copy(id = taskIdToSave, title = task.title, status = task.status)
    tasks = tasks.filter(not(isSameId(taskIdToSave))).concatWith(taskToSave.copy(id = taskIdToSave).toMono())

    return findById(taskIdToSave)
  }

  fun deleteById(id: Long) {
    tasks = tasks.filter(not(isSameId<TaskModel, Long>(id)))
  }
}

typealias PredicateFn<T> = (T) -> Boolean
private fun <T : BaseEntity<ID>, ID> isSameId(id: ID): PredicateFn<T> = { task ->
  task.id == id
}
