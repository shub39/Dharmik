package com.shub39.dharmik.core.presentation.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shub39.dharmik.app.Routes
import com.shub39.dharmik.atharva_veda.presentation.AvState
import com.shub39.dharmik.bhagvad_gita.presentation.BgState
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.atharva_veda
import dharmik.composeapp.generated.resources.bhagvad_gita
import dharmik.composeapp.generated.resources.favorites_template
import org.jetbrains.compose.resources.stringResource

@Composable
fun FavoritesSection(
    navController: NavController,
    avState: AvState,
    bgState: BgState
) = Box {
    LazyColumn(
        modifier = Modifier
            .animateContentSize()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.bhagvad_gita)
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(Res.string.favorites_template, bgState.favorites.size)
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            navController.navigate(Routes.BgFavVersesPage)
                        },
                        enabled = bgState.favorites.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = "Navigate"
                        )
                    }
                }
            )

            HorizontalDivider()
        }

       item {
           ListItem(
               headlineContent = {
                   Text(
                       text = stringResource(Res.string.atharva_veda)
                   )
               },
               supportingContent = {
                   Text(
                       text = stringResource(Res.string.favorites_template, avState.favorites.size)
                   )
               },
               trailingContent = {
                   IconButton(
                       onClick = {
                           navController.navigate(Routes.AvFavVersesPage)
                       },
                       enabled = avState.favorites.isNotEmpty()
                   ) {
                       Icon(
                           imageVector = Icons.AutoMirrored.Default.ArrowForward,
                           contentDescription = "Navigate"
                       )
                   }
               }
           )

           HorizontalDivider()
       }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}