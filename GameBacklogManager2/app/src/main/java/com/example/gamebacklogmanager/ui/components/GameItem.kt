package com.example.gamebacklogmanager.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gamebacklogmanager.R
import com.example.gamebacklogmanager.data.local.GameEntity
import com.example.gamebacklogmanager.data.model.GameStatus
import java.io.File

@Composable
fun GameCard(
    game: GameEntity,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Highlight "Now Playing" games with a primary border color
    val borderColor = if (game.status == GameStatus.NOW_PLAYING) 
        MaterialTheme.colorScheme.primary 
    else 
        MaterialTheme.colorScheme.outlineVariant

    OutlinedCard(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick(game.id) },
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            val imageModel = if (game.localBoxImagePath != null) {
                File(game.localBoxImagePath)
            } else {
                game.imageUrl
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = game.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp), // Removed rounded clip here, letting Card shape handle it (standard M3)
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_background), 
                error = painterResource(R.drawable.ic_launcher_background)
            )
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${game.progressHours.toInt()} HRS", // All caps for tech feel
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun GameListItem(
    game: GameEntity,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Color coding borders based on status
    val borderColor = when (game.status) {
        GameStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
        GameStatus.ABANDONED -> MaterialTheme.colorScheme.error
        GameStatus.NOW_PLAYING -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick(game.id) },
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageModel = if (game.localBoxImagePath != null) {
                File(game.localBoxImagePath)
            } else {
                game.imageUrl
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageModel)
                    .crossfade(true)
                    .build(),
                contentDescription = game.title,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${game.progressHours.toInt()} HRS PLAYED",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}