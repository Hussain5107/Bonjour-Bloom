# Third-party notices

No source code, branding, learning datasets, recordings or visual assets from the five evaluated reference applications have been incorporated into Bonjour Bloom.

The following MIT-licensed repositories were inspected as read-only architectural/product references on 2026-08-23:

- EchoTalk — Copyright (c) 2025 AliSol — https://github.com/alisolphp/EchoTalk
- PopMots — https://github.com/claudiabdm/popmots
- SpeakFlow — Copyright Harshan Aiyappa — https://github.com/Kimosabey/speak-flow
- WordPecker — https://github.com/baturyilmaz/wordpecker-app
- EchoType — https://github.com/Talljack/echo-type

Concepts assessed included local-first persistence, spaced review, recording failure states, vocabulary detail views and provider boundaries. All Bonjour Bloom implementation remains original. PopMots-linked Lexique/Kaikki/Wiktionary data and audio were explicitly not imported because code licensing does not establish content/audio licensing.

Runtime packages and their licenses remain governed by `package-lock.json` and their distributed license files. Milo artwork was generated specifically for this project using OpenAI image generation; French browser voices are supplied by the learner’s operating system/browser.
## Roadmap sources evaluated but not imported

- French-Dictionary — https://github.com/hbenbel/French-Dictionary — repository software is MIT-licensed, while the CSV data is generated from Kaikki/Wiktionary and requires separate attribution/share-alike review before any import. No code or data is included in this release.
- LanguageLearning — https://github.com/melling/LanguageLearning — CC0-licensed curated link directory. Linked third-party resources retain their own terms and require individual review. No links or content are included in this release.

These repositories are candidates for a later, administrator-reviewed content pipeline only. They are not dependencies of the responsive application.
