package meal.decider

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
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
    color: Int,
    onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
        },
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
        colors = ButtonDefaults.buttonColors(colorResource(color)),
    ) {
        RegText(text = text, fontSize = fontSize, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MaterialIconButton(
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    tint: Int,
    enabled: Boolean = true,
    onClick: () -> Unit) {
    IconButton(onClick = { if (enabled) onClick() }) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = colorResource(id = tint),
            modifier = modifier
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

@Composable
fun CardUi(
    color: Color,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier
            .padding(6.dp)
            .selectable(
                selected = true,
                onClick = {
                    onClick()
                }
            ),
    ) {
        content()
    }
}

@Composable
fun DropDownItemUi(
    text: String,
    fontSize: Int,
    color: Color,
    function: () -> Unit) {
    DropdownMenuItem(
        text = { RegText(
            text = text,
            fontSize = fontSize,
            color = color,
            modifier = Modifier.padding(12.dp)) },
        onClick = {
            function()
        }
    )
}