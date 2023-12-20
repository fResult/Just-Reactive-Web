package dev.fresult.reactiveweb.routers

import dev.fresult.reactiveweb.handlers.StreamOperatorHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class StreamOperatorRouter(private val handler: StreamOperatorHandler) {
  @Bean
  fun streamOperatorRoutes() = coRouter {
"/stream-operators".nest {
  GET("/map", handler::handleMap)
  GET("/filter", handler::handleFilter)
  GET("/flat-map", handler::handleFlatMap)
  GET("/concat-map", handler::handleConcatMap)
  GET("/reduce", handler::handleReduce)
  GET("/zip", handler::handleZip)
  GET("/merge", handler::handleMerge)
}
  }
}