package com.shub39.dharmik.bhagvad_gita.data

import com.shub39.dharmik.bhagvad_gita.domain.GitaVerse

fun BgVerseEntity.toGitaVerse(): GitaVerse {
    return GitaVerse(
        chapter = chapter,
        verse = verse,
        text = text,
        commentaries = commentaries,
        translations = translations
    )
}

fun GitaVerse.toBgVerseEntity(): BgVerseEntity {
    return  BgVerseEntity(
        chapter = chapter,
        verse = verse,
        text = text,
        commentaries = commentaries,
        translations = translations
    )
}