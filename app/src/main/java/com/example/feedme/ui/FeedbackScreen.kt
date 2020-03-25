package com.example.feedme.ui

import androidx.compose.Composable
import androidx.ui.unit.dp
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.Card
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import com.example.feedme.models.AnswerOption
import com.example.feedme.models.Question



@Composable
fun FeedbackScreen(questions: List<Question>) = MaterialTheme {

    Column {
        questions.forEach {
            QuestionCard(it)
        }

    }
}

@Composable
@Preview("Default colors")
fun FeedbackScreenPreview() {
    val questions = arrayListOf(
        Question(
            "", false, "How are you?",
            arrayListOf(AnswerOption("", "Good"), AnswerOption("", "Bad"))
        ),
        Question(
            "", false, "Very very very long question?",
            arrayListOf(AnswerOption("", "Good"), AnswerOption("", "Bad"))
        ),

        Question(
            "", false, "How is the weather?",
            arrayListOf(AnswerOption("", "Sunny"), AnswerOption("", "Cloudy"))
        )
    )
    FeedbackScreen(questions)
}

@Composable
fun QuestionCard(question: Question) {
        Card (shape = RoundedCornerShape(8.dp)){
//            Container(modifier = Height(180.dp) wraps Expanded) {
//            Column {
//                Text(text = question.value)
//                Row  {
//                    question.answerOptions.forEach {
//                        Button(text = it.value, modifier = Spacing(right = 5.dp))
//                    }
//                }
//            }
//        }
    }
}
