package dev.fresult.reactiveweb.handlers

import dev.fresult.reactiveweb.TaskService
import dev.fresult.reactiveweb.entities.Task
import dev.fresult.reactiveweb.entities.TaskDTO
import dev.fresult.reactiveweb.entities.TaskStatus
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class TaskHandler(val service: TaskService) {

  suspend fun all(request: ServerRequest): ServerResponse {
    val tasksResponse = service.all().map{ it.toDTO() }.asFlow()
    return ServerResponse.ok().bodyAndAwait(tasksResponse)
  }

  suspend fun byId(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    return service.byId(id)
      .flatMap {
        ServerResponse.ok().bodyValue(it)
      }.switchIfEmpty {
        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Task ID: $id not found.")
      }.awaitSingle()
  }

  suspend fun create(request: ServerRequest): ServerResponse {
    return request.bodyToMono<TaskDTO>()
      .flatMap { body ->
        val taskToCreate = Task.fromDTO(body)
        ServerResponse.ok().body(service.create(taskToCreate))
      }.awaitSingle()
  }

  suspend fun updateById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    TODO()
  }

  suspend fun updateStatusById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    TODO()
  }

  suspend fun deleteById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    TODO()
  }
}
