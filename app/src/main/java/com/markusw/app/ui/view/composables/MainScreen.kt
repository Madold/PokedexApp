package com.markusw.app.ui.view.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.markusw.app.core.utils.PokemonGeneration
import com.markusw.app.core.utils.PokemonGeneration.*
import com.markusw.app.data.network.remote.responses.PokemonInfoResponse
import com.markusw.app.ui.theme.PokemonRed
import com.markusw.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val options = listOf(
            FirstGeneration,
            SecondGeneration,
            ThirdGeneration,
            FourthGeneration,
            FifthGeneration,
            SixthGeneration
        )

        val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
        val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
        val pokemonInfo = viewModel.pokemonInfo.collectAsState().value
        BottomSheetScaffold(
            sheetContent = {
                Spacer(modifier = Modifier.height(10.dp))
                Divider(
                    modifier = Modifier
                        .width(50.dp)
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .align(CenterHorizontally)
                )
                PokemonDetailSheet(pokemonInfo)
            },
            sheetBackgroundColor = MaterialTheme.colors.primary,
            sheetShape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp
            ),
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp
        ) {
            Column {
                ComboBox(
                    options = options,
                    placeholder = "Select a generation"
                )
                Box {
                    PokemonList(
                        sheetState = sheetState
                    )
                }
            }
        }
    }
}

@Composable
fun ComboBox(
    options: List<PokemonGeneration>,
    placeholder: String,
    viewModel: MainViewModel = hiltViewModel()
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedItem by rememberSaveable { mutableStateOf("") }
    var textFiledSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (isExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {
                selectedItem = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFiledSize = coordinates.size.toSize()
                },
            label = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colors.secondary
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            isExpanded = !isExpanded
                        }
                )
            },
            enabled = false,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colors.secondary
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colors.secondary
            )
        )
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            },
            modifier = Modifier
                .width(
                    with(LocalDensity.current) {
                        textFiledSize.width.toDp()
                    }
                )
                .background(
                    MaterialTheme.colors.primaryVariant
                )
        ) {
            options.forEach { generation ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem = generation.name
                        isExpanded = false
                        val selectedGeneration: PokemonGeneration? =
                            options.find { it.name == generation.name }
                        viewModel.onPokemonGenerationChanged(selectedGeneration!!)
                    }
                ) {
                    Text(
                        text = generation.name,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PokemonList(
    viewModel: MainViewModel = hiltViewModel(),
    sheetState: BottomSheetState
) {
    val pokemonList = viewModel.pokemonList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(columns = GridCells.Adaptive(180.dp), content = {
            items(pokemonList) { pokemonEntry ->
                PokemonCard(
                    spriteURL = pokemonEntry.spriteURL,
                    pokemonName = pokemonEntry.name,
                    sheetState = sheetState
                )
            }
        })
        if (isLoading) {
            LoadingScreen()
        }
    }
}

@Composable
fun LoadingScreen(
    backgroundColor: Color = MaterialTheme.colors.primary
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Center
    ) {
        CircularProgressIndicator(
            color = PokemonRed
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PokemonCard(
    spriteURL: String,
    pokemonName: String,
    viewModel: MainViewModel = hiltViewModel(),
    sheetState: BottomSheetState
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(0.5f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.primaryVariant)
            .clickable {
                coroutineScope.launch {
                    viewModel.onPokemonItemClicked(pokemonName)
                    sheetState.expand()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SubcomposeAsyncImage(
            model = spriteURL,
            contentDescription = pokemonName,
            loading = {
                CircularProgressIndicator(
                    color = PokemonRed
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Text(
            pokemonName.capitalize(Locale.ROOT),
            style = TextStyle(
                color = MaterialTheme.colors.secondary,
                fontSize = 18.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun PokemonDetailSheet(
    pokemonInfo: PokemonInfoResponse?,
    viewModel: MainViewModel = hiltViewModel()
) {

    val isLoading = viewModel.isLoading.collectAsState().value

    Box(
        modifier = Modifier.fillMaxHeight(0.85f),
        contentAlignment = Alignment.Center
    ) {

        if (isLoading) {
            LoadingScreen(
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    }
}