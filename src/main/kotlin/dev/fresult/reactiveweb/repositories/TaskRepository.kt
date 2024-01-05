package dev.fresult.reactiveweb.repositories

import dev.fresult.reactiveweb.entities.TaskDAO
import dev.fresult.reactiveweb.entities.TaskStatus
import dev.fresult.reactiveweb.entities.getId
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Component
class TaskRepository {
  private val tasks: Flux<TaskDAO> = Flux.just(
    TaskDAO(777, "Eat cat", TaskStatus.TODO.name),
    TaskDAO(888, "Eat dog", TaskStatus.DOING.name),
    TaskDAO(999, "Eat elephant", TaskStatus.DONE.name),
  )

  fun findAll(): Flux<TaskDAO> = tasks

  fun findById(id: Long): Mono<TaskDAO> {
    return tasks.filter { id == it.id }.next()
  }

  fun save(task: TaskDAO): Mono<TaskDAO> {
    tasks.concatWith(task.copy(id = getId()).toMono()).subscribe()
    return task.toMono()
  }

  fun updateTask(task: TaskDAO): Mono<TaskDAO> {
    save(task)
    return task.toMono()
  }

  fun deleteById(id: Long) {
    tasks
      .filter { it.id != id }
      .collectList()
      .map {
        Flux.empty<TaskDAO>()
      }.map { updatedTasks ->
        tasks.concatWith(updatedTasks.toFlux()) // Add the updated tasks back
      }.switchIfEmpty {
        Mono.error(NoSuchElementException("Task ID: $id not found."))
      }.subscribe()
  }

  fun updateStatusById(id: Long, newStatus: TaskStatus): Mono<TaskDAO> {

//    val existingTask = tasks.filter { it.id == id }.toMono()
//
//
//    return existingTask
//      .let {
//        val updatedTask = it!!.copy(status = newStatus.toString())
//        save(updatedTask)
//        updatedTask
//      }
    return tasks.filter { it.id == id }.next()
      .flatMap { existingTask ->
        val updatedTask = existingTask.copy(status = newStatus.toString())
        save(updatedTask).thenReturn(updatedTask)
      }.toMono()
  }
}
