package com.example.homesweethome

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CleaningTaskViewModel(private val cleaningTaskDao : CleaningTaskDao) : ViewModel() {

    private val TAG = "VIEW_MODEL"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /*
    * EVENT-HANDLER:
    * WHEN button RESET clicked THEN populate Room DB afresh
    *
    * WHEN button RESET clicked.. (click event)
    *   Create a 30-day schedule with 4 tasks each day (4 x 30 = 120).
    *   The task frequency is a mix of daily and weekly tasks,
    *   with daily tasks repeated every 3 days and weekly tasks repeated every 7 days.
    *   Take the list of daily tasks (Group A) and replicate each item 10 times.
    *   Then take the list of weekly tasks (Group B) and replicate each item 4 times.
    *   Merge both lists. You should now have a list of 120 items (10 x 10 + 5 x 4).
    *   Separately, generate a list of 30 dates, one for each day from today plus 1.
    *   Now replicate each date 4 times, so you have a list of 120. Now you have two lists.
    *   Assign a random date from the second list (120 dates = 30x4) to each item in the 1st list
    *   (120 tasks = 10X10 + 5x4) to prepare a list of 120 scheduled cleaning tasks to put in DB.
    * THEN populate Room DB afresh.. (DB ops)
    *   populateDatabase() wraps around Dao function (C)RUD
    *   generateCleaningTasks() implements business logic to schedule tasks
    *
    * Actors:
    *   groupATasks
    *   groupBTasks
    *
    * */
    private val groupATasks = listOf(
        "Make beds",
        "Wipe down kitchen countertops",
        "Wash dishes or load them in the dishwasher",
        "Sweep or vacuum the floors",
        "Mop kitchen and bathroom floors",
        "Wipe down bathroom sinks and countertops",
        "Clean the toilet",
        "Empty trash bins",
        "Wipe down mirrors",
        "Declutter common areas"
    )

    private val groupBTasks = listOf(
        "Dust furniture and surfaces",
        "Vacuum carpets and rugs",
        "Clean windows and window sills",
        "Clean kitchen appliances (microwave, oven, stovetop, etc.)",
        "Change bed sheets and pillowcases"
    )

    private fun generateCleaningTasks(): List<CleaningTask> {
        val allTasks = mutableListOf<CleaningTask>()
        val tasksList120: MutableList<String> = mutableListOf()

        // Replicate Group A tasks
        for (task in groupATasks) {
            repeat(10) {
                tasksList120.add(task)
            }
        }

        // Replicate Group B tasks
        for (task in groupBTasks) {
            repeat(4) {
                tasksList120.add(task)
            }
        }

        // Generate the list of dates
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1) // Start from tomorrow
        val dates = mutableListOf<Calendar>()

        repeat(30) {
            repeat(4) {
                val clonedCalendar = calendar.clone() as Calendar
                dates.add(clonedCalendar)
                clonedCalendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Assign random dates to tasks
        tasksList120.shuffle()
        for (i in tasksList120.indices) {
            val randomIndex = i % dates.size
            val assignedDate = dateFormat.format(dates[randomIndex].time) // Get the Date object from Calendar
            Log.d(TAG, "${i.toLong()}, ${tasksList120[i]}, ${assignedDate.toString()}")
            allTasks.add(CleaningTask(i.toLong(), tasksList120[i], "Group A", assignedDate.toString()))
        }

        return allTasks
    }

    fun populateDatabase() {
        Log.d(TAG, "RESET Clicked!")
        val cleaningTasks = generateCleaningTasks()
        viewModelScope.launch(Dispatchers.IO) {
            cleaningTaskDao.insertAll(cleaningTasks)
        }
    }

    /*
    * SUB-SYSTEM: KEEP TRACK OF PENDING TASKS
    * */
    private val _incompleteTasks = MutableStateFlow<List<CleaningTask>>(emptyList())
    val incompleteTasks: StateFlow<List<CleaningTask>> = _incompleteTasks.asStateFlow()

    init {
        observeIncompleteTasks()
        observeIncompleteTasksTodayChanges()
    }

    private fun observeIncompleteTasks() {
        viewModelScope.launch {
            cleaningTaskDao.getIncompleteTasks().collect { tasks ->
                _incompleteTasks.update { tasks }
            }
        }
    }

    /*
    * EVENT-HANDLER
    * WHEN button TODAY clicked
    * THEN fetch today's incomplete cleaning tasks to show in UI
    * */
    private val _selectedOption = MutableStateFlow<String>("ALL")
    val selectedOption: StateFlow<String> = _selectedOption
    fun setOption(option: String) {
        _selectedOption.value = option
    }

    private val _incompleteTasksToday = MutableStateFlow<List<CleaningTask>>(emptyList())
    val incompleteTasksToday: StateFlow<List<CleaningTask>> = _incompleteTasksToday.asStateFlow()

    fun observeIncompleteTasksTodayChanges() {
        val calendar = Calendar.getInstance()
        val today = dateFormat.format(calendar.time)
        viewModelScope.launch {
            cleaningTaskDao.getTasksByDate(today)
                .map { tasks ->
                    tasks.filter { !it.isCompleted }
                }
                .collect { filteredTasks ->
                    _incompleteTasksToday.update { filteredTasks }
                    Log.d(TAG,"Today's tasks ${incompleteTasksToday.value}")
                }
        }
        Log.d(TAG,"Today is ${today}")
    }

    /*
    * EVENT-HANDLER
    * WHEN a cleaning task is marked complete THEN update DB and refresh UI
    *
    *
    * WHEN a cleaning task is marked complete.. (checkbox is checked)
    *
    * THEN update DB and refresh UI
    * */
    fun updateTaskCompletionStatus(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val task = cleaningTaskDao.getTaskById(taskId)
                task?.let {
                    it.isCompleted = isCompleted
                    cleaningTaskDao.updateTask(it)
                }
            }
        }
    }

}






