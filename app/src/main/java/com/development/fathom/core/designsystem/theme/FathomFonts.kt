package com.development.fathom.core.designsystem.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.development.fathom.R

object FathomFonts {

    private val provider = GoogleFont.Provider(
        providerAuthority = "com.google.play.gms.fonts",
        providerPackage = "com.google.play.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    private val spaceGroteskName = GoogleFont("Space Grotesk")
    private val jetbrainsMonoName = GoogleFont("JetBrains Mono")

    val spaceGrotesk = FontFamily(
        Font(spaceGroteskName, provider, FontWeight.W600),
        Font(spaceGroteskName, provider, FontWeight.W400)
    )

    val jetbrainsMono = FontFamily(
        Font(jetbrainsMonoName, provider, FontWeight.W500)
    )
}