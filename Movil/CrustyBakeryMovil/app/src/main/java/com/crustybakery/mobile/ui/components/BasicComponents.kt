package com.crustybakery.mobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crustybakery.mobile.R
import com.crustybakery.mobile.ui.theme.*

@Composable
fun CrustyLogo(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Logo Crusty Bakery",
        modifier = modifier.size(if (compact) 72.dp else 138.dp)
    )
}

@Composable
fun ScreenTitle(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = CrustyBrown
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = CrustyMuted
            )
        }
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    password: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        leadingIcon = leadingIcon?.let { icon ->
            { Icon(icon, contentDescription = null, tint = CrustyBrown) }
        },
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CrustyBrown,
            unfocusedBorderColor = CrustyPink,
            focusedLabelColor = CrustyBrown,
            unfocusedLabelColor = CrustyMuted,
            focusedContainerColor = CrustySurface,
            unfocusedContainerColor = CrustySurface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    )
}

@Composable
fun AppButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CrustyBrown,
            contentColor = CrustyCream,
            disabledContainerColor = CrustySoftBrown,
            disabledContentColor = CrustyMuted
        ),
        contentPadding = PaddingValues(vertical = 15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CrustyPink),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrustyBrown),
        contentPadding = PaddingValues(vertical = 14.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CrustySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(18.dp), content = content)
    }
}

@Composable
fun PromoCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CrustyPink)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Dulces momentos,\nhechos para ti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = CrustyBrown
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Elige tu favorito y disfruta Crusty Bakery.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CrustyMuted
                )
            }
            Box(
                Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(CrustyCream),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Cake,
                    contentDescription = null,
                    tint = CrustyBrown,
                    modifier = Modifier.size(42.dp)
                )
            }
        }
    }
}

@Composable
fun Loading() {
    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CrustyBrown)
    }
}

@Composable
fun ErrorMessage(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RoundIconButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(CrustyCream)
    ) {
        Icon(icon, contentDescription, tint = CrustyBrown)
    }
}
