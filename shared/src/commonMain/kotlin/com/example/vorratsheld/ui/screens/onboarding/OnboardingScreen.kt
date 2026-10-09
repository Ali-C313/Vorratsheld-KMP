package com.example.vorratsheld.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vorratsheld.data.model.OnboardingPage
import com.example.vorratsheld.resources.Res
import com.example.vorratsheld.resources.onboarding_1_text
import com.example.vorratsheld.resources.onboarding_1_title
import com.example.vorratsheld.resources.onboarding_2_text
import com.example.vorratsheld.resources.onboarding_2_title
import com.example.vorratsheld.resources.onboarding_3_text
import com.example.vorratsheld.resources.onboarding_3_title
import com.example.vorratsheld.resources.onboarding_4_text
import com.example.vorratsheld.resources.onboarding_4_title
import com.example.vorratsheld.resources.onboarding_next
import com.example.vorratsheld.resources.onboarding_skip
import com.example.vorratsheld.resources.onboarding_start
import com.example.vorratsheld.ui.theme.VorratsheldTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private val pages = listOf(
    OnboardingPage(Res.string.onboarding_1_title, Res.string.onboarding_1_text, Color(0xFF3F6B3F)),
    OnboardingPage(Res.string.onboarding_2_title, Res.string.onboarding_2_text, Color(0xFF3A6470)),
    OnboardingPage(Res.string.onboarding_3_title, Res.string.onboarding_3_text, Color(0xFFC07A45)),
    OnboardingPage(Res.string.onboarding_4_title, Res.string.onboarding_4_text, Color(0xFFB8432F))
)

/** Wird nur beim ersten Start gezeigt – [onFinished] speichert das und geht weiter. */
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex
    val accent by animateColorAsState(pages[pagerState.currentPage].accent, tween(400))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Überspringen oben rechts (auf der letzten Seite unnötig)
        Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.CenterEnd) {
            if (!isLastPage) {
                TextButton(onClick = onFinished) {
                    Text(stringResource(Res.string.onboarding_skip), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AccentBlob(color = page.accent)
                    when (pageIndex) {
                        0 -> ScanIllustration()
                        1 -> OverviewIllustration()
                        2 -> ReminderIllustration()
                        else -> ShoppingIllustration()
                    }
                }

                Spacer(Modifier.height(40.dp))

                Text(
                    text = stringResource(page.title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(page.text),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // Seiten-Punkte: der aktive wird zur Pille
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            pages.indices.forEach { index ->
                val selected = index == pagerState.currentPage
                val width by animateDpAsState(if (selected) 28.dp else 8.dp, tween(300))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(if (selected) accent else MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }

        Button(
            onClick = {
                if (isLastPage) onFinished()
                else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = accent),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            AnimatedContent(
                targetState = isLastPage,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) }
            ) { last ->
                Text(
                    text = stringResource(if (last) Res.string.onboarding_start else Res.string.onboarding_next),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    VorratsheldTheme {
        OnboardingScreen(onFinished = {})
    }
}
