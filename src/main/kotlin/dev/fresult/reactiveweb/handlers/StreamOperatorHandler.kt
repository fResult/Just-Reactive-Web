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

  suspend fun handleMapBk(request: ServerRequest): ServerResponse {
    val originalStream: Flux<Int> = Flux.just(1, 2, 3, 4, 5)
    val transformedStream: Flux<Int> = originalStream.map(::double)
    return ServerResponse.ok().bodyAndAwait(transformedStream.asFlow())
  }

  suspend fun handleFilter(request: ServerRequest): ServerResponse {
    val numberStream: Flux<Int> = Flux.range(1, 10)
    val evenNumbers: Flux<Int> = numberStream.filter(::isEven)
    return ServerResponse.ok().bodyAndAwait(evenNumbers.asFlow())
  }

  suspend fun handleFlatMap(request: ServerRequest): ServerResponse {
    val sourceStream: Flux<Flux<Int>> = Flux.just(
      Flux.just(1984, 1987, 1990),
      Flux.just(2002, 2005, 2008),
      Flux.just(2013, 2016, 2019),
    )
    val flattenedStream: Flux<Int> = sourceStream.flatMap(::delayHalfSecond)
    return ServerResponse.ok().bodyAndAwait(flattenedStream.asFlow())
  }

  suspend fun handleConcatMap(request: ServerRequest): ServerResponse {
    val sourceStream: Flux<Flux<Int>> = Flux.just(
      Flux.just(1984, 1987, 1990),
      Flux.just(2002, 2005, 2008),
      Flux.just(2013, 2016, 2019),
    )
    val concatenatedStream: Flux<Int> = sourceStream.concatMap(::delayHalfSecond)
    return ServerResponse.ok().bodyAndAwait(concatenatedStream.asFlow())
  }

  suspend fun handleReduce(request: ServerRequest): ServerResponse {
    val numbers: Flux<Int> = Flux.just(1, 2, 3, 4, 5)
    val total: Mono<Int> = numbers.reduce(::sum)
    return ServerResponse.ok().bodyValueAndAwait(total.awaitSingle())
  }

  suspend fun handleZip(request: ServerRequest): ServerResponse {
    val letterStream: Flux<String> = Flux.just("A", "B", "C")
    val numberStream: Flux<Int> = Flux.just(1, 2, 3)
    val zippedStream: Flux<Map<String, Int>> =
      Flux.zip(letterStream, numberStream, ::dictOf)
    return ServerResponse.ok().bodyAndAwait(zippedStream.asFlow())
  }

  suspend fun handleMerge(request: ServerRequest): ServerResponse {
    val numberStream: Flux<Int> = Flux.just(1, 2, 3)
    val animalStream: Flux<String> = Flux.just("Dog", "Elephant", "Fox")
    val mergedStream: Flux<*> = Flux.merge(numberStream, animalStream)
    return ServerResponse.ok().bodyAndAwait(mergedStream.asFlow())
  }

  private fun <T> delayHalfSecond(stream: Flux<T>): Flux<T> {
    return stream.delayElements(Duration.ofMillis(500))
  }
  private fun double(x: Int) = x * 2
  private fun isEven(x: Int) = x % 2 == 0
  private fun sum(x: Int, y: Int) = x + y
  private fun <K, V> dictOf(key: K, value: V) = key mapTo value

  private infix fun <K, V> K.mapTo(value: V): Map<K, V> {
    val key = this
    return mapOf(key to value)
  }
}
