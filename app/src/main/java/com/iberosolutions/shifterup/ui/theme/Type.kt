package com.iberosolutions.shifterup.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.iberosolutions.shifterup.R
import androidx.compose.ui.text.font.FontStyle

val PoppinsFamily = FontFamily(
    Font(R.font.poppins_thin, FontWeight.Thin),      // Peso 100
    Font(R.font.poppins_extralight, FontWeight.ExtraLight),  // Peso 200
    Font(R.font.poppins_light, FontWeight.Light),            // Peso 300
    Font(R.font.poppins_regular, FontWeight.Normal),         // Peso 400
    Font(R.font.poppins_medium, FontWeight.Medium),          // Peso 500
    Font(R.font.poppins_mediumitalic, FontWeight.Medium, FontStyle.Italic), // Italic 500
    Font(R.font.poppins_semibold, FontWeight.SemiBold),      // Peso 600
    Font(R.font.poppins_bold, FontWeight.Bold),              // Peso 700
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),    // Peso 800
    Font(R.font.poppins_black, FontWeight.Black)             // Peso 900
)

val AppTypography = Typography(
    // --- DISPLAY: Para textos muy grandes (Intros, Splash screens) ---
    displayLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Black, // Usamos poppins_black
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.ExtraBold, // Usamos poppins_extrabold
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold, // Usamos poppins_bold
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // --- HEADLINE: Encabezados principales ---
    headlineLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold, // Usamos poppins_semibold
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // --- TITLE: Títulos de componentes (Tarjetas, TopAppBar) ---
    titleLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium, // Usamos poppins_medium
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // --- BODY: Texto de lectura normal ---
    bodyLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal, // Usamos poppins_regular
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Light, // Usamos poppins_light para detalles finos
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // --- LABEL: Botones, tabs, etiquetas ---
    labelLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium, // Usamos poppins_medium
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)