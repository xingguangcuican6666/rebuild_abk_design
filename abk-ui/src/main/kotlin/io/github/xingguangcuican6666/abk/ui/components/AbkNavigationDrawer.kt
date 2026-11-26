package io.github.xingguangcuican6666.abk.ui.components

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcon
import io.github.xingguangcuican6666.abk.ui.theme.AbkMotion
import io.github.xingguangcuican6666.abk.ui.theme.AbkShapes
import io.github.xingguangcuican6666.abk.ui.theme.AbkSpacing
import io.github.xingguangcuican6666.abk.ui.theme.AbkTheme

/** Side navigation is wider than the old 240dp rail so labels and headers breathe. */
private val DrawerWidth = 280.dp

/**
 * Side navigation, the wide-screen counterpart of [AbkBottomNavigation].
 *
 * Two shapes from one call:
 *  - `permanent = true` — the drawer sits beside [content] and always shows.
 *    The host chooses this below/above its own breakpoint (a common one is 840dp).
 *  - `permanent = false` — the drawer slides over [content] from the start edge
 *    while [open]; the rest of the screen is a scrim that closes it, and back
 *    closes it first (with the predictive-back gesture where the system offers it).
 *
 * Selection follows the web `.toc__link.is-active`: the row gets the `accent-soft` fill and
 * `accent-soft-ink` foreground, while the icon, type scale and hit box stay identical to
 * every other destination. Rows use `labelMedium` (14sp, the ABK nav size) and carry a
 * press state so a tap visibly lands.
 *
 * [header] is the brand/context slot above the destinations; [footer] holds the low
 * frequency actions. The drawer never persists whether it is open — the host owns that,
 * same as [AbkTheme] owns no preferences.
 */
@Composable
public fun AbkNavigationDrawer(
    items: List<AbkNavigationItem>,
    selectedId: String,
    onSelect: (String) -> Unit,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    permanent: Boolean = false,
    paneTitle: String = "导航",
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical),
    header: (@Composable ColumnScope.() -> Unit)? = null,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    require(items.size >= 2) { "Side navigation supports at least two destinations" }
    require(items.map { it.id }.distinct().size == items.size) { "Destination IDs must be unique" }
    if (permanent) {
        Box(modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize()) {
                DrawerPanel(items, selectedId, onSelect, paneTitle, windowInsets, header, footer,
                    Modifier.fillMaxHeight().drawEndLine())
                Box(Modifier.weight(1f).fillMaxHeight()) { content() }
            }
        }
        return
    }

    val c = AbkTheme.colors
    val reduceMotion = AbkTheme.reduceMotion
    val duration = if (reduceMotion) 0 else AbkMotion.Base
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    // Start edge is left in LTR and right in RTL: the panel begins fully outside it.
    val offScreen = { fraction: Int -> if (rtl) fraction else -fraction }
    val slideIn: EnterTransition =
        if (reduceMotion) fadeIn(tween(0))
        else slideInHorizontally(tween(duration, easing = FastOutSlowInEasing)) { offScreen(it) }
    val slideOut: ExitTransition =
        if (reduceMotion) fadeOut(tween(0))
        else slideOutHorizontally(tween(duration, easing = FastOutSlowInEasing)) { offScreen(it) }

    val scrimInteractions = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }

    // Back closes the drawer before it leaves the destination underneath.
    PredictiveBackHandler(enabled = open) { _ -> onOpenChange(false) }

    Box(modifier.fillMaxSize()) {
        content()
        AnimatedVisibility(visible = open, enter = fadeIn(tween(duration)), exit = fadeOut(tween(duration))) {
            Box(
                Modifier.fillMaxSize().background(c.scrim)
                    .clickable(scrimInteractions, indication = null) { onOpenChange(false) },
            )
        }
        AnimatedVisibility(visible = open, enter = slideIn, exit = slideOut) {
            // The panel catches every tap so a click inside never reaches the scrim below.
            Box(Modifier.fillMaxHeight().clickable(MutableInteractionSource(), indication = null) {}) {
                DrawerPanel(items, selectedId, onSelect, paneTitle, windowInsets, header, footer,
                    Modifier.fillMaxHeight().focusRequester(focusRequester).drawEndLine())
            }
        }
    }
    if (open) {
        // Move focus into the drawer so TalkBack and the keyboard start inside it.
        LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    }
}

@Composable
private fun DrawerPanel(
    items: List<AbkNavigationItem>,
    selectedId: String,
    onSelect: (String) -> Unit,
    paneTitle: String,
    windowInsets: WindowInsets,
    header: (@Composable ColumnScope.() -> Unit)?,
    footer: (@Composable ColumnScope.() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val c = AbkTheme.colors
    Column(
        modifier.width(DrawerWidth).fillMaxHeight().background(c.raised)
            .windowInsetsPadding(windowInsets).semantics { this.paneTitle = paneTitle },
    ) {
        if (header != null) {
            Column(
                Modifier.fillMaxWidth()
                    .padding(start = AbkSpacing.Xl, end = AbkSpacing.Xl, top = AbkSpacing.Xl, bottom = AbkSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(AbkSpacing.Md),
                content = header,
            )
            AbkDivider()
        }
        Column(
            Modifier.fillMaxWidth().selectableGroup()
                .padding(horizontal = AbkSpacing.Md, vertical = AbkSpacing.Md),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items.forEach { item -> DrawerRow(item, item.id == selectedId, onSelect) }
        }
        Spacer(Modifier.weight(1f))
        if (footer != null) {
            AbkDivider()
            Column(
                Modifier.fillMaxWidth().padding(horizontal = AbkSpacing.Sm, vertical = AbkSpacing.Sm),
                content = footer,
            )
        }
    }
}

@Composable
private fun DrawerRow(item: AbkNavigationItem, selected: Boolean, onSelect: (String) -> Unit) {
    val c = AbkTheme.colors
    val interactions = remember(item.id) { MutableInteractionSource() }
    val focused by interactions.collectIsFocusedAsState()
    val pressed by interactions.collectIsPressedAsState()
    // Same foreground as the web `.toc__link.is-active`: the accent container's ink.
    val foreground = when {
        !item.enabled -> c.inkFaint
        selected -> c.accent.onContainer
        else -> c.inkMuted
    }
    val background = when {
        selected -> c.accent.container
        pressed -> c.sunken
        else -> Color.Transparent
    }
    Row(
        Modifier.fillMaxWidth().clip(AbkShapes.Small).background(background)
            .abkFocusBorder(focused, foreground)
            .selectable(selected, interactions, indication = null, enabled = item.enabled, role = Role.Tab) { onSelect(item.id) }
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = AbkSpacing.Md, vertical = AbkSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AbkSpacing.Md),
    ) {
        AbkIcon(item.icon, null, tint = foreground, size = 22.dp)
        // ABK navigation type size (--fs-sm): 14sp medium, the same as AbkTabs.
        Text(item.label, style = MaterialTheme.typography.labelMedium, color = foreground)
    }
}

/** 1dp separator on the logical end of the drawer only. */
@Composable
private fun Modifier.drawEndLine(): Modifier {
    val line = AbkTheme.colors.line
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    return drawBehind {
        val x = if (rtl) 0f else size.width - 1.dp.toPx()
        drawRect(line, topLeft = Offset(x, 0f), size = size.copy(width = 1.dp.toPx()))
    }
}
