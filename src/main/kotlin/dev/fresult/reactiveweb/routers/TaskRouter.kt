package dev.fresult.reactiveweb.routers

import dev.fresult.reactiveweb.handlers.TaskHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TaskRouter(private val handler: TaskHandler) {
  @Bean
  fun taskRoutes() = coRouter {
    "/tasks".nest {
      GET("", handler::all)
      GET("/{id}", handler::byId)
      POST("", handler::create)
      PUT("/{id}", handler::updateById)
      DELETE("/{id}", handler::deleteById)
    }
  }
}
