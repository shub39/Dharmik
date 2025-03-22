package com.shub39.dharmik.bhagvad_gita.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.shub39.dharmik.DharmikConfig
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeAction
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.presentation.components.DharmikDialog
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.solid.Globe
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_name
import dharmik.composeapp.generated.resources.app_theme
import dharmik.composeapp.generated.resources.app_theme_desc
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsSection(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    var showLicences by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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

        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(Res.string.app_name)
                    )
                },
                supportingContent = {
                    Text(
                        text = "${DharmikConfig.packageName} (${DharmikConfig.versionCode})"
                    )
                },
                trailingContent = {
                    FilledTonalIconButton(
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
        }

        item {
            ListItem(
                headlineContent = {
                    Text(
                        text = "Made by shub39"
                    )
                },
                supportingContent = {
                    Text(
                        "Maybe follow me on Github?"
                    )
                },
                trailingContent = {
                    Row {
                        FilledTonalIconButton(
                            onClick = {
                                uriHandler.openUri("https://github.com/shub39")
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
            ListItem(
                headlineContent = {
                    Text(
                        text = "About Libraries"
                    )
                },
                supportingContent = {
                    Text(
                        text = "Open Source Licences"
                    )
                },
                trailingContent = {
                    FilledTonalIconButton(
                        onClick = { showLicences = true }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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

    if (showThemePicker) {
        DharmikDialog(
            onDismissRequest = { showThemePicker = false }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                AppTheme.entries.forEach { theme ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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

    if (showLicences) {
        DharmikDialog(
            onDismissRequest = { showLicences = false }
        ) {
            LibrariesContainer(
                colors = LibraryDefaults.libraryColors(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    badgeBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    badgeContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}