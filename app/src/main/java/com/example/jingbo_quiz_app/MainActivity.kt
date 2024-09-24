package com.example.jingbo_quiz_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jingbo_quiz_app.ui.theme.Jingboquiz_appTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Jingboquiz_appTheme {
                QuizApp()
            }
        }
    }
}

@Composable
fun QuizApp() {
    val questions = listOf(
        Pair("What is 3 + 2?", "5"),
        Pair("What is 2^2?", "4"),
        Pair("What is 2 - 1?", "1"),
    )

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var quizComplete by remember { mutableStateOf(false) }
    var remainingAttempts by remember { mutableStateOf(3) }
    var correctAnswers by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            Box(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (!quizComplete) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = questions[currentQuestionIndex].first)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Attempts remaining: ${remainingAttempts}/3")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Enter your answer") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val isCorrect = userAnswer.trim() == questions[currentQuestionIndex].second

                        if (isCorrect) {
                            correctAnswers++
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("✅ Correct!")
                            }
                            currentQuestionIndex++
                            remainingAttempts = 3 // Reset attempts for next question
                        } else {
                            remainingAttempts--
                            if (remainingAttempts == 0) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("❌ Incorrect! Skipping to the next question.")
                                }
                                currentQuestionIndex++
                                remainingAttempts = 3 // Reset attempts for next question
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("❌ Incorrect! Try again.")
                                }
                            }
                        }

                        userAnswer = ""

                        if (currentQuestionIndex >= questions.size) {
                            quizComplete = true
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Quiz complete!")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Answer")
                }
            } else {
                Text(
                    text = "Quiz complete!\nYou answered $correctAnswers out of ${questions.size} questions correctly.",
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        currentQuestionIndex = 0
                        quizComplete = false
                        correctAnswers = 0
                        remainingAttempts = 3
                        userAnswer = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restart Quiz")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizAppPreview() {
    Jingboquiz_appTheme {
        QuizApp()
    }
}
