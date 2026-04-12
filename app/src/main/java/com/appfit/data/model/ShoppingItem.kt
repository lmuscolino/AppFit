package com.appfit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: String = "",
    val unit: String = "",
    val category: ShoppingCategory,
    val isChecked: Boolean = false,
    val weekStartDate: LocalDate
)

enum class ShoppingCategory {
    PRODUCE, PROTEIN, DAIRY, GRAINS, PANTRY, OTHER;

    fun displayName(): String = when (this) {
        PRODUCE -> "Frutta e verdura"
        PROTEIN -> "Proteine"
        DAIRY -> "Latticini"
        GRAINS -> "Cereali e pasta"
        PANTRY -> "Dispensa"
        OTHER -> "Altro"
    }

    fun order(): Int = when (this) {
        PRODUCE -> 0
        PROTEIN -> 1
        DAIRY -> 2
        GRAINS -> 3
        PANTRY -> 4
        OTHER -> 5
    }
}
