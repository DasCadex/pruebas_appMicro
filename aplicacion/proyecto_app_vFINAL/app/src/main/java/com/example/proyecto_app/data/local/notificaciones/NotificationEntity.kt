package com.example.proyecto_app.data.local.notificaciones

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.proyecto_app.data.local.user.UserEntity

@Entity(
    tableName = "notificaciones",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"], // El usuario que RECIBE la notificación
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long, // ID del usuario que recibe la notificación (el autor del post)
    val adminName: String, // Nombre del admin que borró el post
    val message: String, // El motivo del borrado
    val publicationTitle: String, // Título del post borrado (para contexto)
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false // Para saber si el usuario ya la vio
)