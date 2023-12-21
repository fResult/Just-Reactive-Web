package dev.fresult.reactiveweb.handlers

import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class StreamOperatorHandler {
  private fun toFahrenheit(celsius: Double): Double = celsius * 9 / 5 + 32
  suspend fun handleMap(request: ServerRequest): ServerResponse {
    val celsiusTemps: Flux<Double> = Flux.just(0.0, 10.0, 20.0, 30.0)
    val fahrenheitTemps: Flux<Double> = celsiusTemps.map(::toFahrenheit)
    return ServerResponse.ok().bodyAndAwait(fahrenheitTemps.asFlow())
  }

  private fun isEven(x: Int) = x % 2 == 0
  suspend fun handleFilter(request: ServerRequest): ServerResponse {
    val sensorDataStream: Flux<Int> = Flux.range(1, 10)
    val filteredEvenData: Flux<Int> = sensorDataStream.filter(::isEven)
    return ServerResponse.ok().bodyAndAwait(filteredEvenData.asFlow())
  }

  suspend fun handleFlatMap(request: ServerRequest): ServerResponse {
    val tasks: Flux<Flux<String>> = Flux.just(
      Flux.just("Task 1", "Task 2", "Task 3").delayElements(Duration.ofMillis(500)),
      Flux.just("Task 4", "Task 5").delayElements(Duration.ofMillis(200)),
      Flux.just("Task 6", "Task 7", "Task 8").delayElements(Duration.ofMillis(300))
    )
    val flattedTasks: Flux<String> = tasks.flatMap { it }
    return ServerResponse.ok().bodyAndAwait(flattedTasks.asFlow())
  }

  suspend fun handleConcatMap(request: ServerRequest): ServerResponse {
    val tasks: Flux<Flux<String>> = Flux.just(
      Flux.just("Task 1", "Task 2", "Task 3").delayElements(Duration.ofMillis(500)),
      Flux.just("Task 4", "Task 5").delayElements(Duration.ofMillis(200)),
      Flux.just("Task 6", "Task 7", "Task 8").delayElements(Duration.ofMillis(300))
    )
    val concatenatedTasks: Flux<String> = tasks.concatMap { it }
    return ServerResponse.ok().bodyAndAwait(concatenatedTasks.asFlow())
  }

  private fun sum(x: Double, y: Double): Double = x + y
  suspend fun handleReduce(request: ServerRequest): ServerResponse {
    val salesDataStream: Flux<Double> = Flux.just(120.0, 65.0, 210.0, 90.0)
    val totalRevenueMono: Mono<Double> = salesDataStream.reduce(::sum)
    val totalRevenue: Double = totalRevenueMono.awaitSingle()
    return ServerResponse.ok().bodyValueAndAwait(totalRevenue)
  }

  private fun <K, V> dictOf(key: K, value: V) = mapOf(key to value)
  suspend fun handleZip(request: ServerRequest): ServerResponse {
    val products: Flux<String> = Flux.just("Laptop", "Phone", "Tablet")
      .delayElements(Duration.ofMillis(300))
    val prices: Flux<Double> = Flux.just(1200.0, 800.0, 400.0)
      .delayElements(Duration.ofMillis(200))
    val productPricePairs: Flux<Map<String, Double>> = Flux.zip(products, prices, ::dictOf)
    return ServerResponse.ok().bodyAndAwait(productPricePairs.asFlow())
  }

  suspend fun handleMerge(request: ServerRequest): ServerResponse {
    val userData: Flux<String> = Flux.just("User 1", "User 2", "User 3")
      .delayElements(Duration.ofMillis(300))
    val productData: Flux<String> = Flux.just("Product A", "Product B", "Product C")
      .delayElements(Duration.ofMillis(200))
    val mergedData: Flux<String> = Flux.merge(userData, productData)
    return ServerResponse.ok().bodyAndAwait(mergedData.asFlow())
  }
}
