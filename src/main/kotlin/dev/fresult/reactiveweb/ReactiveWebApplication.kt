package dev.fresult.reactiveweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.stream.Collectors

@SpringBootApplication
class ReactiveWebApplication

fun main(args: Array<String>) {
  runApplication<ReactiveWebApplication>(*args)
}

@RestController
@RequestMapping("/reactive")
class ReactiveController {
  @GetMapping("/hello")
  fun getReactiveHello(): Mono<String> {
    return Flux.just("Hello", "Reactive", "World")
      .collect(Collectors.joining(" "))
  }
}

@RestController
@RequestMapping("/async")
class AsyncController {
  @GetMapping("/task")
  fun getAsyncTask(): Mono<String> = Mono.just("Async Task Completed!")
    .delayElement(Duration.ofSeconds(3))

  @GetMapping("/tasks")
  fun getAsyncTasks(): Flux<String> {
    return Flux.just("Task 1", "Task 2", "Task 3")
      .delayElements(Duration.ofSeconds(2))
  }
}
