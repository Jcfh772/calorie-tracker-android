# Calorie Tracker for Android

An Android app for logging meals and tracking daily calories. You search for a food and the app fetches its nutrition from the CalorieNinjas API, then scales it to your portion size. You can attach a photo from the camera or gallery, which is uploaded to Firebase Storage. Meals are saved on the device with Room and add up against a 2,000 kcal daily goal.

Built for the Mobile Application Development unit at Curtin University (2024).

## Features

- **Food search.** Type a food such as "chicken rice" and get calories, fat, protein and carbohydrates per 100 g from the [CalorieNinjas](https://calorieninjas.com/api) API. The values are then scaled to the portion you enter in grams.
- **Meal log.** Each meal is tagged as breakfast, lunch, dinner or a snack and saved in a local Room database. The home screen shows today's meals and your running total against the daily goal.
- **Edit and delete.** Change a logged meal's food name or nutrition values inline, or remove it.
- **Manual entry.** If the API doesn't know a food, a dialog lets you type in the nutrition values yourself.
- **Meal photos.** Take a photo or pick one from the gallery, with runtime permission prompts. The photo uploads to Firebase Storage with a progress bar, and the meal list shows it next to the meal.

## Architecture

```
ui/            Jetpack Compose screens and components, Navigation Compose
  screens/     MainScreen (today's log), FoodSearchScreen (search, photo, add)
  components/  MealList, MealItem, ManualEntryDialog, MealTypeDropDown, ...
data/
  model/       MealViewModel: UI state, calorie totals, Firebase uploads
  database/    Room entity, DAO and database
  network/     Retrofit client for CalorieNinjas
```

The app follows MVVM. A single `MealViewModel` holds the UI state in Compose `mutableStateOf`, runs database and network calls in `viewModelScope` coroutines, and is shared by both screens.

## Tech stack

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Room · Retrofit + Gson · Kotlin coroutines · Firebase Storage · Accompanist Permissions

## Running it

You need Android Studio, a CalorieNinjas API key (free at [calorieninjas.com](https://calorieninjas.com/api)) and a Firebase project with Storage turned on.

1. Clone the repo and open it in Android Studio.
2. Add your API key to `local.properties` in the project root. Android Studio creates this file, and git ignores it:
   ```
   CALORIE_NINJAS_API_KEY=your_key_here
   ```
3. In the Firebase console, add an Android app with the package name `com.example.assignment2`. Download its `google-services.json` and put it in `app/`. This file is git-ignored too.
4. Run the app on an emulator or a phone with Android 8.0 (API 26) or newer.
