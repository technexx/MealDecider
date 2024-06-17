package meal.decider

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private suspend fun startDismissWithExitAnimation(
    animateTrigger: MutableState<Boolean>,
    onDismissRequest: () -> Unit
) {
    animateTrigger.value = false
    delay(200)
    onDismissRequest()
}

@Composable
fun AnimatedComposable(
    any: Any? = Unit,
    modifier: Modifier = Modifier,
    backHandler: () -> Unit,
    contentAnimated: @Composable () -> Unit = { },
    contentStatic: @Composable () -> Unit,
){
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val animateTrigger = remember { mutableStateOf(false) }

    BackHandler {
        coroutineScope.launch {
            startDismissWithExitAnimation(animateTrigger, backHandler)
        }
    }

    LaunchedEffect(key1 = any) {
        launch {
            delay(0)
            animateTrigger.value = true
        }
    }

    Box(
        modifier = modifier
    ) {
        AnimatedScaleInTransition(
            animationEnter = slideInHorizontally (
                animationSpec = tween(200)
            ),
            animationExit = slideOutHorizontally(
                animationSpec = tween(200),
            ),
            visible = animateTrigger.value) {
            contentAnimated()
            contentStatic()
        }
    }
}

//Background color must be set in whichever columns/rows are being used in the content input, otherwise background will be the same as the Box here.
@Composable
fun AnimatedTransitionDialog(
    any: Any? = Unit,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val animateTrigger = remember { mutableStateOf(false) }

    //This delays our animateTrigger value, meaning our Dialog box (the faded grey background) launches, but its children composables do not until the delay is over.
    LaunchedEffect(key1 = any) {
        launch {
            delay(0)
            animateTrigger.value = true
        }
    }

    Dialog(onDismissRequest = {
        coroutineScope.launch {
            startDismissWithExitAnimation(animateTrigger, onDismissRequest)
        }
    }
    ) {
//        showLog("test", "dialog recomp")
        Box(
            modifier = modifier
        ) {
            AnimatedScaleInTransition(
                animationEnter = slideInHorizontally (
                    animationSpec = tween(200)
                ),
                animationExit = slideOutHorizontally(
                    animationSpec = tween(200),
                ),
                visible = animateTrigger.value) {
                Row() {
                    content()
                }
            }
        }
    }
}

@Composable
fun AnimatedTransitionVoid(
    modifier: Modifier = Modifier,
    any: Any? = Unit,
    content: @Composable () -> Unit,
) {
    val animateTrigger = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = any) {
        launch {
            delay(0)
            animateTrigger.value = true
        }
    }

    Box(
        modifier = modifier
    ) {
        AnimatedScaleInTransition(
            animationEnter = slideInHorizontally (
                animationSpec = tween(200)
            ),
            animationExit = slideOutHorizontally(
                animationSpec = tween(200),
            ),
            visible = animateTrigger.value) {
            Row() {
                content()
            }
        }
    }
}

@Composable
fun AnimatedScaleInTransition(
    animationEnter: EnterTransition,
    animationExit: ExitTransition,
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        enter = animationEnter,
        exit = animationExit,
        visible = visible,
        content = content
    )
}

@Composable
fun AnimatingDialog(
    isOpen: Boolean,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    enterDuration: Int = 300, // milliseconds
    exitDuration: Int = 200, // milliseconds
) {
    var animatedVisibility by remember { mutableStateOf(false) }
    val enterAnim = remember {
        androidx.compose.animation.core.Animatable(
            initialValue = 1f,
            if (animatedVisibility) 1f else 0f
        )
    }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            enterAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = enterDuration)
            )
        } else {
            enterAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = exitDuration)
            )
        }
        animatedVisibility = isOpen
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Dialog(
            onDismissRequest = onDismissRequest,
            content = {
                showLog("test", "content showing")
                AnimatedContent(
                    alpha = enterAnim.value,
                    content = content
                )
            }
        )
    }
}


@Composable
private fun AnimatedContent(
    alpha: Float,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalContentAlpha provides alpha) {
        Text("Fading in/out")
    }
}