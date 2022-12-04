package com.markusw.app.core.utils


sealed class PokemonGeneration(
    val name: String,
    val startPokemonId: Int,
    val endPokemonId: Int
) {
    object FirstGeneration : PokemonGeneration(
        name = "First Generation",
        startPokemonId = 0,
        endPokemonId = 151
    )

    object SecondGeneration : PokemonGeneration(
        name = "Second Generation",
        startPokemonId = 152,
        endPokemonId = 251
    )

    object ThirdGeneration : PokemonGeneration(
        name = "Third Generation",
        startPokemonId = 153,
        endPokemonId = 386
    )

    object FourthGeneration : PokemonGeneration(
        name = "Fourth Generation",
        startPokemonId = 387,
        endPokemonId = 493
    )

    object FifthGeneration: PokemonGeneration(
        name = "Fifth Generation",
        startPokemonId = 494,
        endPokemonId = 649
    )

    object SixthGeneration: PokemonGeneration(
        name = "Sixth Generation",
        startPokemonId = 650,
        endPokemonId = 721
    )

    object EmptyGeneration: PokemonGeneration(
        name = "",
        startPokemonId = -1,
        endPokemonId = -1
    )
}
