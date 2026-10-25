/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.dharmik.shared.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.dharmik.shared.ui.endItemShape
import com.shub39.dharmik.shared.ui.leadingItemShape
import com.shub39.dharmik.shared.ui.theme.DharmikTheme
import com.shub39.dharmik.shared.ui.theme.getTitleFont
import dharmik.shared.ui.generated.resources.Res
import dharmik.shared.ui.generated.resources.bmc
import dharmik.shared.ui.generated.resources.buymeacoffee
import dharmik.shared.ui.generated.resources.github
import dharmik.shared.ui.generated.resources.github_sponsors
import dharmik.shared.ui.generated.resources.warning
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Shown instead of the RevenueCat paywall in the `foss` flavor, which ships without any billing
 * backend. Offers the donation links instead of a purchase flow.
 */
@Composable
fun FossPaywall(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    Scaffold { paddingValues ->
        Box(
            modifier = modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.size(100.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialShapes.VerySunny.toShape(),
                            ),
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.warning),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "You are using the FOSS variant",
                        style =
                            MaterialTheme.typography.titleLarge.copy(
                                textAlign = TextAlign.Center,
                                fontFamily = getTitleFont(),
                            ),
                    )

                    Text(
                        text =
                            "All features are Unlocked. If this app added value to you. Consider supporting me on",
                        style =
                            MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    FilledTonalButton(
                        onClick = { uriHandler.openUri("https://buymeacoffee.com/shub39") },
                        shape = leadingItemShape(),
                        modifier =
                            Modifier.fillMaxWidth().height(ButtonDefaults.MediumContainerHeight),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.buymeacoffee),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.MediumIconSize),
                        )

                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

                        Text(
                            text = stringResource(Res.string.bmc),
                            style =
                                ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
                        )
                    }

                    FilledTonalButton(
                        onClick = { uriHandler.openUri("https://github.com/sponsors/shub39") },
                        shape = endItemShape(),
                        modifier =
                            Modifier.fillMaxWidth().height(ButtonDefaults.MediumContainerHeight),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.github),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.MediumIconSize),
                        )

                        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

                        Text(
                            text = stringResource(Res.string.github_sponsors),
                            style =
                                ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DharmikTheme { FossPaywall() }
}
