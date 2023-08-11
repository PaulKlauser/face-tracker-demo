package com.example.facetracker

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import com.google.ar.core.AugmentedFace
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.math.Vector3


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arSceneView = ArSceneView(this)
        val session = Session(this, mutableSetOf(Session.Feature.FRONT_CAMERA))
        val config = Config(session)

        config.setAugmentedFaceMode(Config.AugmentedFaceMode.MESH3D)
        config.lightEstimationMode
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        session.configure(config)

        arSceneView.setupSession(session)
        arSceneView.resume()

        setContent {
            var pixelOffset by remember { mutableStateOf(Offset(0f, 0f)) }

            val springSpec = remember {
                SpringSpec<Dp>(
                    stiffness = Spring.StiffnessLow
                )
            }
            val tweenSpec = remember {
                TweenSpec<Dp>(
                    durationMillis = 100,
                    easing = LinearEasing
                )
            }

            val widthDp = LocalConfiguration.current.screenWidthDp.dp
            val heightDp = LocalConfiguration.current.screenHeightDp.dp

            val xSensitivityFactor = 2
            val ySensitivityFactor = 4

            val xDpOffset = with(LocalDensity.current) {
//                pixelOffset.x.toDp()
                animateDpAsState(
                    targetValue = (pixelOffset.x.toDp() - (widthDp / 2)) * xSensitivityFactor,
                    animationSpec = springSpec
                )
            }

            val yDpOffset = with(LocalDensity.current) {
//                pixelOffset.y.toDp()
                animateDpAsState(
                    targetValue = (pixelOffset.y.toDp() - (heightDp / 2)) * ySensitivityFactor,
                    animationSpec = springSpec
                )
            }
//            val dpOffset = with(LocalDensity.current) {
//                DpOffset(animateDpAsState(targetValue = pixelOffset.x.toDp()), pixelOffset.y.toDp())
//            }

            LaunchedEffect(Unit) {
                arSceneView.scene.addOnUpdateListener {
                    session.getAllTrackables(
                        AugmentedFace::class.java
                    ).forEach { face ->
                        if (face.trackingState == TrackingState.TRACKING) {
                            val screenPoint = arSceneView.scene.camera.worldToScreenPoint(
                                face.centerPose.zAxis
                                    .let { Vector3(it[0], it[1], -it[2]) }
                            )
                            Log.d("ScreenPoint", screenPoint.toString())
                            pixelOffset = Offset(screenPoint.x, screenPoint.y)
                        }
                    }
                }
            }

            Box(contentAlignment = Alignment.Center) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { arSceneView })
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .absoluteOffset(xDpOffset.value, yDpOffset.value)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .size(48.dp, 48.dp)
                ) {
                    with(LocalDensity.current) {
                        100.toDp()
                    }
                }
            }
        }
    }
}