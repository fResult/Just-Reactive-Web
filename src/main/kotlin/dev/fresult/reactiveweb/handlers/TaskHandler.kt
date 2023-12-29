package dev.fresult.reactiveweb.handlers

import dev.fresult.reactiveweb.TaskService
import dev.fresult.reactiveweb.entities.TaskDTO
import dev.fresult.reactiveweb.entities.TaskStatus
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.*
import reactor.kotlin.core.publisher.toMono

@Component
class TaskHandler(val service: TaskService/*, val client: WebClient*/, builder: WebClient.Builder) {
  private val client = builder.baseUrl("localhost").build()

  suspend fun all(request: ServerRequest): ServerResponse {
    return ServerResponse.ok().bodyAndAwait(service.all())
//    return ServerResponse.ok().bodyAndAwait(
//      client.get().uri("/tasks").retrieve().awaitBody<TaskDTO>().toMono().asFlow()
//    )
  }

  suspend fun byId(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    val foundTask = runBlocking { service.byId(id) }

//    return foundTask.toMono()
//      .flatMap {
//        ServerResponse.ok().bodyValue(it)
//      }
//      .switchIfEmpty { ServerResponse.notFound().build() }
//      .awaitSingle()
    return if (foundTask != null) {
      ServerResponse.ok().bodyValue(foundTask)
    } else {
      ServerResponse.notFound().build()
    }.awaitSingle()
  }

  suspend fun create(request: ServerRequest): ServerResponse {
    val bodyRequest = request.bodyToMono<TaskDTO>()
    val result = bodyRequest.map { body ->
      val taskToCreate = when (body.status) {
        TaskStatus.TODO -> body.copyToTaskTodo()
        TaskStatus.DOING -> body.copyToTaskDoing()
        TaskStatus.DONE -> body.copyToTaskDone()
      }
      val response = runBlocking { service.create(taskToCreate) }

      return@map response
    }

    return ServerResponse.ok().bodyAndAwait(result.asFlow())
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
