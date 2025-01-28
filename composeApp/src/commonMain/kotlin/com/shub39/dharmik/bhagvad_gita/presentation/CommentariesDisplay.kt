package com.shub39.dharmik.bhagvad_gita.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import com.shub39.dharmik.bhagvad_gita.domain.Commentaries
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.commentaries
import dharmik.composeapp.generated.resources.noto_regular
import dharmik.composeapp.generated.resources.round_content_copy_24
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommentariesDisplay(
    commentaries: Commentaries,
    onCopy: (String) -> Unit,
    fontFamily: FontFamily = FontFamily(Font(Res.font.noto_regular))
) {
    val commentaryMap = mapOf(
        "Swami Ramsukhdas" to commentaries.swamiRamsukhdas,
        "Sri Harikrishnadas Goenka" to commentaries.sriHarikrishnadasGoenka,
        "Sri Anandgiri" to commentaries.sriAnandgiri,
        "Sri Dhanpati" to commentaries.sriDhanpati,
        "Sri Madhavacharya" to commentaries.sriMadhavacharya,
        "Sri Neelkanth" to commentaries.sriNeelkanth,
        "Sri Ramanuja" to commentaries.sriRamanuja,
        "Sri Sridhara Swami" to commentaries.sriSridharaSwami,
        "Sri Vedantadeshikacharya Venkatanatha" to commentaries.sriVedantadeshikacharyaVenkatanatha,
        "Swami Chinmayananda" to commentaries.swamiChinmayananda,
        "Sri Abhinavgupta" to commentaries.sriAbhinavgupta,
        "Sri Jayatritha" to commentaries.sriJayatritha,
        "Sri Madhusudan Saraswati" to commentaries.sriMadhusudanSaraswati,
        "Sri Purushottamji" to commentaries.sriPurushottamji,
        "Sri Shankaracharya" to commentaries.sriShankaracharya,
        "Sri Vallabhacharya" to commentaries.sriVallabhacharya,
        "Swami Sivananda" to commentaries.swamiSivananda,
        "Swami Gambirananda" to commentaries.swamiGambirananda,
        "Dr. S Sankaranarayan" to commentaries.drSSankaranarayan,
        "Swami Adidevananda" to commentaries.swamiAdidevananda
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.commentaries),
            style = MaterialTheme.typography.titleLarge
        )

        commentaryMap.forEach { (key, value) ->
            if (!value.isNullOrEmpty()) {
                CommentaryComposable(
                    title = key,
                    commentary = value,
                    onCopy = onCopy,
                    fontFamily = fontFamily
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun CommentaryComposable(
    title: String,
    commentary: String,
    onCopy: (String) -> Unit,
    fontFamily: FontFamily
) {
    var showComplete by remember { mutableStateOf(false) }

    ListItem(
        modifier = Modifier.clickable {
            showComplete = !showComplete
        },
        headlineContent = {
            AnimatedContent(
                targetState = showComplete
            ) {
                if (it) {
                    Text(
                        text = commentary,
                        fontFamily = fontFamily
                    )
                } else {
                    Text(
                        text = commentary,
                        fontFamily = fontFamily,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )
                }
            }
        },
        supportingContent = {
            Text(
                text = title,
                fontFamily = fontFamily
            )
        },
        trailingContent = {
            IconButton(
                onClick = {
                    onCopy(listOf(commentary, title).joinToString("\n"))
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.round_content_copy_24),
                    contentDescription = "Copy to clipboard"
                )
            }
        }
    )
}
