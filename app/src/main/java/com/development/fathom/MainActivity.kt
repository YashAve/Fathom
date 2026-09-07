package com.development.fathom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.text.font.createFontFamilyResolver
import com.development.fathom.core.designsystem.theme.FathomFonts
import com.development.fathom.core.designsystem.theme.FathomTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val fontResolver = createFontFamilyResolver(applicationContext, backgroundScope.coroutineContext)
        backgroundScope.launch {
            fontResolver.apply {
                resolve(FathomFonts.spaceGrotesk)
                resolve(FathomFonts.jetbrainsMono)
            }
        }

        setContent {
            FathomTheme {

            }
        }
    }
}
