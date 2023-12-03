package dev.fresult.reactiveweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootApplication
class ReactiveWebApplication

fun main(args: Array<String>) {
  runApplication<ReactiveWebApplication>(*args)
}

@Configuration
class AsyncRoutesConfiguration(private val handlers: AsyncTaskHandlers) {
  @Bean
  fun routeDefinition(): RouterFunction<ServerResponse> {
    return route()
      .path("/async") { router ->
        router.GET("/task", handlers::getTask)
        router.GET("/tasks", handlers.getTasks)
      }
      .build()
  }
}

@Component
class AsyncTaskHandlers {
  fun getTask(request: ServerRequest): Mono<ServerResponse> {
    return Mono.just("Async Task Completed!")
      .delayElement(3.seconds.toJavaDuration())
//      .flatMap { task -> ServerResponse.ok().bodyValue(task) }
      .flatMap(ServerResponse.ok()::bodyValue)
  }

  val getTasks = HandlerFunction { _ ->
    val taskFlux = Flux.just("Task 1", "Task 2", "Task 3")
      .delayElements(1.seconds.toJavaDuration())
    // return@HandlerFunction ServerResponse.ok().body(taskFlux, String::class.java)
    ServerResponse.ok().body(taskFlux, String::class.java)
  }
}
