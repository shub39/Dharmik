package com.shub39.dharmik.bhagvad_gita.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitaFile (
    @SerialName("BhagavadGitaChapter")
    val gitaVerses: List<GitaVerse>
)

@Serializable
data class GitaVerse (
    val chapter: Long,
    val verse: Long,
    val text: String,
    val commentaries: Commentaries,
    val translations: Translations
)

@Serializable
data class Commentaries (
    @SerialName("Swami Ramsukhdas")
    val swamiRamsukhdas: String? = null,

    @SerialName("Sri Harikrishnadas Goenka")
    val sriHarikrishnadasGoenka: String? = null,

    @SerialName("Sri Anandgiri")
    val sriAnandgiri: String? = null,

    @SerialName("Sri Dhanpati")
    val sriDhanpati: String? = null,

    @SerialName("Sri Madhavacharya")
    val sriMadhavacharya: String? = null,

    @SerialName("Sri Neelkanth")
    val sriNeelkanth: String? = null,

    @SerialName("Sri Ramanuja")
    val sriRamanuja: String? = null,

    @SerialName("Sri Sridhara Swami")
    val sriSridharaSwami: String? = null,

    @SerialName("Sri Vedantadeshikacharya Venkatanatha")
    val sriVedantadeshikacharyaVenkatanatha: String? = null,

    @SerialName("Swami Chinmayananda")
    val swamiChinmayananda: String? = null,

    @SerialName("Sri Abhinavgupta")
    val sriAbhinavgupta: String? = null,

    @SerialName("Sri Jayatritha")
    val sriJayatritha: String? = null,

    @SerialName("Sri Madhusudan Saraswati")
    val sriMadhusudanSaraswati: String? = null,

    @SerialName("Sri Purushottamji")
    val sriPurushottamji: String? = null,

    @SerialName("Sri Shankaracharya")
    val sriShankaracharya: String? = null,

    @SerialName("Sri Vallabhacharya")
    val sriVallabhacharya: String? = null,

    @SerialName("Swami Sivananda")
    val swamiSivananda: String? = null,

    @SerialName("Swami Gambirananda")
    val swamiGambirananda: String? = null,

    @SerialName("Dr. S. Sankaranarayan")
    val drSSankaranarayan: String? = null,

    @SerialName("Swami Adidevananda")
    val swamiAdidevananda: String? = null
)

@Serializable
data class Translations (
    @SerialName("sri harikrishnadas goenka")
    val sriHarikrishnadasGoenka: String? = null,

    @SerialName("swami ramsukhdas")
    val swamiRamsukhdas: String? = null,

    @SerialName("swami tejomayananda")
    val swamiTejomayananda: String? = null,

    @SerialName("swami adidevananda")
    val swamiAdidevananda: String? = null,

    @SerialName("swami gambirananda")
    val swamiGambirananda: String? = null,

    @SerialName("swami sivananda")
    val swamiSivananda: String? = null,

    @SerialName("dr. s. sankaranarayan")
    val drSSankaranarayan: String? = null,

    @SerialName("shri purohit swami")
    val shriPurohitSwami: String? = null
)