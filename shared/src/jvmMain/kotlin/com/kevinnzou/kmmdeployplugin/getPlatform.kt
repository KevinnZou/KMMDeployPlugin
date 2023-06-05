package com.kevinnzou.kmmdeployplugin

class JvmPlatform : Platform {
    override val name: String
        get() = "Jvm Platform"
}

actual fun getPlatform(): Platform {
    return JvmPlatform()
}