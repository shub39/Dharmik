package com.shub39.dharmik.bhagvad_gita.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeAction
import com.shub39.dharmik.bhagvad_gita.presentation.home.HomeState
import com.shub39.dharmik.core.domain.AppTheme
import com.shub39.dharmik.core.domain.VerseCardState
import com.shub39.dharmik.core.presentation.components.SettingSlider
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Github
import compose.icons.fontawesomeicons.solid.Globe
import dharmik.composeapp.generated.resources.Res
import dharmik.composeapp.generated.resources.app_theme
import dharmik.composeapp.generated.resources.app_theme_desc
import dharmik.composeapp.generated.resources.verse_state
import dharmik.composeapp.generated.resources.verse_state_desc
import dharmik.composeapp.generated.resources.verses_font_size
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsSection(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
    val listState = rememberLazyListState()
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(top = 16.dp, bottom = 60.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(Res.string.verse_state_desc)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                            VerseCardState.entries.forEach { vcState ->
                                ToggleButton(
                                    checked = vcState == state.verseCardState,
                                    onCheckedChange = { onAction(HomeAction.OnSetVerseCardState(vcState)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = vcState.fullName
                                    )
                                }
                            }
                        }
                    }
                },

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
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(Res.string.app_theme_desc)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
                            AppTheme.entries.forEach { theme ->
                                ToggleButton(
                                    checked = theme == state.theme.appTheme,
                                    onCheckedChange = { onAction(HomeAction.OnSetAppTheme(theme)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = stringResource(theme.label)
                                    )
                                }
                            }
                        }
                    }
                },
            )
        }

        item { HorizontalDivider() }

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
    }
}