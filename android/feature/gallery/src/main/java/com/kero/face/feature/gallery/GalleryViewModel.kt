package com.kero.face.feature.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kero.face.feature.gallery.internal.MediaStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MediaStoreRepository(application.contentResolver)

    private val _state = MutableStateFlow(GalleryState())
    val state: StateFlow<GalleryState> = _state.asStateFlow()

    private val _effect = Channel<GalleryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var allImages = emptyList<com.kero.face.feature.gallery.internal.MediaImage>()

    fun dispatch(intent: GalleryIntent) {
        when (intent) {
            GalleryIntent.LoadMedia -> loadMedia()
            is GalleryIntent.PermissionResult -> {
                _state.update { it.copy(permissionGranted = intent.granted) }
                if (intent.granted) loadMedia()
            }
            is GalleryIntent.SelectAlbum -> {
                _state.update { it.copy(selectedAlbumId = intent.albumId, isAlbumDropdownExpanded = false) }
                filterPhotos(intent.albumId)
            }
            is GalleryIntent.SelectPhoto -> {
                val newUri = if (_state.value.selectedPhotoUri == intent.uri) null else intent.uri
                _state.update { it.copy(selectedPhotoUri = newUri) }
            }
            GalleryIntent.ToggleAlbumDropdown -> {
                _state.update { it.copy(isAlbumDropdownExpanded = !it.isAlbumDropdownExpanded) }
            }
            GalleryIntent.NavigateBack -> {
                viewModelScope.launch { _effect.send(GalleryEffect.NavigateBack) }
            }
            GalleryIntent.ConfirmSelection -> {
                val uri = _state.value.selectedPhotoUri ?: return
                viewModelScope.launch { _effect.send(GalleryEffect.NavigateWithPhoto(uri)) }
            }
        }
    }

    private fun loadMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            val images = repository.loadImages()
            allImages = images
            val albums = repository.groupByAlbum(images)
            _state.update {
                it.copy(
                    albums = albums,
                    photoUris = images.map { img -> img.uri },
                    isLoading = false,
                )
            }
        }
    }

    private fun filterPhotos(albumId: Long?) {
        val filtered = if (albumId == null) allImages
        else allImages.filter { it.bucketId == albumId }
        _state.update { it.copy(photoUris = filtered.map { img -> img.uri }) }
    }
}
