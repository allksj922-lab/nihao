package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DialogueLine
import com.example.model.DialogueScenario
import com.example.model.SpeakerRole
import com.example.viewmodel.NihaoUiState

@Composable
fun DialoguesScreen(
  uiState: NihaoUiState,
  onSelectDialogue: (String) -> Unit,
  onSpeakLine: (String, String) -> Unit,
  onPlayFullDialogue: (DialogueScenario) -> Unit,
  onToggleHideLearnerLines: () -> Unit,
  onToggleRevealLine: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val selectedScenario =
    uiState.dialogues.firstOrNull { it.id == uiState.selectedDialogueId }
      ?: uiState.dialogues.first()

  val scenarioPrefix = "${selectedScenario.id}_line_"
  val isSequencePlaying =
    uiState.ttsState.activeUtteranceId?.startsWith(scenarioPrefix) == true

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item(key = "dialogue_header") {
        Column {
          Text(
            text = "실전 상황 대화 연습",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "말풍선을 눌러 한 줄씩 듣거나 '내 대사 가리기'로 직접 말하기 연습을 해보세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // Scenario Picker Chips
      item(key = "scenario_picker") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(uiState.dialogues, key = { it.id }) { scenario ->
            val selected = scenario.id == selectedScenario.id
            FilterChip(
              selected = selected,
              onClick = { onSelectDialogue(scenario.id) },
              label = { Text(scenario.title) },
              modifier = Modifier.testTag("dialogue_chip_${scenario.id}"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
          }
        }
      }

      // Scenario Info & Controls Card
      item(key = "scenario_controls") {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
          colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Surface(
              color = MaterialTheme.colorScheme.secondaryContainer,
              shape = MaterialTheme.shapes.small,
            ) {
              Text(
                text = selectedScenario.category.koreanName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = selectedScenario.title,
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = selectedScenario.situationDescription,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Button(
                onClick = { onPlayFullDialogue(selectedScenario) },
                modifier =
                  Modifier.weight(1f)
                    .minimumInteractiveComponentSize()
                    .testTag("play_full_dialogue_button"),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor =
                      if (isSequencePlaying) {
                        MaterialTheme.colorScheme.tertiary
                      } else {
                        MaterialTheme.colorScheme.primary
                      }
                  ),
              ) {
                Icon(
                  imageVector =
                    if (isSequencePlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                  contentDescription = null,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isSequencePlaying) "대화 중지" else "전체 듣기")
              }

              OutlinedButton(
                onClick = onToggleHideLearnerLines,
                modifier =
                  Modifier.weight(1f)
                    .minimumInteractiveComponentSize()
                    .testTag("roleplay_hide_button"),
              ) {
                Icon(
                  imageVector =
                    if (uiState.hideLearnerLines) {
                      Icons.Filled.Visibility
                    } else {
                      Icons.Filled.VisibilityOff
                    },
                  contentDescription = null,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (uiState.hideLearnerLines) "모두 보기" else "내 대사 가리기")
              }
            }
          }
        }
      }

      // Chat Bubbles
      items(selectedScenario.lines, key = { it.id }) { line ->
        val utteranceId = "${scenarioPrefix}${line.id}"
        val isSpeaking = uiState.ttsState.activeUtteranceId == utteranceId
        val isHidden =
          uiState.hideLearnerLines &&
            line.role == SpeakerRole.LEARNER &&
            line.id !in uiState.revealedLineIds

        DialogueBubbleCard(
          line = line,
          isSpeaking = isSpeaking,
          isHidden = isHidden,
          showPinyin = uiState.preferences.showPinyin,
          showHangulHint = uiState.preferences.showHangulHint,
          onClick = {
            if (isHidden) {
              onToggleRevealLine(line.id)
            } else {
              onSpeakLine(utteranceId, line.hanzi)
            }
          },
          onRevealToggle = { onToggleRevealLine(line.id) },
        )
      }

      item(key = "dialogue_bottom_spacer") { Spacer(modifier = Modifier.height(16.dp)) }
    }
  }
}

@Composable
private fun DialogueBubbleCard(
  line: DialogueLine,
  isSpeaking: Boolean,
  isHidden: Boolean,
  showPinyin: Boolean,
  showHangulHint: Boolean,
  onClick: () -> Unit,
  onRevealToggle: () -> Unit,
) {
  val isLearner = line.role == SpeakerRole.LEARNER

  val bubbleColor by
    animateColorAsState(
      targetValue =
        when {
          isSpeaking -> MaterialTheme.colorScheme.primaryContainer
          isLearner -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
          else -> MaterialTheme.colorScheme.surface
        },
      label = "bubbleColor",
    )

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isLearner) Arrangement.End else Arrangement.Start,
  ) {
    Card(
      modifier =
        Modifier.fillMaxWidth(0.90f)
          .clip(MaterialTheme.shapes.medium)
          .clickable(onClick = onClick)
          .testTag("dialogue_bubble_${line.id}"),
      shape = MaterialTheme.shapes.medium,
      colors = CardDefaults.cardColors(containerColor = bubbleColor),
      border =
        BorderStroke(
          width = if (isSpeaking) 1.5.dp else 1.dp,
          color =
            if (isSpeaking) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            },
        ),
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "${line.speakerName} · ${line.role.label}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color =
              if (isLearner) {
                MaterialTheme.colorScheme.secondary
              } else {
                MaterialTheme.colorScheme.primary
              },
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            if (isHidden) {
              Text(
                text = "터치해서 확인",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
              )
            } else {
              Icon(
                imageVector = if (isSpeaking) Icons.Filled.GraphicEq else Icons.Filled.VolumeUp,
                contentDescription = "대사 듣기",
                tint =
                  if (isSpeaking) {
                    MaterialTheme.colorScheme.primary
                  } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                  },
                modifier = Modifier.size(20.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isHidden) {
          Text(
            text = "뜻: ${line.koreanMeaning}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "중국어로 먼저 말해 본 뒤 카드를 눌러 정답을 확인하세요!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        } else {
          Text(
            text = line.hanzi,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )

          if (showPinyin || showHangulHint) {
            Spacer(modifier = Modifier.height(4.dp))
            val hintText =
              buildString {
                if (showPinyin) append(line.pinyin)
                if (showPinyin && showHangulHint) append(" · ")
                if (showHangulHint) append("[${line.hangulHint}]")
              }
            Text(
              text = hintText,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.primary,
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = line.koreanMeaning,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable { onRevealToggle() },
          )
        }
      }
    }
  }
}
