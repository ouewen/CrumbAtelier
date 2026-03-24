package com.crumbatelier

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.crumbatelier.ui.navigation.CrumbNavGraph
import com.crumbatelier.ui.theme.CrumbAtelierTheme
import com.crumbatelier.util.AppSessionManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CrumbAtelierApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: AppSessionManager
    @Inject lateinit var supabase: SupabaseClient

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CHECK_URL", BuildConfig.SUPABASE_URL)

        enableEdgeToEdge()
        setContent {
            CrumbAtelierTheme {
                val session by sessionManager.session.collectAsStateWithLifecycle()
                val nav = rememberNavController()
                navController = nav
                CrumbNavGraph(navController = nav, session = session)
            }
        }

        // Handle deep link AFTER setContent so navController is ready
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        Log.d("DEEP_LINK", "Received URI: $uri")

        if (uri.scheme == "crumbatelier" && uri.host == "reset-password") {
            Log.d("DEEP_LINK", "Reset password deep link detected")
            lifecycleScope.launch {
                try {
                    val code = uri.getQueryParameter("code")
                    if (code != null) {
                        // PKCE flow — exchange the code for a valid session
                        supabase.auth.exchangeCodeForSession(code)
                        Log.d("DEEP_LINK", "Session imported successfully")
                        navController?.navigate("reset_password") {
                            popUpTo("login") { inclusive = false }
                        }
                    } else {
                        Log.e("DEEP_LINK", "No code parameter found in URI: $uri")
                        navController?.navigate("login")
                    }
                } catch (e: Exception) {
                    Log.e("DEEP_LINK", "Failed to import session: ${e.message}")
                    navController?.navigate("login")
                }
            }
        }
    }
}