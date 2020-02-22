<h1 align="center">Aimybox voice assistant</h1>
<a href="https://aimybox.com"><img src="https://i.imgur.com/qyCxMmO.gif" align="right"></a>

<h4 align="center">Open source voice assistant built on top of <a href="https://github.com/just-ai/aimybox-android-sdk">Aimybox SDK</a></h4>

<p align="center">
    <a href="https://gitter.im/aimybox/community"><img src="https://badges.gitter.im/amitmerchant1990/electron-markdownify.svg"></a>
    <a href="https://twitter.com/intent/follow?screen_name=aimybox"><img alt="Twitter Follow" src="https://img.shields.io/twitter/follow/aimybox.svg?label=Follow%20on%20Twitter&style=popout"></a>
    <a href="https://travis-ci.com/just-ai/aimybox-android-assistant"><img alt="Travis CI Build" src="https://api.travis-ci.org/just-ai/aimybox-android-assistant.svg?branch=master"></a>
    <a href="https://bintray.com/aimybox/aimybox-android-assistant/components/"><img alt="Bintray artifact" src="https://api.bintray.com/packages/aimybox/aimybox-android-assistant/components/images/download.svg"></a>
    
### iOS version is available [here](https://github.com/just-ai/aimybox-ios-assistant)

# Key Features

* Provides ready to use **UI components** for fast building of your voice assistant app
* Modular and independent from speech-to-text, text-to-speech and NLU vendors
* Provides ready to use speech-to-text and text-to-speech implementations like [Android platform speechkit](https://github.com/just-ai/aimybox-android-sdk/tree/master/google-platform-speechkit), [Google Cloud speechkit](https://github.com/just-ai/aimybox-android-sdk/tree/master/google-cloud-speechkit), [Houndify](https://github.com/just-ai/aimybox-android-sdk/tree/master/houndify-speechkit) or [Snowboy wake word trigger](https://github.com/just-ai/aimybox-android-sdk/tree/master/snowboy-speechkit)
* Works with any NLU providers like [Aimylogic](https://help.aimybox.com/en/article/aimylogic-webhook-5quhb1/) or [Dialogflow](https://help.aimybox.com/en/article/dialogflow-agent-cqdvjn/)
* Fully customizable and extendable, you can connect any other speech-to-text, text-to-speech and NLU services
* Open source under Apache 2.0, written in pure Kotlin
* Embeddable into any application or device running Android
* Voice skills logic and complexity is not limited by any restrictions
* Can interact with any local device services and local networks

# How to start using

Just clone this repository and try to build and run the app module 😉

If you want some details - there is how to do the same with your hands.

1. Create a new Android project with next dependencies in the _build.gradle_ file

```kotlin
    android {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    repositories {
        maven("https://dl.bintray.com/aimybox/aimybox-android-assistant/")
        maven("https://dl.bintray.com/aimybox/aimybox-android-sdk/")
    }
    
    dependencies {
        implementation("com.justai.aimybox:core:0.7.0")
        implementation("com.justai.aimybox:components:0.1.8")
    }
```


2. Add one or more dependencies of third party speech-to-text and text-to-speech libraries. For example

```kotlin
implementation("com.justai.aimybox:google-platform-speechkit:0.7.0")
```

3. Create a new project in [Aimybox console](https://help.aimybox.com/en/article/introduction-to-aimybox-web-console-n49kfr/), enable some voice skills and **copy your project's API key**.

4. Instantiate [Aimybox](https://github.com/just-ai/aimybox-android-sdk/blob/master/core/src/main/java/com/justai/aimybox/Aimybox.kt) in your [Application](https://github.com/just-ai/aimybox-android-assistant/blob/master/app/src/main/java/com/justai/aimybox/assistant/AimyboxApplication.kt) class like that

```kotlin
class AimyboxApplication : Application(), AimyboxProvider {

    companion object {
        private const val AIMYBOX_API_KEY = "your Aimybox project key"
    }

    override val aimybox by lazy { createAimybox(this) }

    private fun createAimybox(context: Context): Aimybox {
        val unitId = UUID.randomUUID().toString()

        val textToSpeech = GooglePlatformTextToSpeech(context)
        val speechToText = GooglePlatformSpeechToText(context)

        val dialogApi = AimyboxDialogApi(AIMYBOX_API_KEY, unitId)

        return Aimybox(Config.create(speechToText, textToSpeech, dialogApi))
    }
}
```

5. Add `FrameLayout` to your application's layout like this

```xml
<FrameLayout
    android:id="@+id/assistant_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

6. Add `AimyboxAssistantFragment` in your activity that uses this layout (like [here](https://github.com/just-ai/aimybox-android-assistant/blob/master/app/src/main/java/com/justai/aimybox/assistant/MainActivity.kt))

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_activity_main)

    supportFragmentManager.beginTransaction().apply {
        replace(R.id.assistant_container, AimyboxAssistantFragment())
        commit()
    }
}
```

Now you can run your application and tap a small microphone button in bottom right corner of the screen.
Try to say some phrase that corresponds to any of enabled voice skills in your Aimybox project.

_Your assistant will handle all job regarding speech recognition, processing, displaying and synthesising of the response._

# More details

Please refer to the [demo app](https://github.com/just-ai/aimybox-android-assistant/tree/master/app) to see how to use Aimybox library in your own project.

# Documentation

There is a full Aimybox documentation available [here](https://help.aimybox.com). It's better to start with our [Quick Start](https://help.aimybox.com/en/article/quick-start-s9rswy/) to make first steps with Aimybox.
