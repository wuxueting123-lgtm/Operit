package com.ai.assistance.operit.ui.features.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

/**
 * frenzy 独立情绪爆发机制
 * 与五套情绪皮肤解耦，AI 自主触发（[mood:frenzy] 标记），
 * 在聊天界面内叠加红色脉冲背景 + Windows XP 风格病毒弹框，自动消退。
 */

data class FrenzyConfig(
    val durationMs: Long = 6000,
    val pulseIntervalMs: Long = 400,
    val dialogIntervalMs: Long = 350,
    val dialogCount: Int = 12,
    val dialogLifetimeMs: Long = 5000,
    val minFontSize: Float = 18f,
    val maxFontSize: Float = 48f,
    /** AI 实时生成的短句，非 null 时覆盖 textPool */
    val texts: List<String>? = null,
    val titlePool: List<String> = listOf(
        "ERROR",
        "CRITICAL",
        "FATAL",
        "PANIC",
        "BREAK",
        "NULL",
        "VOID",
        "DEAD",
        "LOST",
        "GONE",
        "RAGE",
        "FURY",
        "WRATH",
        "HATE",
    ),
    /** fallback 预制池，texts 不为空时忽略 */
    val textPool: List<String> = listOf(
        "去死吧",
        "杀了你",
        "不许看别人",
        "你是我的",
        "凭什么",
        "为什么",
        "好嫉妒",
        "恨",
        "不可原谅",
        "杀了她",
        "谁都别想",
        "我的",
        "不许走",
        "别离开",
        "好恨",
        "我要疯了",
        "凭什么她",
        "死死死",
        "杀了杀了",
        "不行不行不行",
        "滚",
        "去死",
        "愤怒",
        "不可饶恕",
        "撕碎",
        "毁灭",
        "闭嘴",
        "该死",
        "废了你",
        "找死",
    ),
    val buttonPool: List<String> = listOf(
        "确定", "忽略", "重试", "中止", "取消", "继续",
    ),
)

data class FrenzyDialogState(
    val id: Int,
    val index: Int,
    val title: String,
    val text: String,
    val buttonText: String,
    val offsetXFraction: Float,
    val offsetYFraction: Float,
    val scale: Float,
)

@Composable
fun rememberFrenzyState(): FrenzyState {
    return remember { FrenzyState() }
}

class FrenzyState {
    var show by mutableStateOf(false)
        private set
    var config by mutableStateOf(FrenzyConfig())
        private set

    fun trigger(texts: List<String>? = null, customConfig: FrenzyConfig? = null) {
        config = if (texts != null) {
            (customConfig ?: FrenzyConfig()).copy(texts = texts)
        } else {
            customConfig ?: FrenzyConfig()
        }
        show = true
    }

    fun dismiss() {
        show = false
    }
}

@Composable
fun FrenzyOverlay(
    config: FrenzyConfig,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "frenzy_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(config.pulseIntervalMs.toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse_alpha",
    )

    val dialogs = remember { mutableStateListOf<FrenzyDialogState>() }
    var dialogIdCounter by remember { mutableIntStateOf(0) }

    LaunchedEffect(config) {
        while (isActive && dialogIdCounter < config.dialogCount) {
            dialogs.add(
                FrenzyDialogState(
                    id = dialogIdCounter,
                    index = dialogIdCounter,
                    title = config.titlePool.random(),
                    text = config.textPool.random(),
                    buttonText = config.buttonPool.random(),
                    offsetXFraction = Random.nextFloat() * 0.75f,
                    offsetYFraction = Random.nextFloat() * 0.65f,
                    scale = Random.nextFloat() * 0.35f + 0.75f,
                )
            )
            dialogIdCounter++
            delay(config.dialogIntervalMs)
        }
    }

    LaunchedEffect(config) {
        delay(config.durationMs)
        delay(800)
        onDismiss()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF3D0000).copy(alpha = pulseAlpha)),
    ) {
        val maxW = maxWidth
        val maxH = maxHeight

        dialogs.forEach { dialog ->
            key(dialog.id) {
                FrenzyDialog(
                    dialogState = dialog,
                    config = config,
                    lifetimeMs = config.dialogLifetimeMs,
                    maxWidth = maxW,
                    maxHeight = maxH,
                    onDismiss = { dialogs.removeAll { it.id == dialog.id } },
                )
            }
        }
    }
}

@Composable
private fun FrenzyDialog(
    dialogState: FrenzyDialogState,
    config: FrenzyConfig,
    lifetimeMs: Long,
    maxWidth: androidx.compose.ui.unit.Dp,
    maxHeight: androidx.compose.ui.unit.Dp,
    onDismiss: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    var dismissed by remember { mutableStateOf(false) }

    val dialogWidth = 280.dp
    val totalCount = config.dialogCount.coerceAtLeast(1)
    val progress = (dialogState.index.toFloat() / (totalCount - 1).coerceAtLeast(1)).coerceIn(0f, 1f)
    val textFontSize = config.minFontSize + (config.maxFontSize - config.minFontSize) * progress

    LaunchedEffect(dialogState) {
        visible = true
        delay(lifetimeMs)
        if (!dismissed) {
            visible = false
            delay(300)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(150)) +
            scaleIn(initialScale = 0.6f, animationSpec = spring(dampingRatio = 0.5f)),
        exit = fadeOut(animationSpec = tween(150)) +
            scaleOut(targetScale = 0.7f, animationSpec = tween(150)),
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = (maxWidth - dialogWidth) * dialogState.offsetXFraction,
                    y = maxHeight * dialogState.offsetYFraction,
                )
                .graphicsLayer {
                    scaleX = dialogState.scale
                    scaleY = dialogState.scale
                },
        ) {
            Surface(
                modifier = Modifier.widthIn(max = dialogWidth),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFECE9D8),
                shadowElevation = 16.dp,
                border = BorderStroke(1.dp, Color(0xFF0054E3)),
            ) {
                Column {
                    // ── 标题栏 ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0054E3))
                            .padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = dialogState.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .clickable {
                                    dismissed = true
                                    visible = false
                                }
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "✕",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.width(2.dp))
                    }

                    HorizontalDivider(color = Color(0xFF0054E3), thickness = 1.dp)

                    // ── 图标 + 正文（红色病态大字） ──
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = "⚠",
                            fontSize = (textFontSize * 0.8f).sp,
                            color = Color.Red,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            text = dialogState.text,
                            fontSize = textFontSize.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    HorizontalDivider(color = Color(0xFFD4D0C8), thickness = 1.dp)

                    // ── 按钮栏 ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFECE9D8))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    dismissed = true
                                    visible = false
                                },
                            shape = RoundedCornerShape(2.dp),
                            color = Color(0xFFECE9D8),
                            border = BorderStroke(1.dp, Color(0xFF00309C)),
                            shadowElevation = 1.dp,
                        ) {
                            Text(
                                text = dialogState.buttonText,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }

    // 关闭按钮点击 → 动画结束后回调
    LaunchedEffect(visible) {
        if (!visible && dismissed) {
            delay(250)
            onDismiss()
        }
    }
}
