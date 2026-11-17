package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChineseCurriculumData
import com.example.data.UserPreferencesRepository
import com.example.data.UserStudyPreferences
import com.example.model.DialogueScenario
import com.example.model.PhraseCategory
import com.example.model.PhraseItem
import com.example.model.QuizMode
import com.example.model.QuizQuestion
import com.example.model.ToneExample
import com.example.tts.ChineseTtsManager
import com.example.tts.TtsState
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizUiState(
  val mode: QuizMode = QuizMode.LISTEN_AND_MATCH,
  val questions: List<QuizQuestion> = emptyList(),
  val currentIndex: Int = 0,
  val selectedOptionIndex: Int? = null,
  val score: Int = 0,
  val isFinished: Boolean = false,
) {
  val currentQuestion: QuizQuestion?
    get() = questions.getOrNull(currentIndex)
}

data class NihaoUiState(
  val searchQuery: String = "",
  val selectedCategory: PhraseCategory? = null,
  val allPhrases: List<PhraseItem> = ChineseCurriculumData.phrases,
  val filteredPhrases: List<PhraseItem> = ChineseCurriculumData.phrases,
  val dailyFeaturedPhrase: PhraseItem = ChineseCurriculumData.phrases.first(),
  val dialogues: List<DialogueScenario> = ChineseCurriculumData.dialogues,
  val selectedDialogueId: String = ChineseCurriculumData.dialogues.first().id,
  val hideLearnerLines: Boolean = false,
  val revealedLineIds: Set<String> = emptySet(),
  val tones: List<ToneExample> = ChineseCurriculumData.tones,
  val preferences: UserStudyPreferences = UserStudyPreferences(),
  val ttsState: TtsState = TtsState(),
  val quizState: QuizUiState = QuizUiState(),
)

private data class ScreenFilters(
  val searchQuery: String = "",
  val selectedCategory: PhraseCategory? = null,
  val selectedDialogueId: String = ChineseCurriculumData.dialogues.first().id,
  val hideLearnerLines: Boolean = false,
  val revealedLineIds: Set<String> = emptySet(),
)

class NihaoViewModel(application: Application) : AndroidViewModel(application) {

  private val prefsRepository = UserPreferencesRepository(application)
  private val ttsManager = ChineseTtsManager(application)

  private val filtersFlow = MutableStateFlow(ScreenFilters())
  private val quizFlow = MutableStateFlow(createInitialQuiz(QuizMode.LISTEN_AND_MATCH))

  private val dailyPhrase: PhraseItem = run {
    val featured = ChineseCurriculumData.phrases.filter { it.isDailyFeatured }
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    if (featured.isNotEmpty()) {
      featured[dayOfYear % featured.size]
    } else {
      ChineseCurriculumData.phrases.first()
    }
  }

