package com.msa.adadyar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msa.adadyar.ui.theme.AdadyarTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.msa.adadyar.core.ui.theme.AdadyarTheme
import com.msa.adadyar.features.home.HomeRoute
import com.msa.adadyar.features.lesson.LessonRoute
import com.msa.adadyar.features.practice.PracticeRoute
import com.msa.adadyar.features.progress.ProgressRoute
import com.msa.adadyar.features.profile.ProfileScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AdadyarApp() }
    }
}

@Composable
fun AdadyarApp() {
    AdadyarTheme {
        val navController = rememberNavController()
        val profileId = remember { "default" }
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeRoute(onLessonSelected = { lessonId ->
                    navController.navigate("lesson/$lessonId")
                })
            }
            composable(
                route = "lesson/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                LessonRoute(
                    lessonId = lessonId,
                    profileId = profileId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "practice/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { entry ->
                val lessonId = entry.arguments?.getString("lessonId") ?: return@composable
                PracticeRoute(
                    lessonId = lessonId,
                    profileId = profileId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("progress") {
                ProgressRoute(profileId = profileId)
            }
            composable("profile") {
                ProfileScreen(onSwitchProfile = { /* future */ })
            }
        }
    }
}