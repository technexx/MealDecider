package meal.decider



import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.maps.android.compose.GoogleMap
import meal.decider.ui.theme.MealDeciderTheme
import android.Manifest;
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.titleContentColor
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.net.URI

//TODO: Categories (Vegan, etc.)

@SuppressLint("StaticFieldLeak")
private lateinit var gameViewModel : GameViewModel
@SuppressLint("StaticFieldLeak")
private lateinit var activityContext : Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameViewModel = GameViewModel(application)
        activityContext = this@MainActivity

        gameViewModel.createSquareList()

        setContent {
            MealDeciderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopBar()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colorResource(id = R.color.blue_600),
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Meal Decider")
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "More"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add Cuisine") },
                                onClick = {
                                    gameViewModel.updateAddMode(true)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Cuisines") },
                                onClick = {
                                    gameViewModel.updateEditMode(!gameViewModel.getEditMode)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Restore Defaults") },
                                onClick = {
                                    expanded = false
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
        ) {
            Board()
        }
    }
}

@Composable
fun Board() {
    Column (modifier = Modifier
        .fillMaxWidth()
        .height((screenHeight() * 1).dp)
        .background(colorResource(id = R.color.grey_50))
    ) {
        SelectionGridLayout()
        Spacer(modifier = Modifier.height(16.dp))
        InteractionLayout()
    }
}

@Composable
fun SelectionGridLayout() {
    val boardUiState = gameViewModel.boardUiState.collectAsStateWithLifecycle()
    val addMode = gameViewModel.addMode.collectAsStateWithLifecycle()
    val editState = gameViewModel.editMode.collectAsStateWithLifecycle()
    val activeEdit = gameViewModel.activeEdit.collectAsStateWithLifecycle()

    var borderStroke: BorderStroke

    borderStroke = BorderStroke(4.dp,Color.Black)

    if (editState.value) {
        borderStroke = BorderStroke(4.dp,Color.Black)
    } else {
        borderStroke = BorderStroke(1.dp,Color.Black)
    }

    if (addMode.value) {
        AddDialogBox()
    }

    if (activeEdit.value) {
        EditDialogBox()
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),

        // content padding
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 16.dp,
            end = 12.dp,
            bottom = 16.dp
        ),
        content = {
            items(boardUiState.value.squareList.size) { index ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = gameViewModel.getSquareList[index].color),
                    ),
                    border = borderStroke,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .selectable(
                            selected = true,
                            onClick = {
                                if (gameViewModel.getEditMode) {
                                    gameViewModel.updateActiveEdit(true)
                                    gameViewModel.updateEditMode(false)
                                    gameViewModel.updateSquareToEdit(index)
                                }
                            }
                        ),
                ) {
                    Text(
                        text = boardUiState.value.squareList[index].name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    )
}

@SuppressLint("MissingPermission")
@Composable
fun InteractionLayout() {
    val rollFinished = gameViewModel.rollFinished.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var foodUri by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        foodUri = "geo:0,0?q=" + gameViewModel.getSelectedSquare.name + " Food"

        if (rollFinished.value) {
            Text(text = context.getString(R.string.meal_decided, gameViewModel.getSelectedSquare.name), color = Color.Black, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (!gameViewModel.getRollEngaged && !gameViewModel.getEditMode) {
                        gameViewModel.updateColorOfSquareValuesList()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_400)),
            ) {
                ButtonText(text = "Decide")
            }

            Spacer(modifier = Modifier.width(24.dp))

            Button(
                onClick = {
                    if (!gameViewModel.getRollEngaged && !gameViewModel.getEditMode) {
                        mapIntent(Uri.parse(foodUri))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_400))
            ) {
                ButtonText(text = "Open Maps")
            }
        }
    }
}
@Composable
fun FullCuisineList() {
    Column(modifier = Modifier.background(Color.Blue)) {
        LazyColumn (modifier = Modifier
            .weight(1f)
            .padding(12.dp)
            .background(Color.Red)
        ){
            items (fullCuisineList.size) { index ->
                Text(fontSize = 16.sp,
                    color = Color.Black,
                    text = fullCuisineList[index])
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialogBox() {
    var txtField by remember { mutableStateOf("") }

    Dialog(onDismissRequest = {
        gameViewModel.updateAddMode(false)
    })
    {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.grey_300)
        ) {
            Box(modifier = Modifier
                .size(height = 400.dp, width = 300.dp),
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly)
                {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        value = txtField,
                        onValueChange = { txtField = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = colorResource(id = R.color.grey_50),
                        ),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FullCuisineList()

//                    Spacer(modifier = Modifier.weight(1f))

                    Row (modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(onClick = {
                            gameViewModel.updateAddMode(false)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_red_light),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                        IconButton(onClick = {
                            gameViewModel.addSquareToList(txtField)
                            gameViewModel.updateAddMode(false)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_green_light),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialogBox() {
    var txtField by remember { mutableStateOf("") }

    txtField = gameViewModel.getSquareList[gameViewModel.getSquareToEdit].name

    Dialog(onDismissRequest = {
        gameViewModel.updateActiveEdit(false)
    })
    {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.grey_300)
        ) {
            Box(modifier = Modifier
                .size(height = 200.dp, width = 300.dp),
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly)
                {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        value = txtField,
                        onValueChange = { txtField = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = colorResource(id = R.color.grey_50),
                        ),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row (modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(onClick = {
                            gameViewModel.updateActiveEdit(false)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_red_light),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                        IconButton(onClick = {
                            gameViewModel.updateSquareName(gameViewModel.getSquareToEdit, txtField)
                            gameViewModel.updateActiveEdit(false)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_green_light),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                    }
                }
            }
        }

    }
}

fun mapIntent(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")

    activityContext.startActivity(intent)
}


@Composable
fun ButtonText(text: String) {
    Text(text = text, color = Color.Black, fontSize = 20.sp)
}

@Composable
private fun screenWidth() : Double {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.toDouble()
}

@Composable
private fun screenHeight() : Double {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.toDouble()
}

fun showLog(name: String, text: String) {
    Log.i(name, text)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MealDeciderTheme {
    }
}