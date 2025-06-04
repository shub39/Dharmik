package com.shub39.dharmik.bhagvad_gita.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.DharmikConfig
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeAction
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.domain.VerseCardState
import com.shub39.dharmik.core.presentation.components.DharmikDialog
import com.shub39.dharmik.core.presentation.components.SettingSlider
import com.shub39.dharmik.core.presentation.components.simpleVerticalScrollbar
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.solid.Coffee
import compose.icons.fontawesomeicons.solid.Globe
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_name
import dharmik.composeapp.generated.resources.app_theme
import dharmik.composeapp.generated.resources.app_theme_desc
import dharmik.composeapp.generated.resources.support
import dharmik.composeapp.generated.resources.verse_state
import dharmik.composeapp.generated.resources.verse_state_desc
import dharmik.composeapp.generated.resources.verses_font_size
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsSection(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val listState = rememberLazyListState()
    val uriHandler = LocalUriHandler.current

    var showThemePicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .simpleVerticalScrollbar(listState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        headlineColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        supportingColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        trailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "${DharmikConfig.packageName} (${DharmikConfig.versionCode})"
                        )
                    },
                    trailingContent = {
                        OutlinedIconButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/shub39/Dharmik")
                            }
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Brands.Github,
                                contentDescription = "Github",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                Button(
                    onClick = {
                        uriHandler.openUri("https://buymeacoffee.com/shub39")
                    },
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Coffee,
                            contentDescription = "Coffee",
                            modifier = Modifier.size(24.dp)
                        )

                        Text(text = stringResource(Res.string.support))
                    }
                }
            }
        }

        item { HorizontalDivider(modifier = Modifier.padding(32.dp)) }

        item {
            SettingSlider(
                title = stringResource(Res.string.verses_font_size),
                value = state.fontSize,
                valueRange = 12f..24f,
                onValueChange = { onAction(HomeAction.SetFontSize(it)) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.verse_state)
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(Res.string.verse_state_desc)
                    )
                },
                trailingContent = {
                    FilledTonalIconButton(
                        onClick = { showLanguagePicker = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Edit"
                        )
                    }
                }
            )
        }

        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.app_theme)
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(Res.string.app_theme_desc)
                    )
                },
                trailingContent = {
                    FilledTonalIconButton(
                        onClick = { showThemePicker = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Edit"
                        )
                    }
                }
            )
        }

        item { HorizontalDivider(modifier = Modifier.padding(32.dp)) }

        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = "Dharmic Data"
                    )
                },
                supportingContent = {
                    Text(
                        text = "by bhavyakhatri"
                    )
                },
                trailingContent = {
                    Row {
                        FilledTonalIconButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/bhavykhatri/DharmicData")
                            }
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Brands.Github,
                                contentDescription = "Github",
                                modifier = Modifier.size(20.dp)
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
                        text = "Gita Supersite"
                    )
                },
                supportingContent = {
                    Text(
                        text = "by IIT Kanpur"
                    )
                },
                trailingContent = {
                    FilledTonalIconButton(
                        onClick = {
                            uriHandler.openUri("https://www.gitasupersite.iitk.ac.in/")
                        }
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Globe,
                            contentDescription = "Open In Browser",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.padding(60.dp))
        }
    }

    if (showLanguagePicker) {
        DharmikDialog(
            onDismissRequest = { showLanguagePicker = false }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                VerseCardState.entries.forEach { vcState ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAction(HomeAction.OnSetVerseCardState(vcState))
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = vcState.fullName)

                        RadioButton(
                            selected = state.verseCardState == vcState,
                            onClick = {
                                onAction(HomeAction.OnSetVerseCardState(vcState))
                            }
                        )
                    }
                }
            }
        }
    }

    if (showThemePicker) {
        DharmikDialog(
            onDismissRequest = { showThemePicker = false }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                AppTheme.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAction(HomeAction.OnSetAppTheme(theme))
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(theme.label))

                        RadioButton(
                            selected = state.theme.appTheme == theme,
                            onClick = {
                                onAction(HomeAction.OnSetAppTheme(theme))
                            }
                        )
                    }
                }
            }
        }
    }
}