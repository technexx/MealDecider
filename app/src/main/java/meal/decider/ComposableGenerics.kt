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
            modifier = Modifier
                .padding(8.dp, 10.dp),
            fontSize = fontSize.sp,
            color = color,
            text = text.toString(),
            fontWeight = fontWeight
        )
    }
}

@Composable
fun ButtonUi(
    text: String,
    onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_400)),
    ) {
        RegText(text = text, fontSize = 18, color = Color.Black, fontWeight = FontWeight.Bold)
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