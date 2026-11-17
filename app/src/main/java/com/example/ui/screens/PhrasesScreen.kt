package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PhraseCategory
import com.example.model.PhraseItem
import com.example.ui.components.PhraseCard
import com.example.viewmodel.NihaoUiState

@Composable
fun PhrasesScreen(
  uiState: NihaoUiState,
  onSearchQueryChange: (String) -> Unit,
  onSelectCategory: (PhraseCategory?) -> Unit,
  onSpeakPhrase: (PhraseItem) -> Unit,
  onToggleFavorite: (String) -> Unit,
  onToggleLearned: (String) -> Unit,
  onOpenTtsSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val totalCount = uiState.allPhrases.size
  val learnedCount = uiState.preferences.learnedIds.size
  val progress = if (totalCount > 0) learnedCount.toFloat() / totalCount.toFloat() else 0f

  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 680.dp),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Hero Banner
      item(key = "hero_banner") {
        Card(
          modifier = Modifier.fillMaxWidth().height(184.dp),
          shape = MaterialTheme.shapes.large,
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            Image(
              painter = painterResource(id = R.drawable.img_hero_conversation),
              contentDescription = stringResource(R.string.cd_hero_banner),
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
            )
            Box(
              modifier =
                Modifier.fillMaxSize()
                  .background(
                    Brush.verticalGradient(
                      colors =
                        listOf(
                          Color.Black.copy(alpha = 0.20f),
                          Color.Black.copy(alpha = 0.72f),
                        )
                    )
                  )
            )
            Column(
              modifier = Modifier.fillMaxSize().padding(20.dp),
              verticalArrangement = Arrangement.SpaceBetween,
            ) {
              Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                shape = MaterialTheme.shapes.small,
              ) {
                Text(
                  text = stringResource(R.string.hero_badge),
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
              }

              Column {
                Text(
                  text = stringResource(R.string.hero_title),
                  style = MaterialTheme.typography.headlineMedium,
                  color = Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = stringResource(R.string.hero_subtitle),
                  style = MaterialTheme.typography.bodyMedium,
                  color = Color.White.copy(alpha = 0.90f),
                )
              }
            }
          }
        }
      }

      // TTS Warning Banner if device lacks Chinese voice data
      if (uiState.ttsState.isReady && !uiState.ttsState.isChineseAvailable) {
        item(key = "tts_warning") {
          Card(
            colors =
              CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
              ),
            shape = MaterialTheme.shapes.medium,
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = stringResource(R.string.tts_unavailable_title),
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = stringResource(R.string.tts_unavailable_desc),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
              }
              OutlinedButton(
                onClick = onOpenTtsSettings,
                modifier = Modifier.minimumInteractiveComponentSize().testTag("open_tts_settings"),
              ) {
                Icon(
                  imageVector = Icons.Filled.Settings,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.open_tts_settings))
              }
            }
          }
        }
      }

      // Daily Featured Expression + Study Progress
      item(key = "daily_featured") {
        val daily = uiState.dailyFeaturedPhrase
        val isDailySpeaking = uiState.ttsState.activeUtteranceId == "phrase_${daily.id}"
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = MaterialTheme.shapes.medium,
          colors =
            CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
            ),
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.AutoAwesome,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = stringResource(R.string.daily_pick_label),
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.secondary,
                )
              }

              Text(
                text = "학습 완료 $learnedCount / $totalCount",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
              color = MaterialTheme.colorScheme.secondary,
              trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = daily.hanzi,
                  style =
                    MaterialTheme.typography.headlineSmall.copy(
                      fontSize = 22.sp,
                      fontWeight = FontWeight.Bold,
                    ),
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "${daily.pinyin} · [${daily.hangulHint}]",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = daily.koreanMeaning,
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Button(
                onClick = { onSpeakPhrase(daily) },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("daily_speak_button"),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                  ),
              ) {
                Icon(
                  imageVector =
                    if (isDailySpeaking) Icons.Filled.GraphicEq else Icons.Filled.VolumeUp,
                  contentDescription = "오늘의 회화 발음 듣기",
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isDailySpeaking) "중지" else "듣기")
              }
            }
          }
        }
      }

      // Search Bar
      item(key = "search_bar") {
        OutlinedTextField(
          value = uiState.searchQuery,
          onValueChange = onSearchQueryChange,
          modifier = Modifier.fillMaxWidth().testTag("phrase_search_input"),
          placeholder = { Text(stringResource(R.string.search_placeholder)) },
          leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
          },
          trailingIcon = {
            if (uiState.searchQuery.isNotEmpty()) {
              IconButton(
                onClick = { onSearchQueryChange("") },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("clear_search_button"),
              ) {
                Icon(
                  imageVector = Icons.Filled.Clear,
                  contentDescription = stringResource(R.string.clear_search),
                )
              }
            }
          },
          singleLine = true,
          shape = MaterialTheme.shapes.medium,
          colors =
            OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
        )
      }

      // Category Filter Chips
      item(key = "category_chips") {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 2.dp),
        ) {
          item(key = "cat_all") {
            val selected = uiState.selectedCategory == null
            FilterChip(
              selected = selected,
              onClick = { onSelectCategory(null) },
              label = { Text(stringResource(R.string.all_categories)) },
              modifier = Modifier.testTag("category_chip_all"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
          }

          items(PhraseCategory.entries, key = { it.id }) { category ->
            val selected = uiState.selectedCategory == category
            FilterChip(
              selected = selected,
              onClick = { onSelectCategory(if (selected) null else category) },
              label = { Text(category.koreanName) },
              modifier = Modifier.testTag("category_chip_${category.id}"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
          }
        }
      }

      // Result Count Header
      item(key = "results_header") {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          val categoryTitle =
            uiState.selectedCategory?.koreanName ?: "상황별 핵심 회화"
          Text(
            text = categoryTitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Text(
            text = "${uiState.filteredPhrases.size}문장",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // Empty Search State or Phrase Cards
      if (uiState.filteredPhrases.isEmpty()) {
        item(key = "empty_search") {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp),
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "검색 결과가 없어요",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "다른 키워드나 카테고리를 선택해 보세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      } else {
        items(uiState.filteredPhrases, key = { it.id }) { phrase ->
          PhraseCard(
            phrase = phrase,
            isFavorite = phrase.id in uiState.preferences.favoriteIds,
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

      item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(16.dp)) }
    }
  }
}
