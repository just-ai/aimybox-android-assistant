# Sample voice assistant app

This is a sample voice assistant module built on top of [Aimybox Android SDK](https://github.com/just-ai/aimybox-android-sdk)
and [Aimybox UI components](https://github.com/just-ai/aimybox-android-assistant).

It shows how it's simple to build an own voice driven mobile assistant with nice customisable UI.

### How to build and run this app

1. Install [Android Studio](https://developer.android.com/studio/)
2. Create a **new project from Git version control** in Android Studio using `https://github.com/just-ai/aimybox-android-assistant` URL
3. Connect your Android device to your PC using USB cable (make sure [developer mode](https://developer.android.com/studio/debug/dev-options) is turned on)
4. Once your device was connected and recognised by Android Studio, click on the green play button in the Android Studio toolbar to build and run the **app** module

_Sample app is configured to work with pre-defined Aimybox project out of the box. That is why it recognises only English speech._

### How to connect this app to your own voice project

You have to switch the assistant app from the pre-defined voice project to your own one.
To do this you can use Aimybox Console or connect your assistant to the NLU engine directly.
Learn more about these two options in the [special documentation article](https://help.aimybox.com/en/article/voice-skills-overview-n49kfr/).

_You may wish to connect your assistant directly to any other NLU engine like [Dialogflow](https://github.com/just-ai/aimybox-android-sdk/tree/master/dialogflow-api) or [Rasa](https://github.com/just-ai/aimybox-android-sdk/tree/master/rasa-api)_

_As well you can use another [speech-to-text](https://help.aimybox.com/en/article/speech-to-text-components-1o8c1e5/) and [text-to-speech](https://help.aimybox.com/en/article/text-to-speech-components-btg1uk/) components._

### How to customise UI

Aimybox voice assistant is fully customisable.
Please learn more about how to customise UI in the [documentation](https://help.aimybox.com/en/article/android-ui-components-hvh9vw/).

### How to embed voice assistant into your own application

Aimybox voice assistant is designed to be embeddable into any third-party mobile application.
All you need to embed the assistant into your app is to add required dependencies to your project's build.gradle and then instantiate Aimybox with all required speech-=to-text, text-to-speech and dialog-api components.
Please learn how to do this looking through this module source code.