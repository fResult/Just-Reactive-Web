package dev.fresult.reactiveweb.handlers

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import kotlin.jvm.optionals.getOrNull

@Component
class FunctionalConstructHandler {
  suspend fun handlePathVariable(request: ServerRequest): ServerResponse {
    val name = request.pathVariable("name")
    val greeting = "Hello $name"
    return ServerResponse.ok().bodyValueAndAwait(greeting)
  }

  suspend fun handleDynamicResponse(request: ServerRequest): ServerResponse {
    val dynamicResponse = when (val param = request.queryParam("type").getOrNull()) {
      "greet" -> "Greetings, Functional Explorer!"
      "farewell" -> "Farewell, Functional Explorer!"
      null -> "Hello, Functional Explorer!"
      else -> "$param, Functional Explorer!"
    }

    return ServerResponse.ok().bodyValueAndAwait(dynamicResponse)
  }

  suspend fun handleConditionalRouting(request: ServerRequest): ServerResponse {
    val xRoleHeader = request.headers().header("X-Role")
    val isAdmin = xRoleHeader.contains("Admin")

    return if (isAdmin) ServerResponse.ok().bodyValueAndAwait("Welcome, Admin!")
      else ServerResponse.status(HttpStatus.FORBIDDEN).bodyValueAndAwait("Access Denied!")
  }
}
