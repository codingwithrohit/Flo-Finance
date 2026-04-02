package com.flo.app.ui.theme

import androidx.compose.ui.graphics.Color

// --- Background Layers (creates depth in dark mode) ---
val Background = Color(0xFF0A0A0A)        // deepest — page background
val Surface = Color(0xFF141414)           // cards
val SurfaceVariant = Color(0xFF1E1E1E)    // elevated cards, bottom sheets

// --- Primary Accent (amber/gold) ---
val Primary = Color(0xFFF5A623)           // main CTA, score ring, active icons
val PrimaryVariant = Color(0xFFFFD97D)    // lighter amber — positive amounts, highlights
val PrimaryDim = Color(0xFF3A2E10)        // very dark amber — chip backgrounds, subtle tints

// --- Text ---
val OnBackground = Color(0xFFFFFFFF)      // primary text on dark backgrounds
val OnSurface = Color(0xFFA0A0A0)         // secondary text, labels, hints
val OnSurfaceDim = Color(0xFF5A5A5A)      // disabled text, placeholders

// --- Semantic Colors ---
val Income = Color(0xFFF5A623)            // same as primary — income is gold
val Expense = Color(0xFFFF6B6B)           // soft red — expenses
val ExpenseDim = Color(0xFF3A1515)        // dark red — expense chip background

// --- Light Theme (clean, minimal) ---
val LightBackground = Color(0xFFF8F8F8)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F0F0)
val LightPrimary = Color(0xFFE09415)      // slightly darker amber for light bg contrast
val LightOnBackground = Color(0xFF1A1A1A)
val LightOnSurface = Color(0xFF6B6B6B)
val LightOnSurfaceDim = Color(0xFFAAAAAA)

val LightPrimaryContainer = Color(0xFFFFF3DC)
val LightOnPrimaryContainer = Color(0xFF5C3A00)