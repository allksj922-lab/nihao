package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nihao_prefs")

data class UserStudyPreferences(
  val favoriteIds: Set<String> = emptySet(),
  val learnedIds: Set<String> = emptySet(),
  val ttsSpeed: Float = 1.0f,
  val showPinyin: Boolean = true,
  val showHangulHint: Boolean = true,
)

class UserPreferencesRepository(private val context: Context) {

  private object Keys {
    val FAVORITE_IDS = stringSetPreferencesKey("favorite_phrase_ids")
    val LEARNED_IDS = stringSetPreferencesKey("learned_phrase_ids")
    val TTS_SPEED = floatPreferencesKey("tts_speed")
    val SHOW_PINYIN = booleanPreferencesKey("show_pinyin")
    val SHOW_HANGUL = booleanPreferencesKey("show_hangul_hint")
  }

  val preferencesFlow: Flow<UserStudyPreferences> =
    context.dataStore.data.map { prefs ->
      UserStudyPreferences(
        favoriteIds = prefs[Keys.FAVORITE_IDS] ?: emptySet(),
        learnedIds = prefs[Keys.LEARNED_IDS] ?: emptySet(),
        ttsSpeed = prefs[Keys.TTS_SPEED] ?: 1.0f,
        showPinyin = prefs[Keys.SHOW_PINYIN] ?: true,
        showHangulHint = prefs[Keys.SHOW_HANGUL] ?: true,
      )
    }

  suspend fun toggleFavorite(phraseId: String) {
    context.dataStore.edit { prefs ->
      val current = prefs[Keys.FAVORITE_IDS] ?: emptySet()
      prefs[Keys.FAVORITE_IDS] =
        if (phraseId in current) current - phraseId else current + phraseId
    }
  }

  suspend fun toggleLearned(phraseId: String) {
    context.dataStore.edit { prefs ->
      val current = prefs[Keys.LEARNED_IDS] ?: emptySet()
      prefs[Keys.LEARNED_IDS] =
        if (phraseId in current) current - phraseId else current + phraseId
    }
  }

  suspend fun setTtsSpeed(speed: Float) {
    context.dataStore.edit { prefs -> prefs[Keys.TTS_SPEED] = speed }
  }

  suspend fun setShowPinyin(show: Boolean) {
    context.dataStore.edit { prefs -> prefs[Keys.SHOW_PINYIN] = show }
  }

  suspend fun setShowHangulHint(show: Boolean) {
    context.dataStore.edit { prefs -> prefs[Keys.SHOW_HANGUL] = show }
  }
}
