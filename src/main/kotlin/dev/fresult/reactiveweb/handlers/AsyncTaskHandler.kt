package dev.fresult.reactiveweb.handlers

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
class AsyncTaskHandler() {
  fun getTask(request: ServerRequest): Mono<ServerResponse> {
    return Mono.just("Async Task Completed!")
      .delayElement(3.seconds.toJavaDuration())
      .flatMap(ServerResponse.ok()::bodyValue)
  }

  val getTasks = { _: ServerRequest ->
    val taskFlux = Flux.just("Task 1", "Task 2", "Task 3")
      .delayElements(1.seconds.toJavaDuration())
    ServerResponse.ok().body(taskFlux)
  }
}
