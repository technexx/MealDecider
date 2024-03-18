package meal.decider

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//TODO: Card composition.

@Composable
fun RegText(
    text: String,
    fontSize: Int,
    color: Color,
    fontWeight: FontWeight = FontWeight.Normal,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        color = color,
        fontWeight = fontWeight,
        modifier = modifier
    )
}

@Composable
fun RegTextButton(
    text: String?,
    fontSize: Int,
    color: Color,
    fontWeight: FontWeight = FontWeight.Normal,
    onClick: () -> Unit) {
    TextButton(
        onClick = { onClick() }
    ) {
        Text(
            text = text.toString(),
            fontSize = fontSize.sp,
            color = color,
            fontWeight = fontWeight,
            modifier = Modifier
                .padding(8.dp, 10.dp),
        )
    }
}

@Composable
fun ButtonUi(
    text: String,
    fontSize: Int,
    onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_400)),
    ) {
        RegText(text = text, fontSize = fontSize, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

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