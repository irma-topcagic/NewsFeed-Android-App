# 📰 NewsFeed Android App

## 📌 Project Overview
**NewsFeed** is a mobile application developed in Android Studio that provides real-time news updates. The app fetches data from an external API and allows users to browse, filter, and save news articles for offline reading.

---

## 🚀 Key Features

### ⏱️ Real-Time Updates
* **Dynamic Fetching:** The app automatically refreshes the news feed every 3 seconds using API polling to ensure users always see the latest headlines.
* **API Integration:** Seamless connection to a news service API for live data retrieval.

### 🗄️ Local Storage & Persistence
* **Database Integration:** Articles are stored locally in a database, allowing for persistent access and performance optimization.
* **Featured vs. Standard:** Logic implemented to distinguish and visually prioritize "Featured" news over standard articles.

### 🔍 Content Management
* **Filtering:** Users can filter news by specific categories.
* **Sorting:** News items can be organized and displayed based on their publication date or by unwanted words.

---

## 🛠 Tech Stack
* **Platform:** Android
* **Development Environment:** Android Studio
* **Language:** Java/Kotlin 
* **Data Handling:** REST API, Local Database (SQLite/Room)
* **Real-time Logic:** Handlers/Schedulers for the 3s refresh rate

---


* **Models:** Data structures for News items.
