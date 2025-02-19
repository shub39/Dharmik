package com.shub39.dharmik.bhagvad_gita.domain

import kotlinx.coroutines.flow.Flow

interface BgRepo {
    suspend fun getChapter(index: Int): GitaFile

    fun getFavesFlow(): Flow<List<GitaVerse>>

    suspend fun setFave(verse: GitaVerse)

    suspend fun deleteFave(verse: GitaVerse)
}