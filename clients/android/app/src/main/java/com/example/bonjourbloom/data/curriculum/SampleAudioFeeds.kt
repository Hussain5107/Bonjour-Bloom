package com.example.bonjourbloom.data.curriculum

data class AudioFeedPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val durationText: String,
    val icon: String,
    val sampleTextToSpeak: String,
    val expectedKeywords: List<String>
)

object SampleAudioFeeds {
    val presets = listOf(
        AudioFeedPreset(
            id = "bakery",
            title = "À la Boulangerie",
            subtitle = "Ordering fresh pastries and baguette in Paris",
            category = "Daily Life",
            durationText = "0:12",
            icon = "🥖",
            sampleTextToSpeak = "Bonjour madame ! Un croissant croustillant et une baguette tradition s'il vous plaît. Très bien, voilà pour vous !",
            expectedKeywords = listOf("croissant", "baguette", "s'il vous plaît", "bonjour")
        ),
        AudioFeedPreset(
            id = "nursery_song",
            title = "Frère Jacques",
            subtitle = "Traditional French children's melody with morning bells",
            category = "Song & Rhyme",
            durationText = "0:15",
            icon = "🔔",
            sampleTextToSpeak = "Frère Jacques, Frère Jacques, dormez-vous ? Dormez-vous ? Sonnez les matines ! Ding dang dong !",
            expectedKeywords = listOf("frère", "dormez-vous", "matines", "sonnez")
        ),
        AudioFeedPreset(
            id = "tgv_train",
            title = "Départ en TGV",
            subtitle = "Paris Gare de Lyon station announcement",
            category = "Travel",
            durationText = "0:14",
            icon = "🚄",
            sampleTextToSpeak = "Mesdames et messieurs, bienvenue à bord du TGV en direction de Marseille. Départ voie trois dans deux minutes. Bon voyage !",
            expectedKeywords = listOf("bienvenue", "TGV", "départ", "bon voyage")
        ),
        AudioFeedPreset(
            id = "milo_story",
            title = "Le Renard dans le Jardin",
            subtitle = "Milo discovering colorful blooming flowers",
            category = "Storytime",
            durationText = "0:18",
            icon = "🦊",
            sampleTextToSpeak = "Dans le beau jardin enchanté, Milo le petit renard observe trois fleurs rouges, deux papillons bleus et un grand soleil doré.",
            expectedKeywords = listOf("jardin", "renard", "fleurs", "papillons")
        ),
        AudioFeedPreset(
            id = "classroom",
            title = "La Classe de Français",
            subtitle = "Morning greeting and vocabulary practice",
            category = "School",
            durationText = "0:12",
            icon = "📚",
            sampleTextToSpeak = "Bonjour les élèves ! Ouvrez vos livres à la page cinq. Répétons ensemble : un, deux, trois, nous aimons le français !",
            expectedKeywords = listOf("élèves", "livres", "ensemble", "français")
        )
    )
}
