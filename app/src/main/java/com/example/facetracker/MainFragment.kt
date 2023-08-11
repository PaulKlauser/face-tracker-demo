//package com.example.facetracker
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.activity.ComponentActivity
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.lifecycle.ViewModelProvider
//import com.google.ar.core.AugmentedFace
//import com.google.ar.core.CameraConfig
//import com.google.ar.core.CameraConfigFilter
//import com.google.ar.core.Config
//import com.google.ar.core.Session
//import com.google.ar.sceneform.ux.ArFragment
//
//class MainFragment : ArFragment() {
//
//    private lateinit var vm: FaceTrackingViewModel
//
//    override fun getSessionConfiguration(session: Session): Config {
//        session.cameraConfig = session.getSupportedCameraConfigs(
//            CameraConfigFilter(session).setFacingDirection(CameraConfig.FacingDirection.FRONT)
//        ).first()
//        return Config(session)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return ComposeView(requireContext()).apply {
//            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//            setContent {
//
//            }
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        arSceneView.resume()
//        vm = ViewModelProvider(requireActivity() as ComponentActivity).get(FaceTrackingViewModel::class.java)
//        arSceneView.scene.addOnUpdateListener {
//            vm.onSceneUpdate(arSceneView.session?.getAllTrackables(AugmentedFace::class.java))
//        }
//    }
//}