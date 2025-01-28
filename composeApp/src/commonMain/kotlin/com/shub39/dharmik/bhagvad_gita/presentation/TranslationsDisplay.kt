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
import com.shub39.dharmik.bhagvad_gita.domain.Translations
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.noto_regular
import dharmik.composeapp.generated.resources.round_content_copy_24
import dharmik.composeapp.generated.resources.translations
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TranslationsDisplay(
    translations: Translations,
    onCopy: (String) -> Unit,
    fontFamily: FontFamily = FontFamily(Font(Res.font.noto_regular))
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.translations),
            style = MaterialTheme.typography.titleLarge
        )

        translationsMap.forEach { (key, value) ->
            if (!value.isNullOrEmpty()) {
                TranslationComposable(
                    title = key,
                    translation = value,
                    onCopy = onCopy,
                    fontFamily = fontFamily
                )

                HorizontalDivider()
            }
        }
    }
}

@Composable
fun TranslationComposable(
    title: String,
    translation: String,
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
                       text = translation,
                       fontFamily = fontFamily
                   )
               } else {
                   Text(
                       text = translation,
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
                   onCopy(listOf(translation, title).joinToString("\n"))
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
