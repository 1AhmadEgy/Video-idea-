package com.example.domain.model

data class Idea(
    val id: String,
    val text: String,
    val tag: String = "Shorts",
    val script: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
