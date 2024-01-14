package dev.fresult.reactiveweb.handlers

import dev.fresult.reactiveweb.TaskService
import dev.fresult.reactiveweb.entities.Task
import dev.fresult.reactiveweb.entities.TaskDTO
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class TaskHandler(val service: TaskService) {
  suspend fun all(request: ServerRequest): ServerResponse {
    val tasksResponse = service.all().map(Task::toDTO).asFlow()
    return ServerResponse.ok().bodyAndAwait(tasksResponse)
  }

  suspend fun byId(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    return service.byId(id).flatMap {
      ServerResponse.ok().bodyValue(it.toDTO())
    }.onErrorResume { exception ->
      when (exception) {
        is NoSuchElementException -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(exception.message.orEmpty())
        else -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Some thing went wrong")
      }
    }.awaitSingle()
  }

  suspend fun create(request: ServerRequest): ServerResponse {
    return request.bodyToMono<TaskDTO>().flatMap { body ->
      service.create(Task.fromDTO(body)).flatMap {
        ServerResponse.status(HttpStatus.CREATED).bodyValue(it.toDTO())
      }
    }.switchIfEmpty {
      ServerResponse.badRequest().bodyValue("Request Body cannot be null")
    }.awaitSingle()
  }

  suspend fun updateById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    return request.bodyToMono<TaskDTO>().flatMap { body ->
      service.updateById(id, Task.fromDTO(body)).flatMap { updatedTask ->
        ServerResponse.ok().bodyValue(updatedTask.toDTO())
      }.onErrorResume { exception ->
        when (exception) {
          is NoSuchElementException ->
            ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(exception.message.orEmpty())

          else -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Some thing went wrong")
        }
      }
    }.switchIfEmpty {
      ServerResponse.badRequest().bodyValue("Request Body cannot be null")
    }.awaitSingle()
  }

  suspend fun updateStatusById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    val action = request.pathVariable("status-action")
    val updateStatusById = service.updateByAction(action)

    return updateStatusById(id).flatMap { updatedTask ->
      ServerResponse.ok().bodyValue(updatedTask.toDTO())
    }.onErrorResume { exception ->
      when (exception) {
        is NoSuchElementException -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(exception.message.orEmpty())
        else -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Some thing went wrong")
      }
    }.awaitSingle()
  }

  suspend fun deleteById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    service.deleteById(id)
    return ServerResponse.noContent().buildAndAwait()
  }
}
