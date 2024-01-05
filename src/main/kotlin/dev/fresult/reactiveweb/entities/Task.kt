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

  fun toDAO(): TaskDAO {
    return when (this) {
      is Todo -> TaskDAO(id, title, TaskStatus.TODO.name)
      is Doing -> TaskDAO(id, title, TaskStatus.DOING.name)
      is Done -> TaskDAO(id, title, TaskStatus.DONE.name)
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

    fun fromDAO(dao: TaskDAO): Task {
      return when (dao.status) {
        TaskStatus.TODO.name -> Todo(getId(dao.id), dao.title)
        TaskStatus.DOING.name -> Doing(getId(dao.id), dao.title)
        TaskStatus.DONE.name -> Done(getId(dao.id), dao.title)
        else -> throw Exception("Task Status may be wrong.")
      }
    }
  }
}

fun getId(id: Long? = null): Long = id.takeIf { it !== null } ?: (Math.random() * 10000).toLong()

data class TaskDAO(
  val id: Long? = null,
  val title: String,
  val status: String,
)

data class TaskDTO(
  val id: Long? = null,
  val title: String,
  val status: TaskStatus,
)

enum class TaskStatus {
  TODO, DOING, DONE
}
