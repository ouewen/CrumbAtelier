package com.crumbatelier.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.crumbatelier.data.model.Recipe
import com.crumbatelier.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrumbTopBar(title: String, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge, color = LightTextOnDark) },
        navigationIcon = { if (onBack != null) IconButton(onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = LightTextOnDark) } },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ChocolateBrown)
    )
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))) {
                if (!recipe.imageUrl.isNullOrBlank()) {
                    AsyncImage(model = recipe.imageUrl, contentDescription = recipe.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(CaramelBrown, ChocolateBrownLight))), contentAlignment = Alignment.Center) {
                        Text(recipe.title.first().uppercase(), style = MaterialTheme.typography.displayMedium, color = LightTextOnDark.copy(alpha = 0.5f))
                    }
                }
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, ChocolateBrownDark.copy(alpha = 0.3f)), startY = 100f)))
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(recipe.title, style = MaterialTheme.typography.titleMedium, color = BrownText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(recipe.description, style = MaterialTheme.typography.bodySmall, color = MutedText, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun CrumbButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, isLoading: Boolean = false) {
    Button(onClick = onClick, modifier = modifier.fillMaxWidth().height(52.dp), enabled = enabled && !isLoading, shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CaramelBrown, contentColor = Color.White, disabledContainerColor = WarmBeigeMuted),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun CrumbTextField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier, singleLine: Boolean = true, minLines: Int = 1, maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        modifier = modifier.fillMaxWidth(), singleLine = singleLine, minLines = minLines, maxLines = maxLines,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CaramelBrown, unfocusedBorderColor = DividerColor, focusedLabelColor = CaramelBrown, unfocusedLabelColor = MutedText, cursorColor = CaramelBrown, focusedTextColor = BrownText, unfocusedTextColor = BrownText)
    )
}

@Composable fun SectionTitle(text: String, modifier: Modifier = Modifier) = Text(text, style = MaterialTheme.typography.headlineSmall, color = ChocolateBrown, modifier = modifier)
@Composable fun LoadingContent(modifier: Modifier = Modifier) = Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = CaramelBrown) }
@Composable fun ErrorContent(message: String, modifier: Modifier = Modifier) = Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) { Text(message, style = MaterialTheme.typography.bodyMedium, color = ErrorRed) }
