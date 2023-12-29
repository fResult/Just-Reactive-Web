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
    TODO()
  }

  suspend fun byId(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    TODO()
  }

  suspend fun create(request: ServerRequest): ServerResponse {
    val bodyRequest = request.bodyToMono<TaskDTO>()
    TODO()
  }

  suspend fun updateById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    val bodyRequest = request.bodyToMono<TaskDTO>()
    TODO()
  }

  suspend fun updateStatusById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    val action = request.pathVariable("status-action")
    val updateStatusById = service.updateByAction(action)
    TODO()
  }

  suspend fun deleteById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    TODO()
  }
}
