package com.shub39.dharmik.bhagvad_gita.presentation.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeAction
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.github_mark
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsSection(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
//            ListItem(
//                headlineContent = {
//                    Text(
//                        text = stringResource(Res.string.dark_mode)
//                    )
//                },
//                trailingContent = {
//                    Switch(
//                        checked = isDark,
//                        onCheckedChange = {
//                            coroutineScope.launch {
//                                settingsUseCase.setPrefIsDarkTheme(it)
//                            }
//                        }
//                    )
//                }
//            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = "Made by shub39"
                    )
                },
                trailingContent = {
                    Row {
                        IconButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/shub39")
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.github_mark),
                                contentDescription = "Github",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = "Dharmic Data by bhavyakhatri"
                    )
                },
                trailingContent = {
                    Row {
                        IconButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/bhavykhatri/DharmicData")
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.github_mark),
                                contentDescription = "Github",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}