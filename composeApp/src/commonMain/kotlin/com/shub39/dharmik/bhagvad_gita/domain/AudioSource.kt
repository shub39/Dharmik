package com.shub39.dharmik.bhagvad_gita.domain

interface AudioSource {
    suspend fun getAudios(index: Int): List<Audios>
}