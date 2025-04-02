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
    val swamiRamsukhdas: String = "",

    @SerialName("Sri Harikrishnadas Goenka")
    val sriHarikrishnadasGoenka: String = "",

    @SerialName("Sri Anandgiri")
    val sriAnandgiri: String = "",

    @SerialName("Sri Dhanpati")
    val sriDhanpati: String = "",

    @SerialName("Sri Madhavacharya")
    val sriMadhavacharya: String = "",

    @SerialName("Sri Neelkanth")
    val sriNeelkanth: String = "",

    @SerialName("Sri Ramanuja")
    val sriRamanuja: String = "",

    @SerialName("Sri Sridhara Swami")
    val sriSridharaSwami: String = "",

    @SerialName("Sri Vedantadeshikacharya Venkatanatha")
    val sriVedantadeshikacharyaVenkatanatha: String = "",

    @SerialName("Swami Chinmayananda")
    val swamiChinmayananda: String = "",

    @SerialName("Sri Abhinavgupta")
    val sriAbhinavgupta: String = "",

    @SerialName("Sri Jayatritha")
    val sriJayatritha: String = "",

    @SerialName("Sri Madhusudan Saraswati")
    val sriMadhusudanSaraswati: String = "",

    @SerialName("Sri Purushottamji")
    val sriPurushottamji: String = "",

    @SerialName("Sri Shankaracharya")
    val sriShankaracharya: String = "",

    @SerialName("Sri Vallabhacharya")
    val sriVallabhacharya: String = "",

    @SerialName("Swami Sivananda")
    val swamiSivananda: String? = null,

    @SerialName("Swami Gambirananda")
    val swamiGambirananda: String? = null,

    @SerialName("Dr. S. Sankaranarayan")
    val drSSankaranarayan: String = "",

    @SerialName("Swami Adidevananda")
    val swamiAdidevananda: String = ""
)

@Serializable
data class Translations (
    @SerialName("sri harikrishnadas goenka")
    val sriHarikrishnadasGoenka: String = "",

    @SerialName("swami ramsukhdas")
    val swamiRamsukhdas: String = "",

    @SerialName("swami tejomayananda")
    val swamiTejomayananda: String = "",

    @SerialName("swami adidevananda")
    val swamiAdidevananda: String = "",

    @SerialName("swami gambirananda")
    val swamiGambirananda: String = "",

    @SerialName("swami sivananda")
    val swamiSivananda: String = "",

    @SerialName("dr. s. sankaranarayan")
    val drSSankaranarayan: String = "",

    @SerialName("shri purohit swami")
    val shriPurohitSwami: String = ""
)