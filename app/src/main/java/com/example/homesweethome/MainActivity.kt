package com.example.homesweethome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.homesweethome.ui.theme.HomeSweetHomeTheme

class MainActivity : ComponentActivity() {

    // Initialize Room Database
    private val cleaningTaskDB by lazy {
        Room.databaseBuilder(
            applicationContext,
            CleaningTaskDB::class.java,
            "cleaning_task_database"
        ).build()
    }

    private val cleaningTaskViewModel by viewModels<CleaningTaskViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CleaningTaskViewModel(cleaningTaskDB.CleaningTaskDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeSweetHomeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenNew(viewModel = cleaningTaskViewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreenNew(viewModel: CleaningTaskViewModel) {
    val progress = viewModel.progress.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Text(
                text = "Home Sweet Home",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = viewModel.today,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ProgressBar(progress)
            if (viewModel.selectedOption.collectAsState().value == "ALL") {
                GridTaskList(
                    tasks = viewModel.incompleteTasks.collectAsState().value,
                    viewModel = viewModel)
            } else {
                GridTaskList(
                    tasks = viewModel.incompleteTasksToday.collectAsState().value,
                    viewModel = viewModel)
            }

        }
        FloatingActionBar(
            onResetClick = {
                viewModel.populateDatabase()
                viewModel.setOption("ALL")
            },
            onTodayClick = { viewModel.setOption("TODAY") }
        )
    }
}

@Composable
fun ProgressBar(progress: Float) {
    LinearProgressIndicator(
        progress,
        Modifier
            .fillMaxWidth()
            .height(12.dp),
        Color.White,
        Color.LightGray
    )
}

@Composable
fun GridTaskList(tasks: List<CleaningTask>, viewModel: CleaningTaskViewModel) {
    val itemsPerRow = 2
    val rows = tasks.chunked(itemsPerRow)

    LazyColumn(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        items(rows) { rowItems ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { task ->
                    TaskTile(task, viewModel)
                }
            }
        }
    }
}

@Composable
fun TaskTile(task: CleaningTask, viewModel: CleaningTaskViewModel) {
    val color = Color(0xFF3C8DBC)
    val lightShade = color.copy(alpha = 0.3f)
    val mediumShade = color.copy(alpha = 0.5f)
    val darkShade = color.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(150.dp)
            .background(color, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = task.taskName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { isChecked ->
                viewModel.updateTaskCompletionStatus(task, isChecked)
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun FloatingActionBar(onResetClick: () -> Unit, onTodayClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        IconButton(onClick = onResetClick) {
            Icon(
                Icons.Filled.Home,
                contentDescription = "Reset",
                tint = Color.White
            )
        }
        IconButton(onClick = onTodayClick) {
            Icon(
                Icons.Filled.Info,
                contentDescription = "Today",
                tint = Color.White
            )
        }
    }
}
