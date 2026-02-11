# EventFlow 

EventFlow is a modern native Android application designed for browsing, managing, and creating events. Built entirely with **Jetpack Compose**, it demonstrates comprehensive handling of cloud data synchronization, local storage for offline favorites, and media handling.

## Features

* **Event Discovery:** Browse a dynamic list of events with search functionality.
* **Advanced Filtering:** Filter events by date, category, and specific location.
* **Event Creation:** Users can create new events, including:
  * Image upload (integrated with **Cloudinary**).
  * Precise location selection via an interactive **Map Screen**.
* **Favorites System:** Save events to favorites using **Room Database** for offline access.
* **Real-time Data:** All event data is stored and synced via **Firebase Firestore**.
* **Event Detail:** Comprehensive view of event information.

## Tech Stack & Libraries

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Architecture:** MVVM with Clean Architecture principles
* **Dependency Injection:** Hilt
* **Network & Parsing:** OkHttp, Moshi
* **Cloud & Backend:**
  * **Firebase Firestore:** NoSQL database for event storage.
  * **Cloudinary:** Image management and optimization.
* **Local Storage:** Room Database (SQLite abstraction).
* **Maps:** Google Maps SDK (or similar implementation for location picking).
