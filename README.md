# Doodle App - IA08 Technical I

**Course:** IA08 Mobile Development
**Date:** Fall 2025
**Language:** Kotlin
**Framework:** Jetpack Compose

## Overview

This application is a simple doodle/drawing tool designed for Android phones and tablets. It is built entirely using **Jetpack Compose**, bypassing the traditional XML layouts and custom View `onDraw()` methods. The app demonstrates the use of declarative UI patterns, state management (`remember`, `mutableState`), and Compose Canvas graphics.

## Features

### Core Requirements

  * **Drawing Canvas:** Utilizes `Canvas` and `detectDragGestures` to capture touch input and render paths in real-time.
  * **Tool Panel:** A control interface allowing the user to customize their brush.
      * **Brush Size:** A slider to adjust the stroke width (5px to 50px).
      * **Brush Colors:** A palette of 8 colors to choose from.
  * **State Management:** All drawing data is held in a `mutableStateListOf`, ensuring the UI recomposes automatically without manual `invalidate()` calls.

### **Bonus Features (+5 Points)**

  * **Undo Functionality:** Users can undo their last stroke. This is implemented using a secondary history stack.
  * **Redo Functionality:** Users can redo a stroke they accidentally removed.
  * **Smart History Clearing:** Starting a new stroke after an undo clears the redo stack to maintain logical history flow.

## How to Run

1.  **Clone the Repository:**
    ```bash
    https://github.com/talhaMah56/Doodle-App
    ```
2.  **Open in Android Studio:**
      * Open Android Studio and select "Open".
      * Navigate to the cloned folder and select it.
3.  **Sync Gradle:**
      * Allow Android Studio to download the necessary dependencies and sync the project.
4.  **Run the App:**
      * Connect an Android device or start an AVD Emulator (Pixel 6 or newer recommended).
      * Press the green **Run** button (Shift+F10).

## References & Resources

The following resources were used to build this prototype:

  * **Android Developers - Graphics in Compose:** [https://developer.android.com/develop/ui/compose/graphics/draw/overview](https://developer.android.com/develop/ui/compose/graphics/draw/overview)
  * **Android Developers - Gestures:** [https://developer.android.com/develop/ui/compose/touch-input/pointer-input/tap-and-press](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/tap-and-press)
  * **Jetpack Compose State:** [https://developer.android.com/develop/ui/compose/state](https://developer.android.com/develop/ui/compose/state)
  * *Course Lecture Notes on Jetpack Compose and Canvas.*

-----

*Developed by Talha Mahmood*
