package com.nscc.spaceapp

//import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.hoverable
//import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.nscc.spaceapp.ui.theme.SpaceAppTheme

//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.window.Dialog
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Surface
//import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpaceAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NasaApodImage()
                }
            }
        }
    }
}

@Composable
fun NasaApodImage() {
    // state variables set default to empty to help preserving state across recomps - variables
    // same as API variables
    var imageUrl by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // coroutine launcher
    LaunchedEffect(Unit) {
        // calls API_Retrofit method
        val apiService = createApiService()
        // give the api key and fetch the image
        apiService.fetchApodImage("qJPle1eTUWnr2fshhMLca9NygfeG95alpxXWrK36")
            .enqueue(object : Callback<NasaApodService.ApodResponse> {
                override fun onResponse(
                    call: Call<NasaApodService.ApodResponse>,
                    response: Response<NasaApodService.ApodResponse>
                ) {
                    // if the request is successful it updates the state variables declared earlier
                    if (response.isSuccessful) {
                        imageUrl = response.body()?.url.orEmpty()
                        title = response.body()?.title.orEmpty()
                        date = response.body()?.date.orEmpty()
                        explanation = response.body()?.explanation.orEmpty()
                    }
                }
                override fun onFailure(call: Call<NasaApodService.ApodResponse>, t: Throwable) {
                    title = "Error fetching APOD"
                }
            })
    }

    // this displays the image and title
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "NASA Astronomy Picture of the Day",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 6.dp)
        )

        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
            Box(
                contentAlignment = Alignment.BottomCenter, // Aligns the Text to the top center
                modifier = Modifier
                    .fillMaxWidth() // Fills the width of the parent
                    .padding(top = 10.dp),
                // Adds a padding at the top
            ) {
                Surface(
                    modifier = Modifier
                        .clickable { showDialog = true }
                        .padding(8.dp), // Add padding inside the Surface for the Text
                    shape = MaterialTheme.shapes.medium, // Rounded corners
                    color = MaterialTheme.colorScheme.primaryContainer, // Background color
                    shadowElevation = 4.dp // Elevation for a slight shadow
                ) {
                    Text(
                        text = "Click Here For More Information",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp) // Padding around the Text
                    )
                }
            }

            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) { //enables diaglog box to be closed
                    Surface(
                        shape = MaterialTheme.shapes.medium, // Gives the dialog rounded corners
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = date, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = explanation, style = MaterialTheme.typography.bodyMedium)
                            // You can add a Button or IconButton here to explicitly close the dialog if you want
                            Button(
                                onClick = { showDialog = false },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }
}
