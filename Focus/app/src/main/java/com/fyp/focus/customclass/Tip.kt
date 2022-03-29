package com.fyp.focus.customclass

class Tip (
    val name: String,
    val imageResId: Int,
    val text: String
) {
    override fun toString(): String {
        return """
            name: $name
            imageResId: $imageResId
            text: $text
        """.trimIndent()
    }
}