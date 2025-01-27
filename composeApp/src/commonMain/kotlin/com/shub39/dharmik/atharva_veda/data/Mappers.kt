package com.shub39.dharmik.atharva_veda.data

import com.shub39.dharmik.atharva_veda.domain.AvVerse

fun AvVerse.toAvEntity(): AvEntity {
    return AvEntity(
        veda = veda,
        samhita = samhita,
        kaanda = kaanda,
        sukta = sukta,
        text = text
    )
}

fun AvEntity.toAvVerse(): AvVerse {
    return AvVerse(
        veda = veda,
        samhita = samhita,
        kaanda = kaanda,
        sukta = sukta,
        text = text
    )
}