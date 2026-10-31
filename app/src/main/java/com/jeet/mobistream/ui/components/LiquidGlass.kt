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
import com.jeet.mobistream.ui.theme.AmoledBlack

/**
 * Apple Liquid Glass Surface with top specular rim reflection and AMOLED depth.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    borderWidth: Dp = 1.dp,
    tint: Color = Color(0x261E1E1E),
    specularAlpha: Float = 0.28f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptics = rememberHaptics()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.965f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 500f),
        label = "glassScale"
    )

    LaunchedEffect(isPressed) {
        if (isPressed && onClick != null) {
            haptics.selectionTick()
        }
    }

    val glassBorder = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = specularAlpha),
            Color.White.copy(alpha = specularAlpha * 0.35f),
            Color.White.copy(alpha = 0.04f)
        )
    )

    val glassFill = Brush.verticalGradient(
        colors = listOf(
            tint.copy(alpha = 0.35f),
            AmoledBlack.copy(alpha = 0.55f)
        )
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(glassFill, shape)
            .border(borderWidth, glassBorder, shape)
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
 * Liquid Glass Capsule / Pill Badge (for quality tags, category chips, live badges)
 */
@Composable
fun LiquidGlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        accentColor.copy(alpha = 0.18f),
                        Color(0x1AFFFFFF)
                    )
                )
            )
            .border(
                0.75.dp,
                accentColor.copy(alpha = 0.45f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        androidx.compose.material3.Text(
            text = text,
            color = accentColor,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    }
}

