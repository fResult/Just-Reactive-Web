package dev.fresult.reactiveweb.entities

import reactor.core.publisher.Sinks.One

sealed class Task(open val id: Long, open val title: String) {
  data class Todo(override val id: Long, override val title: String) : Task(id, title) {
    fun copyToTaskDoing() = Doing(id, title)
  }
  data class Doing(override val id: Long, override val title: String) : Task(id, title)
  data class Done(override val id: Long, override val title: String) : Task(id, title)

  fun toDTO(): TaskDTO {
    return when (this) {
      is Todo -> TaskDTO(id, title, TaskStatus.TODO)
      is Doing -> TaskDTO(id, title, TaskStatus.DOING)
      is Done -> TaskDTO(id, title, TaskStatus.DONE)
    }
  }
}

fun getId(id: Long? = null): Long = id.takeIf { it !== null } ?: (Math.random() * 10000).toLong()

data class TaskDAO(val id: Long? = null, val title: String, val status: String) {
  fun copyToTaskTodo(): Task.Todo {
    println("id: $id")
    return Task.Todo(getId(id), title)
  }
  fun copyToTaskDoing() = Task.Doing(getId(id), title)
  fun copyToTaskDone() = Task.Done(getId(id), title)
}

data class TaskDTO(
  val id: Long? = null,
  val title: String,
  val status: TaskStatus
) {
  fun copyToTaskTodo() = Task.Todo(getId(id), title)
  fun copyToTaskDoing() = Task.Doing(getId(id), title)
  fun copyToTaskDone() = Task.Done(getId(id), title)
}

enum class TaskStatus {
  TODO, DOING, DONE
}