  val uiState: StateFlow<NihaoUiState> =
    combine(
        filtersFlow,
        prefsRepository.preferencesFlow,
        ttsManager.state,
        quizFlow,
      ) { filters, prefs, tts, quiz ->
        val query = filters.searchQuery.trim()
        val filtered =
          ChineseCurriculumData.phrases.filter { item ->
            val matchesCategory =
              filters.selectedCategory == null || item.category == filters.selectedCategory
            val matchesQuery =
              query.isEmpty() ||
                item.hanzi.contains(query, ignoreCase = true) ||
                item.pinyin.contains(query, ignoreCase = true) ||
                item.hangulHint.contains(query, ignoreCase = true) ||
                item.koreanMeaning.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
          }

        NihaoUiState(
          searchQuery = filters.searchQuery,
          selectedCategory = filters.selectedCategory,
          allPhrases = ChineseCurriculumData.phrases,
          filteredPhrases = filtered,
          dailyFeaturedPhrase = dailyPhrase,
          dialogues = ChineseCurriculumData.dialogues,
          selectedDialogueId = filters.selectedDialogueId,
          hideLearnerLines = filters.hideLearnerLines,
          revealedLineIds = filters.revealedLineIds,
          tones = ChineseCurriculumData.tones,
          preferences = prefs,
          ttsState = tts,
          quizState = quiz,
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NihaoUiState(dailyFeaturedPhrase = dailyPhrase, quizState = quizFlow.value),
      )

  fun onSearchQueryChange(query: String) {
    filtersFlow.value = filtersFlow.value.copy(searchQuery = query)
  }

  fun onSelectCategory(category: PhraseCategory?) {
    filtersFlow.value = filtersFlow.value.copy(selectedCategory = category)
  }

  fun toggleFavorite(phraseId: String) {
    viewModelScope.launch { prefsRepository.toggleFavorite(phraseId) }
  }

  fun toggleLearned(phraseId: String) {
    viewModelScope.launch { prefsRepository.toggleLearned(phraseId) }
  }

  fun setTtsSpeed(speed: Float) {
    viewModelScope.launch { prefsRepository.setTtsSpeed(speed) }
  }

  fun setShowPinyin(show: Boolean) {
    viewModelScope.launch { prefsRepository.setShowPinyin(show) }
  }

  fun setShowHangulHint(show: Boolean) {
    viewModelScope.launch { prefsRepository.setShowHangulHint(show) }
  }

  fun speakText(utteranceId: String, text: String) {
    val currentActive = uiState.value.ttsState.activeUtteranceId
    if (currentActive == utteranceId) {
      ttsManager.stop()
    } else {
      ttsManager.speak(text = text, utteranceId = utteranceId, speed = uiState.value.preferences.ttsSpeed)
    }
  }

  fun speakPhrase(phrase: PhraseItem) {
    speakText(utteranceId = "phrase_${phrase.id}", text = phrase.hanzi)
  }

  fun playFullDialogue(scenario: DialogueScenario) {
    val activeId = uiState.value.ttsState.activeUtteranceId
    val scenarioPrefix = "${scenario.id}_line_"
    if (activeId != null && activeId.startsWith(scenarioPrefix)) {
      ttsManager.stop()
      return
    }
    val items = scenario.lines.map { line -> "${scenarioPrefix}${line.id}" to line.hanzi }
    ttsManager.speakSequence(items = items, speed = uiState.value.preferences.ttsSpeed)
  }

  fun stopAudio() {
    ttsManager.stop()
  }

  fun openTtsSettings() {
    ttsManager.openTtsSettings()
  }

  fun selectDialogue(scenarioId: String) {
    ttsManager.stop()
    filtersFlow.value =
      filtersFlow.value.copy(selectedDialogueId = scenarioId, revealedLineIds = emptySet())
  }

  fun toggleHideLearnerLines() {
    val next = !filtersFlow.value.hideLearnerLines
    filtersFlow.value =
      filtersFlow.value.copy(hideLearnerLines = next, revealedLineIds = emptySet())
  }

  fun toggleRevealDialogueLine(lineId: String) {
    val current = filtersFlow.value.revealedLineIds
    filtersFlow.value =
      filtersFlow.value.copy(
        revealedLineIds = if (lineId in current) current - lineId else current + lineId
      )
  }

  fun startQuiz(mode: QuizMode) {
    ttsManager.stop()
    val newQuiz = createInitialQuiz(mode)
    quizFlow.value = newQuiz
    if (mode == QuizMode.LISTEN_AND_MATCH) {
      newQuiz.currentQuestion?.let { q ->
        ttsManager.speak(
          text = q.promptPhrase.hanzi,
          utteranceId = "quiz_${q.promptPhrase.id}",
          speed = uiState.value.preferences.ttsSpeed,
        )
      }
    }
  }

  fun answerQuizQuestion(optionIndex: Int) {
    val current = quizFlow.value
    if (current.selectedOptionIndex != null || current.isFinished) return
    val question = current.currentQuestion ?: return
    val isCorrect = optionIndex == question.correctIndex
    quizFlow.value =
      current.copy(
        selectedOptionIndex = optionIndex,
        score = if (isCorrect) current.score + 1 else current.score,
      )
  }

  fun nextQuizQuestion() {
    val current = quizFlow.value
    val nextIndex = current.currentIndex + 1
    if (nextIndex >= current.questions.size) {
      ttsManager.stop()
      quizFlow.value = current.copy(isFinished = true)
    } else {
      val updated = current.copy(currentIndex = nextIndex, selectedOptionIndex = null)
      quizFlow.value = updated
      if (updated.mode == QuizMode.LISTEN_AND_MATCH) {
        updated.currentQuestion?.let { q ->
          ttsManager.speak(
            text = q.promptPhrase.hanzi,
            utteranceId = "quiz_${q.promptPhrase.id}",
            speed = uiState.value.preferences.ttsSpeed,
          )
        }
      }
    }
  }

  private fun createInitialQuiz(mode: QuizMode): QuizUiState {
    val pool = ChineseCurriculumData.phrases.shuffled()
    val selectedPrompts = pool.take(5)
    val questions =
      selectedPrompts.map { target ->
        val distractors =
          ChineseCurriculumData.phrases
            .filter { it.id != target.id }
            .shuffled()
            .take(3)
        val options = (distractors + target).shuffled()
        QuizQuestion(
          promptPhrase = target,
          options = options,
          correctIndex = options.indexOfFirst { it.id == target.id },
        )
      }
    return QuizUiState(mode = mode, questions = questions)
  }

  override fun onCleared() {
    super.onCleared()
    ttsManager.shutdown()
  }
}
