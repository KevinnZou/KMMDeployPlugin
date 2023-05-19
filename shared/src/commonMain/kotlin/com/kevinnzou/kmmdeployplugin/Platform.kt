package com.kevinnzou.kmmdeployplugin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform