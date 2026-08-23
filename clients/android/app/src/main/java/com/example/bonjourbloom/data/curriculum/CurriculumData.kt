package com.example.bonjourbloom.data.curriculum

import com.example.bonjourbloom.data.model.AgeBand
import com.example.bonjourbloom.data.model.DialogueLine
import com.example.bonjourbloom.data.model.Exercise
import com.example.bonjourbloom.data.model.ExerciseKind
import com.example.bonjourbloom.data.model.Lesson
import com.example.bonjourbloom.data.model.VocabularyItem

object CurriculumData {

    val vocabularyList: List<VocabularyItem> = listOf(
        // Greetings (0..7)
        VocabularyItem("bonjour", "Bonjour", "Hello", "", "n", "beginner phrase", "bon-zhoor", "Pre-A1", "greetings", "👋", "Bonjour Milo !", "Hello Milo!"),
        VocabularyItem("salut", "Salut", "Hi", "", "n", "beginner phrase", "sah-loo", "Pre-A1", "greetings", "😊", "Salut !", "Hi!"),
        VocabularyItem("bonsoir", "Bonsoir", "Good evening", "", "n", "beginner phrase", "bohn-swahr", "Pre-A1", "greetings", "🌙", "Bonsoir tout le monde", "Good evening everyone"),
        VocabularyItem("au-revoir", "Au revoir", "Goodbye", "", "n", "beginner phrase", "oh ruh-vwahr", "Pre-A1", "greetings", "👋", "Au revoir Milo", "Goodbye Milo"),
        VocabularyItem("merci", "Merci", "Thank you", "", "n", "beginner phrase", "mair-see", "Pre-A1", "greetings", "💛", "Merci beaucoup", "Thank you very much"),
        VocabularyItem("svp", "S’il vous plaît", "Please", "", "n", "beginner phrase", "seel voo pleh", "Pre-A1", "greetings", "🙏", "Un croissant, s’il vous plaît", "A croissant, please"),
        VocabularyItem("oui", "Oui", "Yes", "", "n", "beginner phrase", "wee", "Pre-A1", "greetings", "👍", "Oui, merci !", "Yes, thank you!"),
        VocabularyItem("non", "Non", "No", "", "n", "beginner phrase", "noh", "Pre-A1", "greetings", "🙅", "Non, merci", "No, thank you"),

        // Introductions (8..11)
        VocabularyItem("je-mappelle", "Je m’appelle…", "My name is…", "", "n", "beginner phrase", "zhuh mah-pel", "Pre-A1", "introductions", "🙋", "Je m’appelle Milo", "My name is Milo"),
        VocabularyItem("comment-tu", "Comment tu t’appelles ?", "What is your name?", "", "n", "beginner phrase", "koh-mahn too tah-pel", "Pre-A1", "introductions", "❓", "Bonjour ! Comment tu t’appelles ?", "Hello! What is your name?"),
        VocabularyItem("enchante", "Enchanté", "Nice to meet you", "", "n", "beginner phrase", "ahn-shahn-tay", "Pre-A1", "introductions", "🤝", "Enchanté de faire votre connaissance", "Nice to meet you"),
        VocabularyItem("je-suis", "Je suis…", "I am…", "", "n", "beginner phrase", "zhuh swee", "Pre-A1", "introductions", "🙂", "Je suis content", "I am happy"),

        // Numbers (12..22)
        VocabularyItem("un", "Un", "One", "", "n", "number", "uhn", "Pre-A1", "numbers", "1️⃣", "Un chat", "One cat"),
        VocabularyItem("deux", "Deux", "Two", "", "n", "number", "duh", "Pre-A1", "numbers", "2️⃣", "Deux chiens", "Two dogs"),
        VocabularyItem("trois", "Trois", "Three", "", "n", "number", "trwah", "Pre-A1", "numbers", "3️⃣", "Trois fleurs", "Three flowers"),
        VocabularyItem("quatre", "Quatre", "Four", "", "n", "number", "kahtr", "Pre-A1", "numbers", "4️⃣", "Quatre pommes", "Four apples"),
        VocabularyItem("cinq", "Cinq", "Five", "", "n", "number", "sank", "Pre-A1", "numbers", "5️⃣", "Cinq oiseaux", "Five birds"),
        VocabularyItem("six", "Six", "Six", "", "n", "number", "sees", "Pre-A1", "numbers", "6️⃣", "Six étoiles", "Six stars"),
        VocabularyItem("sept", "Sept", "Seven", "", "n", "number", "set", "Pre-A1", "numbers", "7️⃣", "Sept jours", "Seven days"),
        VocabularyItem("huit", "Huit", "Eight", "", "n", "number", "weet", "Pre-A1", "numbers", "8️⃣", "Huit papillons", "Eight butterflies"),
        VocabularyItem("neuf", "Neuf", "Nine", "", "n", "number", "nuhf", "Pre-A1", "numbers", "9️⃣", "Neuf livres", "Nine books"),
        VocabularyItem("dix", "Dix", "Ten", "", "n", "number", "dees", "Pre-A1", "numbers", "🔟", "Dix crayons", "Ten pencils"),
        VocabularyItem("age", "Quel âge as-tu ?", "How old are you?", "", "n", "beginner phrase", "kel ahzh ah too", "Pre-A1", "numbers", "🎂", "J’ai sept ans", "I am seven years old"),

        // Colors (23..32)
        VocabularyItem("rouge", "Rouge", "Red", "", "n", "color", "roozh", "Pre-A1", "colors", "🔴", "Une fleur rouge", "A red flower"),
        VocabularyItem("bleu", "Bleu", "Blue", "", "n", "color", "bluh", "Pre-A1", "colors", "🔵", "Le ciel bleu", "The blue sky"),
        VocabularyItem("vert", "Vert", "Green", "", "n", "color", "vair", "Pre-A1", "colors", "🟢", "Un arbre vert", "A green tree"),
        VocabularyItem("jaune", "Jaune", "Yellow", "", "n", "color", "zhohn", "Pre-A1", "colors", "🟡", "Le soleil jaune", "The yellow sun"),
        VocabularyItem("orange", "Orange", "Orange", "", "n", "color", "oh-rahnzh", "Pre-A1", "colors", "🟠", "Une orange douce", "A sweet orange"),
        VocabularyItem("rose", "Rose", "Pink", "", "n", "color", "rohz", "Pre-A1", "colors", "🌸", "Un flamant rose", "A pink flamingo"),
        VocabularyItem("noir", "Noir", "Black", "", "n", "color", "nwahr", "Pre-A1", "colors", "⚫", "Un chat noir", "A black cat"),
        VocabularyItem("blanc", "Blanc", "White", "", "n", "color", "blahn", "Pre-A1", "colors", "⚪", "Un nuage blanc", "A white cloud"),
        VocabularyItem("marron", "Marron", "Brown", "", "n", "color", "mah-rohn", "Pre-A1", "colors", "🟤", "Un renard marron", "A brown fox"),
        VocabularyItem("violet", "Violet", "Purple", "", "n", "color", "vee-oh-leh", "Pre-A1", "colors", "🟣", "Une robe violette", "A purple dress"),

        // Family (33..39)
        VocabularyItem("famille", "La famille", "Family", "La", "f", "noun", "lah fah-meey", "Pre-A1", "family", "👨‍👩‍👧‍👦", "Voici ma famille", "Here is my family"),
        VocabularyItem("mere", "La mère", "Mother", "La", "f", "noun", "lah mair", "Pre-A1", "family", "👩", "Ma mère est gentille", "My mother is kind"),
        VocabularyItem("pere", "Le père", "Father", "Le", "m", "noun", "luh pair", "Pre-A1", "family", "👨", "Mon père sourit", "My father smiles"),
        VocabularyItem("soeur", "La sœur", "Sister", "La", "f", "noun", "lah suhr", "Pre-A1", "family", "👧", "J’ai une sœur", "I have a sister"),
        VocabularyItem("frere", "Le frère", "Brother", "Le", "m", "noun", "luh frair", "Pre-A1", "family", "👦", "Mon frère joue", "My brother plays"),
        VocabularyItem("parents", "Les parents", "Parents", "Les", "m", "noun", "lay pah-rahn", "Pre-A1", "family", "🧑‍🤝‍🧑", "Mes chers parents", "My dear parents"),
        VocabularyItem("qui", "Qui est-ce ?", "Who is it?", "", "n", "beginner phrase", "kee ess", "Pre-A1", "family", "❓", "Qui est-ce ? C’est Milo !", "Who is it? It’s Milo!")
    )

