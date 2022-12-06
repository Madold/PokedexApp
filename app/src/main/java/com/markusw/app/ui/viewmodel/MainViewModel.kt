package com.markusw.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markusw.app.core.utils.PokemonGeneration
import com.markusw.app.core.utils.Resource
import com.markusw.app.core.utils.Resource.*
import com.markusw.app.data.model.PokedexListEntry
import com.markusw.app.data.network.remote.responses.PokemonInfoResponse
import com.markusw.app.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PokemonRepository
) :ViewModel() {

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _pokemonList = MutableStateFlow<List<PokedexListEntry>>(listOf())
    val pokemonList = _pokemonList.asStateFlow()
    private var _pokemonInfo = MutableStateFlow<Resource<PokemonInfoResponse>?>(null)
    val pokemonInfo = _pokemonInfo.asStateFlow()

    private fun fetchPokemons(limit: Int, offset: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getPokemons(limit, offset)

            when(result) {
                is Success -> {
                    val pokedexEntries: List<PokedexListEntry>? = result.data?.results?.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }

                        val spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"

                        PokedexListEntry(
                            name = entry.name,
                            id = number.toInt(),
                            spriteURL = spriteUrl
                        )
                    }
                    _pokemonList.value = pokedexEntries!!
                }
                is Error -> {
                    //TODO
                }
            }
            _isLoading.value = false
        }
    }

    private fun fetchPokemonInfo(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getPokemon(name)
            _pokemonInfo.value = result
            _isLoading.value = false
        }
    }

    fun onPokemonItemClicked(name: String) {
        fetchPokemonInfo(name)
    }

    fun onPokemonGenerationChanged(generation: PokemonGeneration) {
        fetchPokemons(
            limit = generation.endPokemonId - generation.startPokemonId,
            offset = generation.startPokemonId
        )
    }

}