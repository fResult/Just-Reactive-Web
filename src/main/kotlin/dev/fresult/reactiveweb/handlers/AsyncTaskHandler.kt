package dev.fresult.reactiveweb.handlers

import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
class AsyncTaskHandler {
  suspend fun getTask(request: ServerRequest): ServerResponse {
    return Mono.just("Async Task Completed!")
      .delayElement(3.seconds.toJavaDuration())
      .flatMap(ServerResponse.ok()::bodyValue).awaitSingle()
  }

  suspend fun getTasks(request: ServerRequest): ServerResponse {
    val taskFlux = Flux.just("Task 1", "Task 2", "Task 3")
      .delayElements(1.seconds.toJavaDuration())
    return ServerResponse.ok().bodyAndAwait(taskFlux.asFlow())
  }
}
