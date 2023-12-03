package dev.fresult.reactiveweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@SpringBootApplication
class ReactiveWebApplication

fun main(args: Array<String>) {
  runApplication<ReactiveWebApplication>(*args)
}

@Configuration
class AsyncRoutesConfiguration {
  @Bean
  fun routeDefinition(): RouterFunction<ServerResponse> {
    return route()
      .path("/async") { router ->
        router.GET("/task") { _ ->
          return@GET Mono.just("Async Task Completed!")
            .delayElement(Duration.ofSeconds(3))
            .flatMap { task -> ServerResponse.ok().bodyValue(task) }
        }
        router.GET("/tasks") { _ ->
          val taskFlux = Flux.just("Task 1", "Task 2", "Task 3")
            .delaySequence(1.seconds.toJavaDuration())
          return@GET ServerResponse.ok().body(taskFlux)
        }
      }
      .build()
  }
}
