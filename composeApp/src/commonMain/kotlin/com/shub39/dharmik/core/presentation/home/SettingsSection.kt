package com.shub39.dharmik.core.presentation.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.dark_mode
import dharmik.composeapp.generated.resources.github_mark
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SettingsSection(
    settingsUseCase: SettingsUseCase = koinInject()
) {
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()

    val isDark by settingsUseCase.getPrefIsDarkTheme()
        .collectAsStateWithLifecycle(initialValue = false)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.dark_mode)
                    )
                },
                trailingContent = {
                    Switch(
                        checked = isDark,
                        onCheckedChange = {
                            coroutineScope.launch {
                                settingsUseCase.setPrefIsDarkTheme(it)
                            }
                        }
                    )
                }
            )

            HorizontalDivider()
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

            HorizontalDivider()
        }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }
}