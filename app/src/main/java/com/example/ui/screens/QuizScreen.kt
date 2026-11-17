package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuizMode
import com.example.viewmodel.NihaoUiState

@Composable
fun QuizScreen(
  uiState: NihaoUiState,
  onStartQuiz: (QuizMode) -> Unit,
  onAnswerOption: (Int) -> Unit,
  onNextQuestion: () -> Unit,
  onSpeakPrompt: (String, String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val quiz = uiState.quizState

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item(key = "quiz_header") {
        Column {
          Text(
            text = "중국어 회화 퀴즈",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "듣기와 뜻 맞추기로 배운 표현을 바로 점검해 보세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // Mode Selector
      item(key = "quiz_mode_selector") {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          QuizMode.entries.forEach { mode ->
            val selected = quiz.mode == mode
            FilterChip(
              selected = selected,
              onClick = { onStartQuiz(mode) },
              label = { Text(mode.title) },
              modifier = Modifier.weight(1f).testTag("quiz_mode_${mode.name}"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
          }
        }
      }

      if (quiz.isFinished) {
        item(key = "quiz_result") {
          val total = quiz.questions.size.coerceAtLeast(1)
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors =
              CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
              ),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(28.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(64.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(34.dp),
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "퀴즈 완료!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "$total 문제 중 ${quiz.score} 문제 정답",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text =
                  when {
                    quiz.score == total -> "완벽해요! 현지에서도 자신 있게 말할 수 있겠네요."
                    quiz.score >= 3 -> "아주 좋아요! 틀린 문장은 회화 탭에서 한 번 더 들어보세요."
                    else -> "좋은 시작이에요! 천천히 발음을 들으며 다시 도전해 봐요."
                  },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
              )
              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = { onStartQuiz(quiz.mode) },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("quiz_restart_button"),
              ) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("새 문제로 다시 풀기")
              }
            }
          }
        }
      } else {
        val currentQuestion = quiz.currentQuestion
        if (currentQuestion != null) {
          item(key = "quiz_progress") {
            val total = quiz.questions.size.coerceAtLeast(1)
            val progress = (quiz.currentIndex + 1).toFloat() / total.toFloat()
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  text = "문제 ${quiz.currentIndex + 1} / $total",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.primary,
                )
                Text(
                  text = "현재 점수 ${quiz.score}점",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.secondary,
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
              )
            }
          }

          // Prompt Card
          item(key = "quiz_prompt") {
            val prompt = currentQuestion.promptPhrase
            val utteranceId = "quiz_${prompt.id}"
            val isSpeaking = uiState.ttsState.activeUtteranceId == utteranceId
            val isAnswered = quiz.selectedOptionIndex != null

            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = MaterialTheme.shapes.large,
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
              ) {
                Text(
                  text = quiz.mode.description,
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (quiz.mode == QuizMode.LISTEN_AND_MATCH) {
                  Button(
                    onClick = { onSpeakPrompt(utteranceId, prompt.hanzi) },
                    modifier =
                      Modifier.minimumInteractiveComponentSize().testTag("quiz_listen_button"),
                  ) {
                    Icon(
                      imageVector =
                        if (isSpeaking) Icons.Filled.GraphicEq else Icons.Filled.VolumeUp,
                      contentDescription = "문제 발음 듣기",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isSpeaking) "재생 중..." else "중국어 발음 다시 듣기")
                  }

                  if (isAnswered) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                      text = prompt.hanzi,
                      style = MaterialTheme.typography.titleLarge,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                      text = "${prompt.pinyin} · [${prompt.hangulHint}]",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.primary,
                    )
                  }
                } else {
                  Text(
                    text = prompt.koreanMeaning,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                  )
                  if (isAnswered) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                      onClick = { onSpeakPrompt(utteranceId, prompt.hanzi) },
                      modifier =
                        Modifier.minimumInteractiveComponentSize()
                          .testTag("quiz_answer_speak_button"),
                    ) {
                      Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = null)
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("정답 발음 듣기")
                    }
                  }
                }
              }
            }
          }

          // Answer Options
          item(key = "quiz_options") {
            val isAnswered = quiz.selectedOptionIndex != null
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = quiz.selectedOptionIndex == index
                val isCorrectOption = index == currentQuestion.correctIndex

                val containerColor =
                  when {
                    !isAnswered -> MaterialTheme.colorScheme.surface
                    isCorrectOption -> MaterialTheme.colorScheme.secondaryContainer
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                  }

                val borderColor =
                  when {
                    !isAnswered -> MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                    isCorrectOption -> MaterialTheme.colorScheme.secondary
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                  }

                Card(
                  modifier =
                    Modifier.fillMaxWidth()
                      .clip(MaterialTheme.shapes.medium)
                      .clickable(enabled = !isAnswered) { onAnswerOption(index) }
                      .testTag("quiz_option_$index"),
                  shape = MaterialTheme.shapes.medium,
                  colors = CardDefaults.cardColors(containerColor = containerColor),
                  border = BorderStroke(1.5.dp, borderColor),
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      if (quiz.mode == QuizMode.LISTEN_AND_MATCH) {
                        Text(
                          text = option.koreanMeaning,
                          style = MaterialTheme.typography.titleMedium,
                          color = MaterialTheme.colorScheme.onSurface,
                        )
                      } else {
                        Text(
                          text = option.hanzi,
                          style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (uiState.preferences.showPinyin) {
                          Spacer(modifier = Modifier.height(2.dp))
                          Text(
                            text = option.pinyin,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                          )
                        }
                      }
                    }

                    if (isAnswered) {
                      if (isCorrectOption) {
                        Icon(
                          imageVector = Icons.Filled.CheckCircle,
                          contentDescription = "정답",
                          tint = MaterialTheme.colorScheme.secondary,
                        )
                      } else if (isSelected) {
                        Icon(
                          imageVector = Icons.Filled.Cancel,
                          contentDescription = "오답",
                          tint = MaterialTheme.colorScheme.primary,
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          if (quiz.selectedOptionIndex != null) {
            item(key = "quiz_next") {
              val isLast = quiz.currentIndex == quiz.questions.lastIndex
              Button(
                onClick = onNextQuestion,
                modifier =
                  Modifier.fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("quiz_next_button"),
              ) {
                Text(if (isLast) "결과 확인하기" else "다음 문제")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
              }
            }
          }
        }
      }

      item(key = "quiz_bottom_spacer") { Spacer(modifier = Modifier.height(16.dp)) }
    }
  }
}
