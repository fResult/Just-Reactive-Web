package dev.fresult.reactiveweb.routers

import dev.fresult.reactiveweb.handlers.FunctionalConstructHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class FunctionalConstructRouter(private val handler: FunctionalConstructHandler) {
  @Bean
  fun functionalConstructRoutes() = coRouter {
    "/func-constructs".nest {
      GET("/greetings", handler::handleDynamicResponse)
      GET("/conditionals", handler::handleConditionalRouting)
      GET("/{name}", handler::handlePathVariable)
    }
  }
}
