package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.DialoguesScreen
import com.example.ui.screens.PhrasesScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.theme.NihaoTalkTheme
import com.example.viewmodel.NihaoViewModel

object AppRoutes {
  const val PHRASES = "phrases"
  const val DIALOGUES = "dialogues"
  const val QUIZ = "quiz"
  const val BOOKMARKS = "bookmarks"
}

private data class NavDestinationItem(
  val route: String,
  val labelRes: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

private val navItems =
  listOf(
    NavDestinationItem(
      route = AppRoutes.PHRASES,
      labelRes = R.string.nav_phrases,
      selectedIcon = Icons.Filled.MenuBook,
      unselectedIcon = Icons.Outlined.MenuBook,
      testTag = "nav_phrases",
    ),
    NavDestinationItem(
      route = AppRoutes.DIALOGUES,
      labelRes = R.string.nav_dialogues,
      selectedIcon = Icons.Filled.Forum,
      unselectedIcon = Icons.Outlined.Forum,
      testTag = "nav_dialogues",
    ),
    NavDestinationItem(
      route = AppRoutes.QUIZ,
      labelRes = R.string.nav_quiz,
      selectedIcon = Icons.Filled.Quiz,
      unselectedIcon = Icons.Outlined.Quiz,
      testTag = "nav_quiz",
    ),
    NavDestinationItem(
      route = AppRoutes.BOOKMARKS,
      labelRes = R.string.nav_bookmarks,
      selectedIcon = Icons.Filled.Bookmark,
      unselectedIcon = Icons.Outlined.BookmarkBorder,
      testTag = "nav_bookmarks",
    ),
  )

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      NihaoTalkTheme {
        NihaoTalkApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NihaoTalkApp(viewModel: NihaoViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  val navigateToTab: (String) -> Unit = { route ->
    navController.navigate(route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  val savedCount = uiState.preferences.favoriteIds.size
  val isAudioPlaying = uiState.ttsState.activeUtteranceId != null

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useNavigationRail = maxWidth >= 600.dp

    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = MaterialTheme.colorScheme.background,
      contentWindowInsets = WindowInsets.safeDrawing,
      topBar = {
        CenterAlignedTopAppBar(
          title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "你好",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }
          },
          actions = {
            if (isAudioPlaying) {
              IconButton(
                onClick = { viewModel.stopAudio() },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("top_stop_audio"),
              ) {
                Icon(
                  imageVector = Icons.Filled.StopCircle,
                  contentDescription = "음성 재생 중지",
                  tint = MaterialTheme.colorScheme.primary,
                )
              }
            }
          },
          colors =
            TopAppBarDefaults.centerAlignedTopAppBarColors(
              containerColor = MaterialTheme.colorScheme.background
            ),
        )
      },
      bottomBar = {
        if (!useNavigationRail) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
          ) {
            navItems.forEach { item ->
              val selected =
                currentDestination?.hierarchy?.any { it.route == item.route } == true
              val label = stringResource(item.labelRes)
              NavigationBarItem(
                selected = selected,
                onClick = { navigateToTab(item.route) },
                modifier = Modifier.testTag(item.testTag),
                icon = {
                  if (item.route == AppRoutes.BOOKMARKS && savedCount > 0) {
                    BadgedBox(badge = { Badge { Text(savedCount.toString()) } }) {
                      Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = label,
                      )
                    }
                  } else {
                    Icon(
                      imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                      contentDescription = label,
                    )
                  }
                },
                label = { Text(label) },
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        if (useNavigationRail) {
          NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surface,
          ) {
            navItems.forEach { item ->
              val selected =
                currentDestination?.hierarchy?.any { it.route == item.route } == true
              val label = stringResource(item.labelRes)
              NavigationRailItem(
                selected = selected,
                onClick = { navigateToTab(item.route) },
                modifier = Modifier.testTag(item.testTag),
                icon = {
                  if (item.route == AppRoutes.BOOKMARKS && savedCount > 0) {
                    BadgedBox(badge = { Badge { Text(savedCount.toString()) } }) {
                      Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = label,
                      )
                    }
                  } else {
                    Icon(
                      imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                      contentDescription = label,
                    )
                  }
                },
                label = { Text(label) },
              )
            }
          }
        }

        NavHost(
          navController = navController,
          startDestination = AppRoutes.PHRASES,
          modifier = Modifier.weight(1f).fillMaxSize(),
        ) {
          composable(AppRoutes.PHRASES) {
            PhrasesScreen(
              uiState = uiState,
              onSearchQueryChange = viewModel::onSearchQueryChange,
              onSelectCategory = viewModel::onSelectCategory,
              onSpeakPhrase = viewModel::speakPhrase,
              onToggleFavorite = viewModel::toggleFavorite,
              onToggleLearned = viewModel::toggleLearned,
              onOpenTtsSettings = viewModel::openTtsSettings,
            )
          }
          composable(AppRoutes.DIALOGUES) {
            DialoguesScreen(
              uiState = uiState,
              onSelectDialogue = viewModel::selectDialogue,
              onSpeakLine = viewModel::speakText,
              onPlayFullDialogue = viewModel::playFullDialogue,
              onToggleHideLearnerLines = viewModel::toggleHideLearnerLines,
              onToggleRevealLine = viewModel::toggleRevealDialogueLine,
            )
          }
          composable(AppRoutes.QUIZ) {
            QuizScreen(
              uiState = uiState,
              onStartQuiz = viewModel::startQuiz,
              onAnswerOption = viewModel::answerQuizQuestion,
              onNextQuestion = viewModel::nextQuizQuestion,
              onSpeakPrompt = viewModel::speakText,
            )
          }
          composable(AppRoutes.BOOKMARKS) {
            BookmarksScreen(
              uiState = uiState,
              onSpeakPhrase = viewModel::speakPhrase,
              onSpeakTone = viewModel::speakText,
              onToggleFavorite = viewModel::toggleFavorite,
              onToggleLearned = viewModel::toggleLearned,
              onSetTtsSpeed = viewModel::setTtsSpeed,
              onSetShowPinyin = viewModel::setShowPinyin,
              onSetShowHangulHint = viewModel::setShowHangulHint,
            )
          }
        }
      }
    }
  }
}
