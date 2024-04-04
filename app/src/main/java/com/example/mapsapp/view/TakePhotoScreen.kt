package com.example.mapsapp.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.mapsapp.PermissionDeclinedScreen
import com.example.mapsapp.R
import com.example.mapsapp.model.MarkerData
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TakePhotoScreen(navigationController: NavHostController, myViewModel: MyViewModel) {
    Camera(navigationController, myViewModel)
}

@Composable
fun Camera(navigationController: NavController, myViewModel: MyViewModel) {
    val comingFromMap by remember { mutableStateOf(myViewModel.comingFromMap) }
    val context = LocalContext.current
    val myMarker: MarkerData by myViewModel.actualMarker.observeAsState(MarkerData("ITB",(LatLng(41.4534265, 2.1837151)),"", "", mutableListOf()))
    val img:Bitmap?= ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmap()
    var bitmap by remember { mutableStateOf(img) }
    val launchImage= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (Build.VERSION.SDK_INT<28){
                bitmap= MediaStore.Images.Media.getBitmap(context.contentResolver,it)
            }else{
                val source=it?.let { it1-> ImageDecoder.createSource(context.contentResolver,it1) }
                source?.let { it1-> ImageDecoder.decodeBitmap(it1)}
                myViewModel.addPhotoToMarker(myMarker, bitmap!!)
                navigationController.navigate(Routes.DetailScreen.route)
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
                    takePhoto(context, controller) {photo ->
                        if (!comingFromMap) {
                            myViewModel.addPhotoToMarker(myMarker, photo)
                        }
                        else {
                            myViewModel.addPhotosToNewMarker(photo)
                        }

                    }
                }) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
                }
                IconButton(onClick = { launchImage.launch("*images/*") }) {
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
                onPhotoTaken(image.toBitmap())
            }
            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Error taken photo", exception)
            }
        }
    )
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