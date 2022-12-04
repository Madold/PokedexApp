package com.markusw.app.data.network

import com.markusw.app.core.utils.Resource
import com.markusw.app.data.network.remote.responses.PokemonInfoResponse
import com.markusw.app.data.network.remote.responses.PokemonListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class PokeApiService @Inject constructor(
    private val api: PokeApiClient
) {

    suspend fun getPokemon(name: String): Resource<PokemonInfoResponse> {
        var response: Response<PokemonInfoResponse>? = null
        return try {
            withContext(Dispatchers.IO) {
                response = api.getPokemon(name)
                return@withContext Resource.Success(response!!.body()!!)
            }
        } catch (e: Exception) {
            Resource.Error(response?.code().toString())
        }
    }

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonListResponse> {
        var response: Response<PokemonListResponse>? = null

        return try {
            withContext(Dispatchers.IO) {
                response = api.getPokemonList(limit, offset)
                return@withContext Resource.Success(response!!.body()!!)
            }
        } catch (e: Exception) {
            Resource.Error(response?.code().toString())
        }

    }

}