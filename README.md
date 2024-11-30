# Smart Trash Bin
This project is a Smart Trash Bin application built using Kotlin and Jetpack Compose. It monitors the capacity of a trash bin and provides real-time updates on various gas levels (NH3, CO2, Acetone) to ensure safety and cleanliness.

## Features
- Real-time Capacity Monitoring: Displays the current capacity of the trash bin.
- Gas Level Monitoring: Monitors NH3, CO2, and Acetone levels and provides warnings if they exceed safe limits.
- Dynamic Animations: Includes floating trash animations when the bin is nearly full.

## Requirements
- Android Studio Ladybug | 2024.2.1 Patch 2
- Kotlin
- Gradle

## Setup
1. Clone the repository:  
`git clone https://github.com/arifamsar/smart-trash-bin.git
cd smart-trash-bin`
2. Open the project in Android Studio.  
3. Add your google-services.json file to the app directory.  
4. Build and run the project on an Android device or emulator.

## Usage
- The main screen displays the current capacity of the trash bin.
- Gas levels are shown with warnings if they exceed safe limits.
- Dynamic animations are triggered when the trash bin is nearly full.

## Code Structure
- MainActivity.kt: Contains the main activity and composable functions for the UI.
- FloatingTrashAnimation.kt: Contains the composable functions for the floating trash animations.