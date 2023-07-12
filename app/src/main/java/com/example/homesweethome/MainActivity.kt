package com.example.homesweethome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    MainScreen(viewModel = cleaningTaskViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HomeSweetHomeTheme {
        Greeting("Android")
    }
}

@Composable
fun MainScreen(viewModel: CleaningTaskViewModel) {
    val incompleteTasks by viewModel.selectedListToShow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Button to populate the database
        Button(
            onClick = { viewModel.populateDatabase();  viewModel.setListToShow("ALL") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "RESET")
        }

        // Button to show incomplete tasks for today
        Button(
            onClick = { viewModel.setListToShow("TODAY") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "TODAY")
        }

        // Display the list of incomplete tasks
        TaskList(tasks = viewModel.incompleteTasksToday.collectAsState().value)
    }
}

@Composable
fun TaskList(tasks: List<CleaningTask>) {
    LazyColumn {
        items(tasks) { task ->
            Text(text = task.taskName)
        }
    }
}
