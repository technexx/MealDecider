package meal.decider

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MaterialIconButton(
    icon: ImageVector,
    description: String,
    tint: Int,
    onClick: () -> Unit) {
    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = colorResource(id = tint)
        )
    }
}

@Composable
fun CustomIconButton(
    image: Int,
    size: Int,
    description: String,
    tint: Int,
    onClick: () -> Unit) {
    IconButton(modifier = Modifier.size(size.dp),
        onClick = { onClick() }) {
        Icon(
            painter = painterResource(image),
            contentDescription = description,
            tint = colorResource(id = tint),
        )
    }
}