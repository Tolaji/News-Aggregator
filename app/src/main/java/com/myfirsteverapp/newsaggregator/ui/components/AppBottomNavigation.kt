// ui/components/AppBottomNavigation.kt
package com.yourname.newsaggregator.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppBottomNavigation(selectedItem: String, onItemSelected: (String) -> Unit) {
    val items = listOf("Home", "Explore", "Saved", "Profile")
    val icons = listOf(Icons.Default.Home, Icons.Default.Explore, Icons.Default.Bookmark, Icons.Default.Person)

    BottomNavigation(
        backgroundColor = Color.White,
        elevation = 8.dp
    ) {
        items.forEachIndexed { index, screen ->
            BottomNavigationItem(
                icon = { Icon(icons[index], contentDescription = screen) },
                label = { Text(screen) },
                selected = screen == selectedItem,
                onClick = { onItemSelected(screen) },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.Gray
            )
        }
    }
}