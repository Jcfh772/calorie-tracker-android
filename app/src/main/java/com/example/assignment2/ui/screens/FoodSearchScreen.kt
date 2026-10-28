package com.example.assignment2.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment2.data.model.MealType
import com.example.assignment2.data.model.MealViewModel
import com.example.assignment2.ui.components.FoodSearchResults
import com.example.assignment2.ui.components.ManualEntryDialog
import com.example.assignment2.ui.components.MealTypeDropdown
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FoodSearchScreen(viewModel: MealViewModel) {
    var query by remember { mutableStateOf("") }
    var portionSize by remember { mutableStateOf("100") } // Default to 100g
    var selectedMealType by remember { mutableStateOf(MealType.Breakfast) }
    var showManualEntryDialog by remember { mutableStateOf(false) }

    val onPortionSizeChange: (String) -> Unit = { portionSize = it }
    val onMealTypeChange: (MealType) -> Unit = { selectedMealType = it }

    val manualEntryEvent = viewModel.manualEntryEvent

    val context = LocalContext.current

    // Permissions
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val storagePermissionState = rememberPermissionState(android.Manifest.permission.READ_MEDIA_IMAGES)

    var thumbnailImage by remember { mutableStateOf<Bitmap?>(null) }

    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }

    // Launcher for capturing a thumbnail image with the camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            thumbnailImage = bitmap
        } else {
            Log.e("CameraCapture", "Failed to capture image")
        }
    }


    // Launcher for selecting a photo from the gallery and converting it to a Bitmap
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                try {
                    // Decode the Uri into a Bitmap
                    val inputStream = context.contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    // Update the thumbnailImage state with the selected image
                    if (bitmap != null) {
                        thumbnailImage = bitmap
                    } else {
                        Log.e("GalleryPicker", "Failed to decode image")
                    }
                } catch (e: Exception) {
                    Log.e("GalleryPicker", "Error loading image from gallery", e)
                }
            }
        }
    )




    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Food") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.searchFood(query) }) {
            Text("Search")
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = portionSize,
            onValueChange = onPortionSizeChange,
            label = { Text("Portion Size (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Meal Type Selection
        MealTypeDropdown(selectedMealType, onMealTypeChange)

        Row {
            Button(onClick = {
                if (cameraPermissionState.status.isGranted) {
                    cameraLauncher.launch() // Launch the camera for a thumbnail
                } else {
                    cameraPermissionState.launchPermissionRequest()
                }
            }) {
                Text("Take Photo Preview")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                Log.d("PermissionStatus", "Permission status: ${storagePermissionState.status.isGranted}")
                if (storagePermissionState.status.isGranted) {
                    pickImageLauncher.launch("image/*")
                } else {
                    storagePermissionState.launchPermissionRequest()

                }

            }) {
                Text("Select from Gallery")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Display the captured thumbnail if available
        thumbnailImage?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }


        if (viewModel.isLoading) {
            CircularProgressIndicator()
        } else if (viewModel.errorMessage != null) {
            Text(text = viewModel.errorMessage!!)
        }else if (viewModel.searchResults.isEmpty() && query.isNotEmpty()) {
            Text("No results found.")
            viewModel.promptManualEntry(query, portionSize.toDouble(), selectedMealType)
            Button(onClick = {
                // Display Manual Entry Dialog if required
                if (manualEntryEvent != null) {

                    showManualEntryDialog = true
                }

            }) {
                Text("Manually Add Meal")
            }
        }else {
            FoodSearchResults(
                results = viewModel.searchResults,
                portionSize = portionSize,
                onAddMeal = { foodItem ->
                    var imageByteArray: ByteArray? = null
                    val size = portionSize.toDoubleOrNull() ?: 100.0
                    if (thumbnailImage != null) {
                        // Convert Bitmap to ByteArray
                        val stream = ByteArrayOutputStream()
                        thumbnailImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        imageByteArray = stream.toByteArray()

                        viewModel.uploadPhotoToFirebase(imageByteArray, onProgress = {
                            isUploading = true
                            uploadProgress = it
                        }, onComplete = { downloadUrl ->
                            Log.d("Screen: Firebase", "Image uploaded successfully: $downloadUrl")
                            uploadedImageUrl = downloadUrl
                            isUploading = false
                            viewModel.addFoodItemAsMeal(foodItem, size, selectedMealType, uploadedImageUrl)
                            Toast.makeText(context, "Image uploaded, Item saved successfully", Toast.LENGTH_SHORT).show()
                        }, onError = {
                            // Handle errors
                            isUploading = false
                            viewModel.addFoodItemAsMeal(foodItem, size, selectedMealType, null)
                            Toast.makeText(context, "Image error, Item saved successfully", Toast.LENGTH_SHORT).show()
                        })
                    }else{
                        Log.d("Screen: MealViewModel", "Adding food item as meal: $foodItem")
                        viewModel.addFoodItemAsMeal(foodItem, size, selectedMealType, null)
                        Toast.makeText(context, "Item saved successfully", Toast.LENGTH_SHORT).show()
                    }

                }
            )
        }

        // Show the dialog if needed
        if (showManualEntryDialog && manualEntryEvent != null) {
            ManualEntryDialog(
                event = manualEntryEvent,
                onSubmit = { calories, fats, proteins, carbohydrates ->
                    var imageByteArray: ByteArray? = null
                    if (thumbnailImage != null) {
                        // Convert Bitmap to ByteArray
                        val stream = ByteArrayOutputStream()
                        thumbnailImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        imageByteArray = stream.toByteArray()

                        viewModel.uploadPhotoToFirebase(imageByteArray, onProgress = {
                            isUploading = true
                            uploadProgress = it
                        }, onComplete = { downloadUrl ->
                            uploadedImageUrl = downloadUrl
                            isUploading = false
                            viewModel.submitManualEntry(calories, fats, proteins, carbohydrates, uploadedImageUrl)
                            Toast.makeText(context, "Image uploaded, Item saved successfully", Toast.LENGTH_SHORT).show()
                        }, onError = {
                            // Handle errors
                            isUploading = false

                            viewModel.submitManualEntry(calories, fats, proteins, carbohydrates, null)
                            Toast.makeText(context, "Image error, Item saved successfully", Toast.LENGTH_SHORT).show()
                        })
                    }else{
                        viewModel.submitManualEntry(calories, fats, proteins, carbohydrates, uploadedImageUrl)
                        showManualEntryDialog = false // Close the dialog after submission
                        Toast.makeText(context, "Item saved successfully", Toast.LENGTH_SHORT).show()
                    }

                },
                onCancel = {
                    viewModel.cancelManualEntry()
                    showManualEntryDialog = false // Close the dialog on cancel
                }
            )
        }

        // Progress indicator for image upload
        if (isUploading) {
            Log.d("UploadProgress", "Upload progress: $uploadProgress")
            LinearProgressIndicator(
                progress = { uploadProgress},
            )
        }
    }
}



@Preview
@Composable
fun FoodSearchScreenPreview() {
    val mealViewModel: MealViewModel = viewModel()
    FoodSearchScreen(viewModel = mealViewModel)
}