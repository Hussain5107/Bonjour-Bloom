package com.example.bonjourbloom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bonjourbloom.data.curriculum.CurriculumData
import com.example.bonjourbloom.data.model.VocabularyItem
import com.example.bonjourbloom.ui.theme.CoralAccent
import com.example.bonjourbloom.ui.theme.CoralLight
import com.example.bonjourbloom.ui.theme.CreamBackground
import com.example.bonjourbloom.ui.theme.CreamSurface
import com.example.bonjourbloom.ui.theme.LineBorder
import com.example.bonjourbloom.ui.theme.MutedText
import com.example.bonjourbloom.ui.theme.NavyPrimary

@Composable
fun WordsScreen(
    searchQuery: String,
    selectedTopic: String,
    onSearchChange: (String) -> Unit,
    onTopicChange: (String) -> Unit,
    onSpeakWord: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val topics = listOf("all", "greetings", "introductions", "numbers", "colors", "family")

    val filteredWords = remember(searchQuery, selectedTopic) {
        CurriculumData.vocabularyList.filter { item ->
            val matchTopic = selectedTopic == "all" || item.topic.equals(selectedTopic, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                    item.french.contains(searchQuery, ignoreCase = true) ||
                    item.english.contains(searchQuery, ignoreCase = true) ||
                    item.example.contains(searchQuery, ignoreCase = true)
            matchTopic && matchQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "MY WORD GARDEN",
            style = MaterialTheme.typography.labelSmall.copy(
                color = CoralAccent,
                letterSpacing = 1.5.sp
            )
        )
        Text(
            text = "Words you’re growing",
            style = MaterialTheme.typography.headlineLarge.copy(color = NavyPrimary)
        )
        Text(
            text = "Tap any card to hear its natural French sound.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MutedText),
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search French or English…") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MutedText
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("word_search_input"),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Topic chips row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(topics) { topic ->
                val isSelected = selectedTopic == topic
                val label = if (topic == "all") "All topics" else topic.replaceFirstChar { it.uppercase() }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(if (isSelected) CoralAccent else CreamSurface)
                        .border(1.dp, if (isSelected) CoralAccent else LineBorder, RoundedCornerShape(99.dp))
                        .clickable { onTopicChange(topic) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("topic_chip_$topic"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CreamSurface else NavyPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Words list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredWords, key = { it.id }) { word ->
                WordCard(
                    word = word,
                    onSpeak = { isSlow -> onSpeakWord(word.french, isSlow) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun WordCard(
    word: VocabularyItem,
    onSpeak: (isSlow: Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSpeak(false) }
            .testTag("word_card_${word.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CreamSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CoralLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = word.emoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (word.article.isNotBlank()) "${word.article} ${word.french}" else word.french,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    )
                }
                Text(
                    text = "${word.english} · ${if (word.gender == "m") "masculine" else if (word.gender == "f") "feminine" else word.partOfSpeech}",
                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                )
                if (word.example.isNotBlank()) {
                    Text(
                        text = "“${word.example}”",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Serif,
                            color = CoralAccent,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Audio button
            IconButton(
                onClick = { onSpeak(false) },
                modifier = Modifier.testTag("speak_word_${word.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Pronounce",
                    tint = CoralAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
