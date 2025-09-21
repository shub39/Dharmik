package com.shub39.dharmik.bhagvad_gita.presentation.verses.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.bhagvad_gita.domain.Translations
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.translations
import org.jetbrains.compose.resources.stringResource

@Composable
fun TranslationsDisplay(
    translations: Translations,
    onCopy: (String) -> Unit,
    fontSize: Float
) {
    val translationsMap = mapOf(
        "Sri Harikrishnadas Goenka" to translations.sriHarikrishnadasGoenka,
        "Swami Ramsukhdas" to translations.swamiRamsukhdas,
        "Swami Tejomayananda" to translations.swamiTejomayananda,
        "Swami Adidevananda" to translations.swamiAdidevananda,
        "Swami Gambirananda" to translations.swamiGambirananda,
        "Swami Sivananda" to translations.swamiSivananda,
        "Dr. S Sankaranarayan" to translations.drSSankaranarayan,
        "Shri Purohit Swami" to translations.shriPurohitSwami
    )

    Card(shape = MaterialTheme.shapes.large) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.translations),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.padding(6.dp))

            Column(
                modifier = Modifier.clip(MaterialTheme.shapes.medium)
            ) {
                translationsMap.forEach { (key, value) ->
                    if (value.isNotEmpty()) {
                        DialogPeek(
                            title = key,
                            content = value,
                            onCopy = onCopy,
                            fontSize = fontSize
                        )
                    }
                }
            }
        }
    }
}