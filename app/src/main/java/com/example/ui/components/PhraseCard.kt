package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PhraseItem

@Composable
fun PhraseCard(
  phrase: PhraseItem,
  isFavorite: Boolean,
  isLearned: Boolean,
  isSpeaking: Boolean,
  showPinyin: Boolean,
  showHangulHint: Boolean,
  onSpeakClick: () -> Unit,
  onFavoriteClick: () -> Unit,
  onLearnedClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var isTipExpanded by rememberSaveable(phrase.id) { mutableStateOf(false) }

  val containerColor by
    animateColorAsState(
      targetValue =
        if (isSpeaking) {
          MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
          MaterialTheme.colorScheme.surface
        },
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "phraseCardColor",
    )

  val borderColor by
    animateColorAsState(
      targetValue =
        when {
          isSpeaking -> MaterialTheme.colorScheme.primary
          isLearned -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)
          else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
        },
      label = "phraseBorderColor",
    )

  Card(
    modifier =
      modifier
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.medium)
        .clickable { isTipExpanded = !isTipExpanded }
        .testTag("phrase_card_${phrase.id}"),
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(1.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isSpeaking) 3.dp else 1.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = MaterialTheme.shapes.small,
        ) {
          Text(
            text = phrase.category.koreanName,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onLearnedClick,
            modifier =
              Modifier.minimumInteractiveComponentSize().testTag("learned_button_${phrase.id}"),
          ) {
            Icon(
              imageVector =
                if (isLearned) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
              contentDescription = if (isLearned) "학습 완료 해제" else "학습 완료 표시",
              tint =
                if (isLearned) {
                  MaterialTheme.colorScheme.secondary
                } else {
                  MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                },
            )
          }

          IconButton(
            onClick = onFavoriteClick,
            modifier =
              Modifier.minimumInteractiveComponentSize().testTag("favorite_button_${phrase.id}"),
          ) {
            Icon(
              imageVector =
                if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isFavorite) "저장 해제" else "저장함에 추가",
              tint =
                if (isFavorite) {
                  MaterialTheme.colorScheme.tertiary
                } else {
                  MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                },
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          FilledTonalIconButton(
            onClick = onSpeakClick,
            modifier =
              Modifier.size(48.dp)
                .clip(CircleShape)
                .testTag("speak_button_${phrase.id}"),
            colors =
              IconButtonDefaults.filledTonalIconButtonColors(
                containerColor =
                  if (isSpeaking) {
                    MaterialTheme.colorScheme.primary
                  } else {
                    MaterialTheme.colorScheme.primaryContainer
                  },
                contentColor =
                  if (isSpeaking) {
                    MaterialTheme.colorScheme.onPrimary
                  } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                  },
              ),
          ) {
            Icon(
              imageVector = if (isSpeaking) Icons.Filled.GraphicEq else Icons.Filled.VolumeUp,
              contentDescription = if (isSpeaking) "발음 재생 중지" else "중국어 발음 듣기",
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = phrase.hanzi,
        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp, lineHeight = 30.sp),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
      )

      if (showPinyin || showHangulHint) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (showPinyin) {
            Text(
              text = phrase.pinyin,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary,
            )
          }
          if (showPinyin && showHangulHint) {
            Text(
              text = "•",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          if (showHangulHint) {
            Text(
              text = "[${phrase.hangulHint}]",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.secondary,
              fontWeight = FontWeight.Medium,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = phrase.koreanMeaning,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.weight(1f),
        )
        Icon(
          imageVector = if (isTipExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
          contentDescription = if (isTipExpanded) "설명 접기" else "회화 팁 보기",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      AnimatedVisibility(visible = isTipExpanded) {
        Surface(
          modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = MaterialTheme.shapes.small,
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.Lightbulb,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.tertiary,
              modifier = Modifier.size(18.dp).padding(top = 2.dp),
            )
            Text(
              text = phrase.tip,
              style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }
    }
  }
}
