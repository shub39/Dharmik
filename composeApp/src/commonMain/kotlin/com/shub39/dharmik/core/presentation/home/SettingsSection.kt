package com.shub39.dharmik.core.presentation.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shub39.dharmik.core.domain.AppThemes
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_theme
import dharmik.composeapp.generated.resources.baseline_circle_24
import dharmik.composeapp.generated.resources.dark_mode
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SettingsSection(
    settingsUseCase: SettingsUseCase = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    val isDark by settingsUseCase.getPrefIsDarkTheme()
        .collectAsStateWithLifecycle(initialValue = false)
    val appTheme by settingsUseCase.getPrefAppTheme()
        .collectAsStateWithLifecycle(initialValue = AppThemes.YELLOW)

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
                        text = stringResource(Res.string.app_theme)
                    )
                },
                trailingContent = {
                    Row {
                        AppThemes.entries.forEach { theme ->
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        settingsUseCase.setPrefAppTheme(theme)
                                    }
                                },
                                modifier = Modifier.border(
                                    width = if (appTheme == theme) 3.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = if (appTheme == theme) 1f else 0f),
                                    shape = MaterialTheme.shapes.extraLarge
                                )
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_circle_24),
                                    contentDescription = "App Theme",
                                    tint = when (theme) {
                                        AppThemes.YELLOW -> if (isDark) Color(0xFFDBC66E) else Color(0xFF6D5E0F)
                                        AppThemes.LIME -> if (isDark) Color(0xFFB1D18A) else Color(0xFF4C662B)
                                        AppThemes.BLUE -> if (isDark) Color(0xFFAAC7FF) else Color(0xFF415F91)
                                    }
                                )
                            }
                        }
                    }
                }
            )

            HorizontalDivider()
        }
    }
}