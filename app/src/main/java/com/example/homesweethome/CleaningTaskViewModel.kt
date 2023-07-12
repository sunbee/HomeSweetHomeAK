package com.example.homesweethome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class CleaningTaskViewModel(private val cleaningTaskDao : CleaningTaskDao) : ViewModel() {

    private val TAG = "VIEW_MODEL"

    /*
    * SUB-SYSTEM: Populate DB
    * Take the list of items in Group A and replicate each item 10 times.
    * Then take the list of items in Group B and replicate each item 4 times.
    * Merge both lists. You should now have a list of 120 items (10 x 10 + 5 x 4).
    * Separately, generate a list of 30 dates, one for each day from today plus 1.
    * Now replicate each date 4 times, so you have a list of 120. Now you have two lists.
    * Assign a random date from the second list (120 dates = 30x4) to each item in the 1st list
    * (120 tasks = 10X10 + 5x4) to prepare a list of 120 entities to put in DB.
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
            val assignedDate = dates[randomIndex].time // Get the Date object from Calendar
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
    }

    private fun observeIncompleteTasks() {
        viewModelScope.launch {
            cleaningTaskDao.getIncompleteTasks().collect { tasks ->
                _incompleteTasks.update { tasks }
            }
        }
    }


}






