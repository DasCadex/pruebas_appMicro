package com.example.proyecto_app.data.local.notificaciones

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    // Obtener todas las notificaciones para un usuario específico
    @Query("SELECT * FROM notificaciones WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    // Contar notificaciones no leídas (útil para un icono con badge)
    @Query("SELECT COUNT(*) FROM notificaciones WHERE userId = :userId AND isRead = 0")
    fun getUnreadCountForUser(userId: Long): Flow<Int>
}