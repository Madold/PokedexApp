package com.markusw.app.data.repository

import com.markusw.app.core.utils.Resource
import com.markusw.app.data.network.PokeApiService
import com.markusw.app.data.network.remote.responses.PokemonInfoResponse
import com.markusw.app.data.network.remote.responses.PokemonListResponse
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val api: PokeApiService
) {
    suspend fun getPokemons(limit: Int, offset: Int): Resource<PokemonListResponse> {
        return api.getPokemonList(limit, offset)
    }

    suspend fun getPokemon(name: String): Resource<PokemonInfoResponse> {
        return api.getPokemon(name)
    }
}