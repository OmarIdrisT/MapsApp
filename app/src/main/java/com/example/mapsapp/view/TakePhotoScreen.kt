package com.example.mapsapp.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.impl.utils.MatrixExt.postRotate
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mapsapp.R
import com.example.mapsapp.viewmodel.MyViewModel

@Composable
fun TakePhotoScreen(navigationController: NavHostController, myViewModel: MyViewModel) {
    Camera(navigationController, myViewModel)
}

@Composable
fun Camera(navigationController: NavController, myViewModel: MyViewModel) {
    val context = LocalContext.current
    val img: Bitmap?= ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmap()
    val comingFromMap by remember { mutableStateOf(myViewModel.comingFromMap) }
    var bitmap by remember { mutableStateOf(img) }
    Uri.parse("")
    val launchImage= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (Build.VERSION.SDK_INT<28){
                bitmap= MediaStore.Images.Media.getBitmap(context.contentResolver,it)
                if (it != null && !comingFromMap) {
                    myViewModel.uploadImage(it)
                }
                else {
                    myViewModel.addPhotosToNewMarker(it.toString())
                    if (it != null) {
                        myViewModel.uploadImage(it)
                    }
                }
            }else{
                val source=it?.let { it1-> ImageDecoder.createSource(context.contentResolver,it1) }
                source?.let { it1-> ImageDecoder.decodeBitmap(it1)}
                if (it != null && !comingFromMap) {
                    myViewModel.uploadImage(it)
                }
                else {
                    myViewModel.addPhotosToNewMarker(it.toString())
                    if (it != null) {
                        myViewModel.uploadImage(it)
                    }
                }
                Log.e("IMAGEN","si va")
            }
        }
    )
    val controller = remember {
        LifecycleCameraController(context).apply {
            CameraController.IMAGE_CAPTURE
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
        IconButton(
            onClick = {
                controller.cameraSelector =
                    if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }
            },
            modifier = Modifier.offset(16.dp, 16.dp)) {
            Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch camera")
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {navigationController.navigateUp()}) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
                IconButton(onClick = {
                    takePhoto(context, controller) {
                        val newPhotoUri = myViewModel.bitmapToUri(context, it)
                        if (newPhotoUri != null) {
                            myViewModel.uploadImage(newPhotoUri)
                        }
0                    }
                }) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
                }
                IconButton(onClick = { launchImage.launch("image/*") }) {
                    Icon(imageVector = Icons.Default.Photo, contentDescription = "Open gallery")
                }

            }
        }
    }
}


private fun takePhoto(context: Context, controller: LifecycleCameraController, onPhotoTaken:(Bitmap) -> Unit) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val rotatedPhoto = rotateImageIfNeeded(image.toBitmap(), 90)
                onPhotoTaken(rotatedPhoto)

            }
            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Error taken photo", exception)
            }
        }
    )
}

private fun rotateImageIfNeeded(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    return if (rotationDegrees != 0) {
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}

@Composable
fun CameraPreview(
    controller: LifecycleCameraController, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }, modifier = modifier
    )
}