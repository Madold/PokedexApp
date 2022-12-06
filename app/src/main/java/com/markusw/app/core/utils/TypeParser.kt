package com.markusw.app.core.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toLowerCase
import com.markusw.app.data.network.remote.responses.Stat
import com.markusw.app.data.network.remote.responses.Type
import com.markusw.app.ui.theme.*
import java.util.*

fun typeParser(type: Type): Color {
    return when(type.type.name.lowercase(Locale.ROOT)) {
        "electric" -> ElectricType
        "normal" -> NormalType
        "fire" -> FireType
        "grass" -> PlantType
        "water" -> WaterType
        "poison" -> PoisonType
        "flying" -> FlyingType
        "bug" -> BugType
        "dragon" -> DragonType
        "ghost" -> GhostType
        "dark" -> DarkType
        "steel" -> SteelType
        "fairy" -> FairyType
        else -> Color.Gray
    }
}

fun parseStatToAbbr(stat: Stat): String {
    return when(stat.stat.name.lowercase(Locale.ROOT)) {
        "hp" -> "HP"
        "attack" -> "Atk"
        "defense" -> "Def"
        "special-attack" -> "SpAtk"
        "special-defense" -> "SpDef"
        "speed" -> "Spd"
        else -> ""
    }
}

fun parseStatToColor(stat: Stat): Color {
    return when(stat.stat.name.lowercase(Locale.ROOT)) {
        "hp" -> PokemonRed
        "attack" -> PlantType
        "defense" -> GhostType
        "special-attack" -> PsychicType
        "special-defense" -> FairyType
        "speed" -> FlyingType
        else -> Color.White
    }
}