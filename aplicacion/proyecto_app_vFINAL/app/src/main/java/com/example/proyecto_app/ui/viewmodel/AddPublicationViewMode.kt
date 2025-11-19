package com.example.proyecto_app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto
import com.example.proyecto_app.data.local.remote.dto.PublicacionDto

import com.example.proyecto_app.data.repository.PublicationRepository
import kotlinx.coroutines.launch
// Imports para manejo de archivos (igual que antes)
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

data class AddPublicationUiState(
    val title: String = "",
    val description: String = "",
    val imageUri: Uri? = null,
    val selectedCategory: String = "Shooter",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class AddPublicationViewModel(
    private val repository: PublicationRepository
) : ViewModel() {

    var uiState by mutableStateOf(AddPublicationUiState())
        private set

    val categories = listOf("Shooter", "RPG", "Indie", "Noticias", "Retro")

    fun onTitleChange(title: String) { uiState = uiState.copy(title = title) }
    fun onDescriptionChange(desc: String) { uiState = uiState.copy(description = desc) }
    fun onCategoryChange(cat: String) { uiState = uiState.copy(selectedCategory = cat) }
    fun onImageSelected(uri: Uri) { uiState = uiState.copy(imageUri = uri) }

    fun savePublication(context: Context, author: LoginResponseDto) { // Recibe DTO de usuario
        if (uiState.isSaving || uiState.title.isBlank() || uiState.imageUri == null) return

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)

            // 1. Guardar imagen (igual que antes, localmente para obtener URI)
            // Nota: En una app real, aquí subirías la imagen a un servidor y obtendrías una URL.
            // Para este ejemplo, usamos la URI local, pero recuerda que otros usuarios no verán tu imagen local.
            val newImageUri = saveImageToInternalStorage(context, uiState.imageUri!!)

            if (newImageUri != null) {
                // 2. Crear DTO
                val newPublication = PublicacionDto(
                    id = 0, // El servidor asignará el ID
                    userId = author.usuarioId,
                    authorName = author.nombreUsuario,
                    title = uiState.title,
                    description = uiState.description,
                    category = uiState.selectedCategory,
                    imageUri = newImageUri.toString(), // Aquí iría la URL de red en producción
                    likes = 0,
                    status = "activo",
                    createDt = null // El servidor pone la fecha
                )

                // 3. Enviar a la API
                repository.createPublication(newPublication)
                uiState = AddPublicationUiState(saveSuccess = true)
            } else {
                uiState = uiState.copy(isSaving = false)
            }
        }
    }

    // ... (saveImageToInternalStorage y clearSuccessFlag se mantienen igual)
    private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "IMG_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, fileName)
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                Uri.fromFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun clearSuccessFlag() {
        uiState = AddPublicationUiState()
    }
}
