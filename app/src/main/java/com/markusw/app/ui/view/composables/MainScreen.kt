package com.markusw.app.ui.view.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.markusw.app.core.utils.*
import com.markusw.app.core.utils.PokemonGeneration.*
import com.markusw.app.data.network.remote.responses.PokemonInfoResponse
import com.markusw.app.ui.theme.PokemonRed
import com.markusw.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavController
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
                PokemonDetailSheet()
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
    viewModel: MainViewModel = hiltViewModel()
) {
    val isLoading = viewModel.isLoading.collectAsState().value
    val pokemonInfo = viewModel.pokemonInfo.collectAsState().value
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxHeight(0.95f),
        contentAlignment = Center
    ) {

        if (pokemonInfo is Resource.Success) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .verticalScroll(
                        state = scrollState
                    )
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${pokemonInfo.data!!.name.capitalize(Locale.ROOT)} #${pokemonInfo.data.id}",
                    style = TextStyle(
                        color = MaterialTheme.colors.secondary,
                        fontSize = 32.sp
                    )
                )
                SubcomposeAsyncImage(
                    model = pokemonInfo.data!!.sprites.front_default,
                    contentDescription = pokemonInfo.data.name,
                    loading = {
                        CircularProgressIndicator(
                            color = PokemonRed
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .aspectRatio(1f)
                )
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Types:",
                        modifier = Modifier
                            .align(CenterStart)
                            .offset(x = 10.dp),
                        style = TextStyle(
                            color = MaterialTheme.colors.secondary,
                            fontSize = 22.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    pokemonInfo.data.types.forEach { type ->
                        Box(
                            contentAlignment = Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .clip(CircleShape)
                                .background(typeParser(type))
                                .height(35.dp)
                        ) {
                            Text(
                                text = type.type.name.capitalize(Locale.ROOT),
                                style = TextStyle(
                                    color = MaterialTheme.colors.secondary,
                                    fontSize = 22.sp
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Base stats:",
                        modifier = Modifier
                            .align(CenterStart)
                            .offset(x = 10.dp),
                        style = TextStyle(
                            color = MaterialTheme.colors.secondary,
                            fontSize = 22.sp
                        )
                    )
                }
                PokemonBaseStats(pokemonInfo = pokemonInfo.data)
            }
        }

        if (pokemonInfo is Resource.Error) {

        }

        if (isLoading) {
            LoadingScreen(
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
fun PokemonBaseStats(
    pokemonInfo: PokemonInfoResponse,
    animDelayPerItem: Int = 100
) {

    val maxBaseStat = remember { pokemonInfo.stats.maxOf { it.base_stat } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        pokemonInfo.stats.forEach { stat ->
            PokemonStat(
                name = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxValue = maxBaseStat,
                statColor = parseStatToColor(stat),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PokemonStat(
    name: String,
    statValue: Int,
    statMaxValue: Int,
    statColor: Color,
    height: Dp = 28.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val currentPercent by animateFloatAsState(
        targetValue = if (animationPlayed) statValue / statMaxValue.toFloat() else 0f,
        animationSpec = tween(
            animationDuration,
            animationDelay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(
                Color.Gray
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(currentPercent)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp),
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = (currentPercent * statMaxValue).toInt().toString(),
                fontWeight = FontWeight.Bold
            )
        }
    }

}