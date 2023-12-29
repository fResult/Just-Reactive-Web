package dev.fresult.reactiveweb.entities

sealed class Task(open val id: Long, open val title: String) {
  data class Todo(override val id: Long, override val title: String) : Task(id, title)
  data class Doing(override val id: Long, override val title: String) : Task(id, title)
  data class Done(override val id: Long, override val title: String) : Task(id, title)

  fun toDTO(): TaskDTO {
    return when (this) {
      is Todo -> TaskDTO(id, title, TaskStatus.TODO)
      is Doing -> TaskDTO(id, title, TaskStatus.DOING)
      is Done -> TaskDTO(id, title, TaskStatus.DONE)
    }
  }

  fun toModel(): TaskModel {
    return when (this) {
      is Todo -> TaskModel(id, title, TaskStatus.TODO.name)
      is Doing -> TaskModel(id, title, TaskStatus.DOING.name)
      is Done -> TaskModel(id, title, TaskStatus.DONE.name)
    }
  }

  companion object {
    fun fromDTO(dto: TaskDTO): Task {
      return when (dto.status) {
        TaskStatus.TODO -> Todo(getId(dto.id), dto.title)
        TaskStatus.DOING -> Doing(getId(dto.id), dto.title)
        TaskStatus.DONE -> Done(getId(dto.id), dto.title)
      }
    }

    fun fromModel(model: TaskModel): Task {
      return when (model.status) {
        TaskStatus.TODO.name -> Todo(getId(model.id), model.title)
        TaskStatus.DOING.name -> Doing(getId(model.id), model.title)
        TaskStatus.DONE.name -> Done(getId(model.id), model.title)
        else -> throw Exception("Task Status may be wrong.")
      }
    }
  }
}

fun getId(id: Long? = null): Long = id.takeIf { it !== null } ?: (Math.random() * 10000).toLong()

open class BaseEntity<ID>(open val id: ID?)
data class TaskModel(
  override val id: Long? = null,
  val title: String,
  val status: String,
) : BaseEntity<Long>(id)

data class TaskDTO(
  val id: Long? = null,
  val title: String,
  val status: TaskStatus,
)

enum class TaskStatus {
  TODO, DOING, DONE
}
