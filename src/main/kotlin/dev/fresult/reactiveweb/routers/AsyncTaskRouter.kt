package dev.fresult.reactiveweb.routers

import dev.fresult.reactiveweb.handlers.AsyncTaskHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class AsyncTaskRouter(private val handler: AsyncTaskHandler) {
  @Bean
  fun routeDefinition(): RouterFunction<ServerResponse> {
    return route()
      .path("/async") { router ->
        router.GET("/task", handler::getTask)
        router.GET("/tasks", handler.getTasks)
      }
      .build()
  }
}
