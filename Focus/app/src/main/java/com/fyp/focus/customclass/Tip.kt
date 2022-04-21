package com.fyp.focus.customclass

/**
 * Represents a Tip
 *
 * @param name the name/heading of the tip
 * @param imageResId the resource ID of the image for the tip
 * @param text the passage of text for the tip
 */
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