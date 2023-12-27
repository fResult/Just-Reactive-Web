package dev.fresult.reactiveweb.handlers

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class TaskHandler {
  suspend fun all(request: ServerRequest): ServerResponse = TODO()

  suspend fun byId(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    TODO()
  }

  suspend fun create(request: ServerRequest): ServerResponse = TODO()

  suspend fun updateById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    TODO()
  }

  suspend fun deleteById(request: ServerRequest): ServerResponse {
    val id: Long = request.pathVariable("id").toLong()
    TODO()
  }
}
