package dev.fresult.reactiveweb.routers

import dev.fresult.reactiveweb.handlers.AsyncTaskHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AsyncTaskRouter(private val handler: AsyncTaskHandler) {
  @Bean
  fun routeDefinition(): RouterFunction<ServerResponse> {
    return coRouter {
      "/async".nest {
        GET("/task", handler::getTask)
        GET("/tasks", handler::getTasks)
      }
    }
  }
}
