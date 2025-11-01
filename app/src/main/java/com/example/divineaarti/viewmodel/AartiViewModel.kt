package com.example.divineaarti.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.divineaarti.data.AartiRepository
import com.example.divineaarti.model.Aarti
import com.example.divineaarti.model.AartiCategory
import com.example.divineaarti.player.AudioPlayerManager

class AartiViewModel : ViewModel() {

    private lateinit var audioPlayerManager: AudioPlayerManager

    private val _allAartis = mutableStateOf<List<Aarti>>(emptyList())

    private val _aartis = mutableStateOf<List<Aarti>>(emptyList())
    val aartis: State<List<Aarti>> = _aartis

    private val _favoriteAartis = mutableStateOf<List<Aarti>>(emptyList())
    val favoriteAartis: State<List<Aarti>> = _favoriteAartis

    private val _selectedCategory = mutableStateOf(AartiCategory.ALL)
    val selectedCategory: State<AartiCategory> = _selectedCategory

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _selectedAarti = mutableStateOf<Aarti?>(null)
    val selectedAarti: State<Aarti?> = _selectedAarti

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: State<Boolean> = _isDarkMode

    private val _currentScreen = mutableStateOf("home")
    val currentScreen: State<String> = _currentScreen

    init {
        loadAartis()
    }

    fun initializeAudioPlayer(context: Context) {
        audioPlayerManager = AudioPlayerManager(context)
    }

    private fun loadAartis() {
        _allAartis.value = AartiRepository.getAllAartis()
        _aartis.value = _allAartis.value
        updateFavorites()
    }

    fun selectCategory(category: AartiCategory) {
        _selectedCategory.value = category
        _aartis.value = AartiRepository.getAartisByCategory(category)
    }

    fun searchAartis(query: String) {
        _searchQuery.value = query
        _aartis.value = if (query.isEmpty()) {
            AartiRepository.getAartisByCategory(_selectedCategory.value)
        } else {
            AartiRepository.searchAartis(query)
        }
    }

    fun selectAarti(aarti: Aarti) {
        _selectedAarti.value = aarti
        _isPlaying.value = false
        if (::audioPlayerManager.isInitialized) {
            audioPlayerManager.loadAudio(aarti.audioUrl)
        }
    }

    fun togglePlayPause() {
        if (::audioPlayerManager.isInitialized) {
            audioPlayerManager.togglePlayPause()
            _isPlaying.value = audioPlayerManager.isPlaying.value
        }
    }

    fun resetAudio() {
        if (::audioPlayerManager.isInitialized) {
            audioPlayerManager.reset()
            _isPlaying.value = false
        }
    }

    fun replayAudio() {
        if (::audioPlayerManager.isInitialized) {
            audioPlayerManager.replay()
            _isPlaying.value = true
        }
    }

    fun toggleFavorite(aartiId: Int) {
        // Update in all lists
        _allAartis.value = _allAartis.value.map {
            if (it.id == aartiId) it.copy(isFavorite = !it.isFavorite)
            else it
        }

        _aartis.value = _aartis.value.map {
            if (it.id == aartiId) it.copy(isFavorite = !it.isFavorite)
            else it
        }

        // Update selected aarti if it's the one being toggled
        _selectedAarti.value?.let { selected ->
            if (selected.id == aartiId) {
                _selectedAarti.value = selected.copy(isFavorite = !selected.isFavorite)
            }
        }

        updateFavorites()
    }

    private fun updateFavorites() {
        _favoriteAartis.value = _allAartis.value.filter { it.isFavorite }
    }

    fun setCurrentScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun clearSelection() {
        _selectedAarti.value = null
        _isPlaying.value = false
        if (::audioPlayerManager.isInitialized) {
            audioPlayerManager.pause()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::audioPlayerManager.isInitialized) {
            audioPlayerManager.release()
        }
    }
}