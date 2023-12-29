package dev.fresult.reactiveweb

import dev.fresult.reactiveweb.entities.Task
import dev.fresult.reactiveweb.entities.TaskDAO
import dev.fresult.reactiveweb.entities.TaskDTO
import dev.fresult.reactiveweb.entities.TaskStatus
import dev.fresult.reactiveweb.repositories.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class TaskService(val repository: TaskRepository) {
  suspend fun all(): Flow<Task> {
    println("Service all")
    return repository.findAll().map(::toTask)
  }

  suspend fun byId(id: Long): TaskDTO? {
    println("Service byId $id")
//    val foundTask =  TaskDAO(333, "Eat Rat", "TODO")
    val foundTask = repository.findById(id)
    println("Found: $foundTask")
    return if (foundTask != null)
      toTask(foundTask).toDTO()
    else null
  }

  suspend fun create(task: Task): TaskDTO {
    val newTask = TaskDAO(null, task.title, TaskStatus.TODO.toString())

    val taskToCreate: TaskDAO = when (task) {
      is Task.Todo -> newTask.copy(status = TaskStatus.TODO.name)
      is Task.Doing -> newTask.copy(status = TaskStatus.DOING.name)
      is Task.Done -> newTask.copy(status = TaskStatus.DONE.name)
    }

    val createdTask = repository.save(taskToCreate)

//    return when (createdTask.status) {
//      TaskStatus.TODO.name -> createdTask.copyToTaskTodo()
//      TaskStatus.DOING.name -> createdTask.copyToTaskDoing()
//      TaskStatus.DONE.name -> createdTask.copyToTaskDone()
//      else -> throw Exception("Test")
//    }
    return toTask(createdTask).toDTO()
  }

  private fun toTask(task: TaskDAO): Task {
    println("In toTask ${TaskStatus.DOING}")
    println("To Task: $task : ${task.status}")
    return when (task.status) {
      TaskStatus.TODO.name -> task.copyToTaskTodo()
      TaskStatus.DOING.name -> task.copyToTaskDoing()
      TaskStatus.DONE.name -> task.copyToTaskDone()
      else -> throw Exception("Task Status may be wrong.")
    }
  }

}
