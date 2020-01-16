# Pocketsphinx powered sample voice assistant

This is a sample voice assistant module built on top of [Aimybox Android SDK](https://github.com/just-ai/aimybox-android-sdk),
[Aimybox UI components](https://github.com/just-ai/aimybox-android-assistant)
and [Pocketsphinx Speechkit](https://github.com/just-ai/aimybox-android-sdk/tree/master/pocketsphinx-speechkit) module.

It shows how it's simple to build voice assistant that recognises speech **without internet connection**.

### How to build and run this app

1. Install [Android Studio](https://developer.android.com/studio/)
2. Create a **new project from Git version control** in Android Studio using `https://github.com/just-ai/aimybox-android-assistant` URL
3. Connect your Android device to your PC using USB cable (make sure [developer mode](https://developer.android.com/studio/debug/dev-options) is turned on)
4. Once your device was connected and recognised by Android Studio, click on the green play button in the Android Studio toolbar to build and run the **app** module

### How to customise speech-to-text

This app uses simple JSGF grammar with pre-defined vocabulary (look at assets folder).
You have to define your own grammar and vocabulary to customise speech-to-text and train it to recognise custom sentences.