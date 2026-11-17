package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PhraseItem
import com.example.model.ToneExample
import com.example.ui.components.PhraseCard
import com.example.viewmodel.NihaoUiState

@Composable
fun BookmarksScreen(
  uiState: NihaoUiState,
  onSpeakPhrase: (PhraseItem) -> Unit,
  onSpeakTone: (String, String) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onToggleLearned: (String) -> Unit,
  onSetTtsSpeed: (Float) -> Unit,
  onSetShowPinyin: (Boolean) -> Unit,
  onSetShowHangulHint: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val savedPhrases =
    uiState.allPhrases.filter { it.id in uiState.preferences.favoriteIds }

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item(key = "bookmarks_header") {
        Column {
          Text(
            text = "내 회화 노트 & 설정",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "저장한 표현 복습, 발음 속도 조절 및 중국어 4성조 가이드를 확인하세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // Study Stats Row
      item(key = "study_stats") {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          StatSummaryCard(
            label = "저장한 문장",
            value = "${savedPhrases.size}개",
            modifier = Modifier.weight(1f),
          )
          StatSummaryCard(
            label = "학습 완료",
            value = "${uiState.preferences.learnedIds.size} / ${uiState.allPhrases.size}",
            modifier = Modifier.weight(1f),
          )
        }
      }

      // Learning & TTS Settings Card
      item(key = "settings_card") {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Speed,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
              )
              Text(
                text = "음성 속도 및 표기 설정",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val speeds =
              listOf(
                0.7f to stringResource(R.string.tts_speed_slow),
                1.0f to stringResource(R.string.tts_speed_normal),
                1.2f to stringResource(R.string.tts_speed_fast),
              )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              speeds.forEach { (speedValue, label) ->
                val selected = kotlin.math.abs(uiState.preferences.ttsSpeed - speedValue) < 0.05f
                FilterChip(
                  selected = selected,
                  onClick = { onSetTtsSpeed(speedValue) },
                  label = { Text(label) },
                  modifier = Modifier.weight(1f).testTag("speed_chip_$speedValue"),
                  colors =
                    FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                      selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "한어병음(Pinyin) 표시",
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = "끄면 한자 읽기 실력을 테스트할 수 있어요",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Switch(
                checked = uiState.preferences.showPinyin,
                onCheckedChange = onSetShowPinyin,
                modifier = Modifier.testTag("toggle_pinyin_switch"),
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "한글 발음 가이드 표시",
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = "입문자를 위한 근사 발음 표기 [니 하오]",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Switch(
                checked = uiState.preferences.showHangulHint,
                onCheckedChange = onSetShowHangulHint,
                modifier = Modifier.testTag("toggle_hangul_switch"),
              )
            }
          }
        }
      }

      // Saved Phrases Section
      item(key = "saved_section_title") {
        Text(
          text = "저장한 표현 (${savedPhrases.size})",
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground,
        )
      }

      if (savedPhrases.isEmpty()) {
        item(key = "empty_bookmarks") {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_empty_bookmarks),
                contentDescription = stringResource(R.string.cd_empty_bookmarks),
                modifier = Modifier.size(140.dp).clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
              )
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = stringResource(R.string.empty_bookmarks_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = stringResource(R.string.empty_bookmarks_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
              )
            }
          }
        }
      } else {
        items(savedPhrases, key = { it.id }) { phrase ->
          PhraseCard(
            phrase = phrase,
            isFavorite = true,
            isLearned = phrase.id in uiState.preferences.learnedIds,
            isSpeaking = uiState.ttsState.activeUtteranceId == "phrase_${phrase.id}",
            showPinyin = uiState.preferences.showPinyin,
            showHangulHint = uiState.preferences.showHangulHint,
            onSpeakClick = { onSpeakPhrase(phrase) },
            onFavoriteClick = { onToggleFavorite(phrase.id) },
            onLearnedClick = { onToggleLearned(phrase.id) },
          )
        }
      }

      // 4 Tones Interactive Guide
      item(key = "tones_title") {
        Column {
          Text(
            text = "중국어 4성조 발음 가이드",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "각 성조 카드를 누르면 'ma' 소리의 높낮이를 바로 들을 수 있어요.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      items(uiState.tones, key = { it.toneNumber }) { tone ->
        val utteranceId = "tone_${tone.toneNumber}"
        ToneGuideCard(
          tone = tone,
          isSpeaking = uiState.ttsState.activeUtteranceId == utteranceId,
          onClick = { onSpeakTone(utteranceId, tone.hanzi) },
        )
      }

      item(key = "bookmarks_bottom_spacer") { Spacer(modifier = Modifier.height(16.dp)) }
    }
  }
}

@Composable
private fun StatSummaryCard(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    shape = MaterialTheme.shapes.medium,
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
      ),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
      )
    }
  }
}

@Composable
private fun ToneGuideCard(
  tone: ToneExample,
  isSpeaking: Boolean,
  onClick: () -> Unit,
) {
  Card(
    modifier =
      Modifier.fillMaxWidth()
        .clip(MaterialTheme.shapes.medium)
        .clickable(onClick = onClick)
        .testTag("tone_card_${tone.toneNumber}"),
    shape = MaterialTheme.shapes.medium,
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (isSpeaking) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
          } else {
            MaterialTheme.colorScheme.surface
          }
      ),
    border =
      BorderStroke(
        width = 1.dp,
        color =
          if (isSpeaking) {
            MaterialTheme.colorScheme.primary
          } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
          },
      ),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.size(56.dp),
      ) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
            text = tone.hanzi,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
          )
          Text(
            text = tone.pinyin,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = tone.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.small,
          ) {
            Text(
              text = tone.pitchContour,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "뜻: ${tone.meaning} · ${tone.description}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Icon(
        imageVector = if (isSpeaking) Icons.Filled.GraphicEq else Icons.Filled.VolumeUp,
        contentDescription = "${tone.name} 발음 듣기",
        tint =
          if (isSpeaking) {
            MaterialTheme.colorScheme.primary
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant
          },
      )
    }
  }
}