    private fun findVocab(id: String): VocabularyItem? = vocabularyList.find { it.id == id }

    val lessonsList: List<Lesson> = listOf(
        Lesson(
            id = "bonjour",
            title = "Bonjour !",
            subtitle = "Greet, thank and say goodbye",
            outcome = "Greet people and be polite in French",
            minutes = 5,
            colorHex = "#EF6A5B",
            dialogue = listOf(
                DialogueLine("Milo", "Bonjour !", "Hello!"),
                DialogueLine("Learner", "Bonjour Milo ! Merci !", "Hello Milo! Thank you!")
            ),
            grammarNote = "In French, we greet warmly with 'Bonjour' in the daytime and 'Bonsoir' in the evening.",
            culturalNote = "Saying 'Bonjour' when entering a bakery or shop in France is a polite custom!",
            exercises = listOf(
                Exercise(
                    id = "bonjour-listen",
                    kind = ExerciseKind.LISTEN,
                    prompt = "Bonjour",
                    answer = "Bonjour",
                    options = listOf("Bonjour", "Salut", "Bonsoir"),
                    emoji = "👋",
                    audioText = "Bonjour"
                ),
                Exercise(
                    id = "bonjour-match",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Merci",
                    answer = "Thank you",
                    options = listOf("Thank you", "Hello", "Goodbye"),
                    emoji = "💛",
                    audioText = "Merci"
                ),
                Exercise(
                    id = "bonjour-picture",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Au revoir",
                    answer = "Goodbye",
                    options = listOf("Goodbye", "Please", "Yes"),
                    emoji = "👋",
                    audioText = "Au revoir"
                ),
                Exercise(
                    id = "bonjour-order",
                    kind = ExerciseKind.ORDER,
                    prompt = "Hello Milo",
                    answer = "Bonjour Milo",
                    options = listOf("Milo", "Bonjour", "Merci"),
                    emoji = "🦊"
                ),
                Exercise(
                    id = "bonjour-missing",
                    kind = ExerciseKind.MISSING,
                    prompt = "S’il vous ___",
                    answer = "plaît",
                    options = listOf("plaît", "merci", "salut"),
                    emoji = "🙏",
                    audioText = "S'il vous plaît"
                ),
                Exercise(
                    id = "bonjour-speak",
                    kind = ExerciseKind.SPEAK,
                    prompt = "Bonjour Milo",
                    answer = "Bonjour Milo",
                    options = emptyList(),
                    emoji = "🎙️",
                    audioText = "Bonjour Milo"
                ),
                Exercise(
                    id = "bonjour-dialogue",
                    kind = ExerciseKind.DIALOGUE,
                    prompt = "Milo says: 'Bonjour ! Comment ça va ?'",
                    answer = "Bonjour ! Merci !",
                    options = listOf("Bonjour ! Merci !", "Au revoir", "Non"),
                    emoji = "💬"
                ),
                Exercise(
                    id = "bonjour-review",
                    kind = ExerciseKind.REVIEW,
                    prompt = "How do you say 'Thank you' in French?",
                    answer = "Merci",
                    options = listOf("Merci", "Oui", "Salut"),
                    emoji = "💛",
                    audioText = "Merci"
                )
            )
        ),
        Lesson(
            id = "introductions",
            title = "Je m’appelle…",
            subtitle = "Introduce yourself and ask a name",
            outcome = "Introduce yourself and ask names politely",
            minutes = 6,
            colorHex = "#7B75C9",
            dialogue = listOf(
                DialogueLine("Milo", "Comment tu t’appelles ?", "What is your name?"),
                DialogueLine("Learner", "Je m’appelle Milo. Enchanté !", "My name is Milo. Nice to meet you!")
            ),
            grammarNote = "'Je m'appelle' literally means 'I call myself'. It is the most natural way to give your name.",
            culturalNote = "When meeting someone new in France, people often smile and say 'Enchanté !'",
            exercises = listOf(
                Exercise(
                    id = "intro-listen",
                    kind = ExerciseKind.LISTEN,
                    prompt = "Je m’appelle",
                    answer = "Je m’appelle…",
                    options = listOf("Je m’appelle…", "Comment tu t’appelles ?", "Enchanté"),
                    emoji = "🙋",
                    audioText = "Je m'appelle"
                ),
                Exercise(
                    id = "intro-match",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Enchanté",
                    answer = "Nice to meet you",
                    options = listOf("Nice to meet you", "What is your name?", "I am…"),
                    emoji = "🤝",
                    audioText = "Enchanté"
                ),
                Exercise(
                    id = "intro-picture",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Comment tu t’appelles ?",
                    answer = "What is your name?",
                    options = listOf("What is your name?", "My name is…", "Hello"),
                    emoji = "❓",
                    audioText = "Comment tu t'appelles ?"
                ),
                Exercise(
                    id = "intro-order",
                    kind = ExerciseKind.ORDER,
                    prompt = "My name is Milo",
                    answer = "Je m’appelle Milo",
                    options = listOf("Milo", "Je", "m’appelle"),
                    emoji = "🦊"
                ),
                Exercise(
                    id = "intro-missing",
                    kind = ExerciseKind.MISSING,
                    prompt = "Je ___ Milo",
                    answer = "suis",
                    options = listOf("suis", "nom", "tu"),
                    emoji = "🙂",
                    audioText = "Je suis Milo"
                ),
                Exercise(
                    id = "intro-speak",
                    kind = ExerciseKind.SPEAK,
                    prompt = "Je m’appelle Milo",
                    answer = "Je m’appelle Milo",
                    options = emptyList(),
                    emoji = "🎙️",
                    audioText = "Je m'appelle Milo"
                ),
                Exercise(
                    id = "intro-dialogue",
                    kind = ExerciseKind.DIALOGUE,
                    prompt = "Milo asks: 'Comment tu t’appelles ?'",
                    answer = "Je m’appelle Milo",
                    options = listOf("Je m’appelle Milo", "Merci beaucoup", "Au revoir"),
                    emoji = "💬"
                ),
                Exercise(
                    id = "intro-review",
                    kind = ExerciseKind.REVIEW,
                    prompt = "What does 'Enchanté' mean?",
                    answer = "Nice to meet you",
                    options = listOf("Nice to meet you", "Good night", "Please"),
                    emoji = "🤝",
                    audioText = "Enchanté"
                )
            )
        ),
        Lesson(
            id = "numbers",
            title = "Numbers 1–10",
            subtitle = "Count and say your age",
            outcome = "Count from 1 to 10 and state your age",
            minutes = 7,
            colorHex = "#E2A43C",
            dialogue = listOf(
                DialogueLine("Milo", "Quel âge as-tu ?", "How old are you?"),
                DialogueLine("Learner", "J’ai sept ans !", "I am seven years old!")
            ),
            grammarNote = "In French, we say 'J'ai ... ans' (I have ... years) rather than 'I am ... years old'.",
            culturalNote = "French children count with their thumbs first! (1 is the thumb, 2 is the index finger).",
            exercises = listOf(
                Exercise(
                    id = "num-listen",
                    kind = ExerciseKind.LISTEN,
                    prompt = "Trois",
                    answer = "Three",
                    options = listOf("Three", "One", "Two"),
                    emoji = "3️⃣",
                    audioText = "Trois"
                ),
                Exercise(
                    id = "num-match",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Cinq",
                    answer = "Five",
                    options = listOf("Five", "Four", "Six"),
                    emoji = "5️⃣",
                    audioText = "Cinq"
                ),
                Exercise(
                    id = "num-picture",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Dix",
                    answer = "Ten",
                    options = listOf("Ten", "Seven", "Nine"),
                    emoji = "🔟",
                    audioText = "Dix"
                ),
                Exercise(
                    id = "num-order",
                    kind = ExerciseKind.ORDER,
                    prompt = "I am six years old",
                    answer = "J’ai six ans",
                    options = listOf("six", "ans", "J’ai"),
                    emoji = "🎂"
                ),
                Exercise(
                    id = "num-missing",
                    kind = ExerciseKind.MISSING,
                    prompt = "Un, deux, ___",
                    answer = "trois",
                    options = listOf("trois", "huit", "dix"),
                    emoji = "🔢",
                    audioText = "Un, deux, trois"
                ),
                Exercise(
                    id = "num-speak",
                    kind = ExerciseKind.SPEAK,
                    prompt = "J’ai six ans",
                    answer = "J’ai six ans",
                    options = emptyList(),
                    emoji = "🎙️",
                    audioText = "J'ai six ans"
                ),
                Exercise(
                    id = "num-dialogue",
                    kind = ExerciseKind.DIALOGUE,
                    prompt = "Milo asks: 'Quel âge as-tu ?'",
                    answer = "J’ai sept ans",
                    options = listOf("J’ai sept ans", "Je m'appelle Milo", "Merci"),
                    emoji = "💬"
                ),
                Exercise(
                    id = "num-review",
                    kind = ExerciseKind.REVIEW,
                    prompt = "What number is 'Huit'?",
                    answer = "8",
                    options = listOf("8", "7", "9"),
                    emoji = "8️⃣",
                    audioText = "Huit"
                )
            )
        ),
        Lesson(
            id = "colors",
            title = "Colors",
            subtitle = "Recognize and name common colors",
            outcome = "Describe colors of everyday objects",
            minutes = 8,
            colorHex = "#47A887",
            dialogue = listOf(
                DialogueLine("Milo", "De quelle couleur est le ciel ?", "What color is the sky?"),
                DialogueLine("Learner", "C’est bleu !", "It is blue!")
            ),
            grammarNote = "Most color adjectives in French come AFTER the noun they describe (e.g. 'un chat noir').",
            culturalNote = "The French flag is 'Bleu, Blanc, Rouge' (Blue, White, Red).",
            exercises = listOf(
                Exercise(
                    id = "col-listen",
                    kind = ExerciseKind.LISTEN,
                    prompt = "Rouge",
                    answer = "Red",
                    options = listOf("Red", "Blue", "Green"),
                    emoji = "🔴",
                    audioText = "Rouge"
                ),
                Exercise(
                    id = "col-match",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Jaune",
                    answer = "Yellow",
                    options = listOf("Yellow", "Orange", "Pink"),
                    emoji = "🟡",
                    audioText = "Jaune"
                ),
                Exercise(
                    id = "col-picture",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Vert",
                    answer = "Green",
                    options = listOf("Green", "Black", "White"),
                    emoji = "🟢",
                    audioText = "Vert"
                ),
                Exercise(
                    id = "col-order",
                    kind = ExerciseKind.ORDER,
                    prompt = "It is red",
                    answer = "C’est rouge",
                    options = listOf("rouge", "C’est", "bleu"),
                    emoji = "🔴"
                ),
                Exercise(
                    id = "col-missing",
                    kind = ExerciseKind.MISSING,
                    prompt = "Le ciel est ___",
                    answer = "bleu",
                    options = listOf("bleu", "rose", "vert"),
                    emoji = "🌤️",
                    audioText = "Le ciel est bleu"
                ),
                Exercise(
                    id = "col-speak",
                    kind = ExerciseKind.SPEAK,
                    prompt = "C’est rouge",
                    answer = "C’est rouge",
                    options = emptyList(),
                    emoji = "🎙️",
                    audioText = "C'est rouge"
                ),
                Exercise(
                    id = "col-dialogue",
                    kind = ExerciseKind.DIALOGUE,
                    prompt = "Milo shows a pink rose: 'C’est quelle couleur ?'",
                    answer = "C’est rose",
                    options = listOf("C’est rose", "C’est noir", "Non merci"),
                    emoji = "🌸"
                ),
                Exercise(
                    id = "col-review",
                    kind = ExerciseKind.REVIEW,
                    prompt = "Which French word means 'White'?",
                    answer = "Blanc",
                    options = listOf("Blanc", "Noir", "Marron"),
                    emoji = "⚪",
                    audioText = "Blanc"
                )
            )
        ),
        Lesson(
            id = "family",
            title = "My Family",
            subtitle = "Identify close family members",
            outcome = "Introduce and talk about your family in French",
            minutes = 9,
            colorHex = "#4D86B5",
            dialogue = listOf(
                DialogueLine("Milo", "Qui est-ce ?", "Who is it?"),
                DialogueLine("Learner", "C’est ma mère et mon père !", "This is my mother and my father!")
            ),
            grammarNote = "Use 'mon' for masculine family members (mon père) and 'ma' for feminine members (ma mère).",
            culturalNote = "In France, family meals are special times to gather and share stories together.",
            exercises = listOf(
                Exercise(
                    id = "fam-listen",
                    kind = ExerciseKind.LISTEN,
                    prompt = "La mère",
                    answer = "Mother",
                    options = listOf("Mother", "Father", "Sister"),
                    emoji = "👩",
                    audioText = "La mère"
                ),
                Exercise(
                    id = "fam-match",
                    kind = ExerciseKind.PICTURE,
                    prompt = "Le père",
                    answer = "Father",
                    options = listOf("Father", "Brother", "Parents"),
                    emoji = "👨",
                    audioText = "Le père"
                ),
                Exercise(
                    id = "fam-picture",
                    kind = ExerciseKind.PICTURE,
                    prompt = "La sœur",
                    answer = "Sister",
                    options = listOf("Sister", "Mother", "Family"),
                    emoji = "👧",
                    audioText = "La sœur"
                ),
                Exercise(
                    id = "fam-order",
                    kind = ExerciseKind.ORDER,
                    prompt = "This is my mother",
                    answer = "C’est ma mère",
                    options = listOf("mère", "ma", "C’est"),
                    emoji = "👩"
                ),
                Exercise(
                    id = "fam-missing",
                    kind = ExerciseKind.MISSING,
                    prompt = "Voici mon ___",
                    answer = "frère",
                    options = listOf("frère", "sœur", "famille"),
                    emoji = "👦",
                    audioText = "Voici mon frère"
                ),
                Exercise(
                    id = "fam-speak",
                    kind = ExerciseKind.SPEAK,
                    prompt = "C’est ma mère",
                    answer = "C’est ma mère",
                    options = emptyList(),
                    emoji = "🎙️",
                    audioText = "C'est ma mère"
                ),
                Exercise(
                    id = "fam-dialogue",
                    kind = ExerciseKind.DIALOGUE,
                    prompt = "Milo asks: 'Qui est-ce ?'",
                    answer = "C’est mon père",
                    options = listOf("C’est mon père", "J'ai sept ans", "Au revoir"),
                    emoji = "👨"
                ),
                Exercise(
                    id = "fam-review",
                    kind = ExerciseKind.REVIEW,
                    prompt = "What is the French word for 'The Brother'?",
                    answer = "Le frère",
                    options = listOf("Le frère", "La sœur", "Le père"),
                    emoji = "👦",
                    audioText = "Le frère"
                )
            )
        )
    )
}
