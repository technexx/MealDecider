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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.net.URI


@SuppressLint("StaticFieldLeak")
private lateinit var gameViewModel : GameViewModel
@SuppressLint("StaticFieldLeak")
private lateinit var activityContext : Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameViewModel = GameViewModel(application)
//        context = applicationContext
        activityContext = this@MainActivity

        gameViewModel.createSquareList()
        gameViewModel.createColorList()

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
                                text = { Text("Edit Cuisines") },
                                onClick = {
                                    gameViewModel.updateEditMode(!gameViewModel.getEditMode)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Restore Defaults") },
                                onClick = {

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
    val boardUiState = gameViewModel.boardUiState.collectAsStateWithLifecycle()

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
fun EditDialog() {
    AlertDialog(
        onDismissRequest = {  gameViewModel.updateActiveEdit(false) },
        title = { Text("Are you sure you want to delete this?") },
        text = { Text("This action cannot be undone") },
        confirmButton = {
            TextButton(onClick = { /* TODO */}) {
                Text("Delete it".uppercase())
            }
        },
        dismissButton = {
            TextButton(onClick = { gameViewModel.updateActiveEdit(false) }) {
                Text("Cancel".uppercase())
            }
        },
    )
}

@Composable
fun SelectionGridLayout() {
    val boardUiState = gameViewModel.boardUiState.collectAsStateWithLifecycle()
    val editState = gameViewModel.editMode.collectAsStateWithLifecycle()
    val activeEdit = gameViewModel.activeEdit.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var borderStroke: BorderStroke

    borderStroke = BorderStroke(4.dp,Color.Black)

    if (editState.value) {
        borderStroke = BorderStroke(4.dp,Color.Black)
    } else {
        borderStroke = BorderStroke(1.dp,Color.Black)
    }

    if (activeEdit.value) {
        EditDialog()
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
                        containerColor = colorResource(id = gameViewModel.colorList[index]),
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
    val boardUiState = gameViewModel.boardUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var foodUri by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        foodUri = "geo:0,0?q=" + gameViewModel.selectedSquare.name + " Food"

        if (boardUiState.value.rollFinished) {
            Text(text = context.getString(R.string.meal_decided, gameViewModel.selectedSquare.name), color = Color.Black, fontSize = 22.sp)
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
                    if (!gameViewModel.rollEngaged) {
                        gameViewModel.updateColorListWithinLooper()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_400)),
            ) {
                ButtonText(text = "Decide")
            }

            Spacer(modifier = Modifier.width(24.dp))

            Button(
                onClick = {
                    if (!gameViewModel.rollEngaged) {
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