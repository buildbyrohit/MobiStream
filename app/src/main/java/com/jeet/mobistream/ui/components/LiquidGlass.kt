package com.jeet.mobistream.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeet.mobistream.ui.theme.AmoledBlack

/**
 * Minimalist AMOLED Surface with ultra-clean subtle specular rim.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 0.5.dp,
    tint: Color = Color(0xFF0D0D0D),
    specularAlpha: Float = 0.08f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptics = rememberHaptics()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 600f),
        label = "glassScale"
    )

    LaunchedEffect(isPressed) {
        if (isPressed && onClick != null) {
            haptics.selectionTick()
        }
    }

    val glassBorder = if (borderWidth > 0.dp && specularAlpha > 0f) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = specularAlpha),
                Color.White.copy(alpha = specularAlpha * 0.4f),
                Color.Transparent
            )
        )
    } else null

    val surfaceFill = tint

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(surfaceFill, shape)
            .then(
                if (glassBorder != null && borderWidth > 0.dp) {
                    Modifier.border(borderWidth, glassBorder, shape)
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            haptics.lightClick()
                            onClick()
                        }
                    )
                } else Modifier
            ),
        content = content
    )
}

/**
 * Minimalist Capsule / Pill Badge
 */
@Composable
fun LiquidGlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .border(
                0.5.dp,
                accentColor.copy(alpha = 0.25f),
                RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        androidx.compose.material3.Text(
            text = text,
            color = accentColor,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}
