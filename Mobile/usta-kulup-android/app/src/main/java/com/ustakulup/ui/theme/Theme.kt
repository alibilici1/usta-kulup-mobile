package com.ustakulup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Design Token'ları (web ile aynı) ────────────────────────────────────────
val Primary    = Color(0xFF0F172A)   // #0F172A
val Gold       = Color(0xFFD4AF37)   // #D4AF37 Accent/Gold
val Background = Color(0xFFF5F5F5)  // #F5F5F5
val Surface    = Color(0xFFFFFFFF)
val Success    = Color(0xFF16A34A)   // #16A34A
val Error      = Color(0xFFDC2626)   // #DC2626
val TextPrimary   = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)
val Divider       = Color(0xFFE2E8F0)
val GoldLight     = Color(0xFFFFF8E1)

private val LightColorScheme = lightColorScheme(
    primary          = Primary,
    onPrimary        = Color.White,
    primaryContainer = Gold,
    onPrimaryContainer = Primary,
    secondary        = Gold,
    onSecondary      = Primary,
    background       = Background,
    onBackground     = TextPrimary,
    surface          = Surface,
    onSurface        = TextPrimary,
    error            = Error,
    onError          = Color.White,
    outline          = Divider
)

@Composable
fun UstaKulupTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
