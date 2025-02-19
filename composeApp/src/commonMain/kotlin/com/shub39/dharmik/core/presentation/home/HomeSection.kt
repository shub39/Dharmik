package com.shub39.dharmik.core.presentation.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shub39.dharmik.app.Routes
import com.shub39.dharmik.atharva_veda.presentation.AvAction
import com.shub39.dharmik.atharva_veda.presentation.AvState
import com.shub39.dharmik.bhagvad_gita.presentation.BgAction
import com.shub39.dharmik.bhagvad_gita.presentation.BgState
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.atharva_veda
import dharmik.composeapp.generated.resources.baseline_bookmark_24
import dharmik.composeapp.generated.resources.baseline_favorite_border_24
import dharmik.composeapp.generated.resources.bhagvad_gita
import dharmik.composeapp.generated.resources.bookmarks
import dharmik.composeapp.generated.resources.favorites_template
import dharmik.composeapp.generated.resources.liked
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeSection(
    navController: NavController,
    avState: AvState,
    avAction: (AvAction) -> Unit,
    bgState: BgState,
    bgAction: (BgAction) -> Unit
) = Box {
    LazyColumn(
        modifier = Modifier
            .animateContentSize()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ListItem(
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_favorite_border_24),
                        contentDescription = "Favorite"
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.liked),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }

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
                           avAction(AvAction.SetKaandas(avState.favorites))
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
       }

        item {
            Spacer(modifier = Modifier.padding(30.dp))
        }

        item {
            ListItem(
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.baseline_bookmark_24),
                        contentDescription = "Favorite"
                    )
                },
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.bookmarks),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
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
                        text = "${avState.currentBookMark.first} : ${avState.currentBookMark.second}"
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            avAction(AvAction.LoadBookMark)
                            navController.navigate(Routes.AvVersesPage)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = "Navigate"
                        )
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}