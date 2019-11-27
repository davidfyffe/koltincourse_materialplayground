package com.lm.ah

import org.junit.Test

class Enums {

    enum class Color(val rgb: Int) {
        RED(0xFF0000),
        GREEN(0x00FF00),
        BLUE(0x0000FF)
    }

    @Test
    fun tryEnum() {
        fun main() {
            val valueOf = Color.valueOf("RED")
            val allCols = Color.values()

        }
    }
}