package meal.decider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private suspend fun startDismissWithExitAnimation(
    animateTrigger: MutableState<Boolean>,
    onDismissRequest: () -> Unit
) {
    animateTrigger.value = false
    delay(300)
    onDismissRequest()
}

//Background color must be set in whichever columns/rows are being used in the content input, otherwise background will be the same as the Box here.
@Composable
fun AnimatedTransitionDialog(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val animateTrigger = remember { mutableStateOf(false) }

    //This delays our animateTrigger value, meaning our Dialog box (the faded grey background) launches, but its children composables do not until the delay is over.
    LaunchedEffect(key1 = Unit) {
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
        //This does not affect the white background surface in our Filters Dialog. It occurs from the execution of the above Dialog.
        Box(
            modifier = modifier
        ) {
            AnimatedScaleInTransition(time = 200, visible = animateTrigger.value) {
                content()
            }
        }
    }
}

@Composable
fun AnimatedTransitionVoid(
    content: @Composable () -> Unit,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val animateTrigger = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        launch {
            delay(0)
            animateTrigger.value = true
        }
    }
    Box(
    ) {
        AnimatedScaleInTransition(time = 200, visible = animateTrigger.value) {
            content()
        }
    }
}

@Composable
fun AnimatedScaleInTransition(
    time: Int,
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandHorizontally(
            animationSpec = tween(time)
        ),
        exit = shrinkHorizontally(
            animationSpec = tween(time)
        ),
        content = content
    )
}


@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.grey_50))) {
            content()
        }
    }
}