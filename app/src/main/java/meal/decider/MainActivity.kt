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
import android.content.ContentValues.TAG
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


private lateinit var gameViewModel : GameViewModel
@SuppressLint("StaticFieldLeak")
private lateinit var activityContext : Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameViewModel = GameViewModel(applicationContext)
//        context = applicationContext
        activityContext = this@MainActivity

        setContent {
            MealDeciderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Board()
                    }
                }
            }
        }
    }
}

@Composable
fun Board() {
    val boardUiState = gameViewModel.boardUiState.collectAsStateWithLifecycle()

    Column (modifier = Modifier
        .fillMaxWidth()
        .height((screenHeight() * 1).dp)
        .background(Color.Blue)
    ) {
        if (boardUiState.value.showMap) {
            GoogleMapView()
        } else {
            SelectionGridLayout()
            Spacer(modifier = Modifier.height(24.dp))
            InteractionLayout()
        }

    }
}

@Composable
fun SelectionGridLayout() {
    val boardUiState = gameViewModel.boardUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    gameViewModel.createSquareList()
    gameViewModel.createColorList()

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
            showLog("test", "recomp grid!")

            items(boardUiState.value.squareList.size) { index ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = gameViewModel.colorList[index],
                    ),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .selectable(
                            selected = true,
                            onClick = {

                            }
                        ),
                ) {
                    Text(
                        text = boardUiState.value.squareList[index].name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFFFFFFF),
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

    //Retrieves last known location of device
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    //Stores location
    var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
    //Lat/long of location
    var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    //Camera position in Maps
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)}

    val locationResult = fusedLocationProviderClient.lastLocation
    locationResult.addOnCompleteListener(context as MainActivity) { task ->
        if (task.isSuccessful) {
            lastKnownLocation = task.result
            deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
        } else {
            Log.d(TAG, "Current location is null. Using defaults.")
            Log.e(TAG, "Exception: %s", task.exception)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            gameViewModel.updateColorListWithinLooper()
        }) {
            ButtonUi(text = "Decide")
        }

        Spacer(modifier = Modifier.height(24.dp))

        showLog("test", "recomp interaction!")

        if (boardUiState.value.rollFinished) {
            Text(text = context.getString(R.string.meal_decided, gameViewModel.selectedSquare.name), color = Color.White, fontSize = 22.sp)

            Spacer(modifier = Modifier.weight(1f))

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    gameViewModel.updateColorListWithinLooper()
                }) {
                    ButtonUi(text = "Roll Again")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(onClick = {
                    if (getLocationPermission()) {
                        gameViewModel.updateShowMap(true)
                    } else {
                        Toast.makeText(context, "Nooo!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    ButtonUi(text = "Open Maps")
                }
            }
        }
    }
}

@Composable
fun GoogleMapView() {
    Column (modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        GoogleMap {

        }
    }

}

@Composable
fun ButtonUi(text: String) {
    Text(text = text, color = Color.White, fontSize = 20.sp)
}

//If user grants location permission, returns true.
private fun getLocationPermission() : Boolean {
    val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1234
    val locationPermissionGranted = mutableStateOf(false)

    if (ContextCompat.checkSelfPermission(
            activityContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        == PackageManager.PERMISSION_GRANTED
    ) {
        locationPermissionGranted.value = true//we already have the permission
    } else {
        ActivityCompat.requestPermissions(
            activityContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted.value=true
        }
    }

    return locationPermissionGranted.value
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