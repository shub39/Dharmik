package com.shub39.dharmik

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform