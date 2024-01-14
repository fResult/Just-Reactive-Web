package dev.fresult.reactiveweb.handlers

import dev.fresult.reactiveweb.TaskService
import dev.fresult.reactiveweb.entities.Task
import dev.fresult.reactiveweb.entities.TaskDTO
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class TaskHandler(val service: TaskService) {
  suspend fun all(request: ServerRequest): ServerResponse {
    val tasksResponse = service.all().map(Task::toDTO).asFlow()
    return ServerResponse.ok().bodyAndAwait(tasksResponse)
  }

  suspend fun byId(request: ServerRequest): ServerResponse {
    val id = idFromParam(request)
    return service.byId(id).flatMap(responseOkWithDTO)
      .onErrorResume(::notFoundOrServerError).awaitSingle()
  }

  suspend fun create(request: ServerRequest): ServerResponse {
    return request.bodyToMono<TaskDTO>().flatMap { body ->
      service.create(Task.fromDTO(body)).flatMap(responseCreatedWithDTO)
    }.switchIfEmpty(::badRequestErrorWhenBodyIsEmpty).awaitSingle()
  }

  suspend fun updateById(request: ServerRequest): ServerResponse {
    val id = idFromParam(request)
    return request.bodyToMono<TaskDTO>().flatMap { body ->
      service.updateById(id, Task.fromDTO(body)).flatMap(responseOkWithDTO)
        .onErrorResume(::notFoundOrServerError)
    }.switchIfEmpty(::badRequestErrorWhenBodyIsEmpty).awaitSingle()
  }

  suspend fun updateStatusById(request: ServerRequest): ServerResponse {
    val id = idFromParam(request)
    val action = request.pathVariable("status-action")
    val updateStatusById = service.updateByAction(action)

    return updateStatusById(id).flatMap(responseOkWithDTO)
      .onErrorResume(::notFoundOrServerError).awaitSingle()
  }

  suspend fun deleteById(request: ServerRequest): ServerResponse {
    val id = idFromParam(request)
    service.deleteById(id)
    return ServerResponse.noContent().buildAndAwait()
  }
}

private fun idFromParam(request: ServerRequest) = request.pathVariable("id").toLong()

private val responseOkWithDTO = responseOneWithDTO(HttpStatus.OK)
private val responseCreatedWithDTO = responseOneWithDTO(HttpStatus.CREATED)
private fun responseOneWithDTO(status: HttpStatus): (Task) -> Mono<ServerResponse> = { task ->
  ServerResponse.status(status).bodyValue(task.toDTO())
}

private fun badRequestErrorWhenBodyIsEmpty(): Mono<ServerResponse> =
  ServerResponse.badRequest().bodyValue("Request Body cannot be null")

private fun notFoundOrServerError(exception: Throwable): Mono<ServerResponse> = when (exception) {
  is NoSuchElementException -> ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(exception.message.orEmpty())
  else -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue("Some thing went wrong")
}
