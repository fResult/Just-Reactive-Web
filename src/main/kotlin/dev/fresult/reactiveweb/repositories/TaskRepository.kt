package dev.fresult.reactiveweb.repositories

import dev.fresult.reactiveweb.entities.TaskDAO
import dev.fresult.reactiveweb.entities.TaskStatus
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Component

@Component
class TaskRepository {
  private val tasks: MutableSharedFlow<TaskDAO> = MutableSharedFlow(replay = Int.MAX_VALUE)

  init {
    val initialData: List<TaskDAO> = listOf(
      TaskDAO(777, "Eat cat", TaskStatus.TODO.name),
      TaskDAO(888, "Eat dog", TaskStatus.DOING.name),
      TaskDAO(999, "Eat elephant", TaskStatus.DONE.name),
    )

    initialData.forEach {
      tasks.tryEmit(it)
    }
  }

  suspend fun findAll(): Flow<TaskDAO> = tasks.asSharedFlow()

  suspend fun findById(id: Long): TaskDAO? {
    return tasks.firstOrNull { id == it.id }
  }

  suspend fun save(task: TaskDAO): TaskDAO {
    tasks.emit(task)
    return task
  }

  suspend fun updateTask(task: TaskDAO): TaskDAO {
    save(task)
    return task
  }

  suspend fun deleteById(id: Long): Unit {
    tasks.emit(tasks.first { it.id == id }) // Emit a task to delete
  }

  suspend fun updateStatusById(id: Long, newStatus: TaskStatus): TaskDAO? {
    val existingTask = tasks.firstOrNull { it.id == id }

    return existingTask
      .takeIf { it != null }
      .let {
        val updatedTask = it!!.copy(status = newStatus.toString())
        save(updatedTask)
        updatedTask
      }
  }
}
