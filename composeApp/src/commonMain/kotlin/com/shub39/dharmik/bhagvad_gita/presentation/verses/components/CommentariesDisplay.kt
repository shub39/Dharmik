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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.bhagvad_gita.domain.Commentaries
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.commentaries
import dharmik.composeapp.generated.resources.noto_regular
import org.jetbrains.compose.resources.Font
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

    Card(shape = MaterialTheme.shapes.large) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.commentaries),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.padding(6.dp))

            commentaryMap.forEach { (key, value) ->
                if (!value.isNullOrEmpty()) {
                    DialogPeek(
                        title = key,
                        translation = value,
                        onCopy = onCopy,
                        fontFamily = fontFamily
                    )
                }
            }
        }
    }
}