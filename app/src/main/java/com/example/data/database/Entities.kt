package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey val id: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val personalityMode: String = "Futuristic"
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val hasImage: Boolean = false,
    val imageBase64: String? = null,
    val isImageResult: Boolean = false,
    val generatedImageUri: String? = null,
    val hasThinkingProcess: Boolean = false,
    val thinkingProcessText: String? = null
)
