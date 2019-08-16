<h1 align="center">
    <br>
    <a href="https://aimybox.com"><img src="https://app.aimybox.com/assets/images/aimybox.png"
                                                                    height="200"></a>
    <br><br>
    Aimybox voice assistant
</h1>

<h4 align="center">Open source voice assistant built on top of <a href="https://github.com/aimybox/aimybox-android-sdk">Aimybox SDK</a></h4>

<p align="center">
    <a href="https://gitter.im/aimybox/community"><img src="https://badges.gitter.im/amitmerchant1990/electron-markdownify.svg"></a>
    <a href="https://twitter.com/intent/follow?screen_name=aimybox"><img alt="Twitter Follow" src="https://img.shields.io/twitter/follow/aimybox.svg?label=Follow%20on%20Twitter&style=popout"></a>
    <a href="https://travis-ci.com/just-ai/aimybox-android-assistant"><img alt="Travis CI Build" src="https://api.travis-ci.org/just-ai/aimybox-android-assistant.svg?branch=master"></a>
    <a href="https://bintray.com/aimybox/aimybox-android-assistant/components/"><img alt="Bintray artifact" src="https://api.bintray.com/packages/aimybox/aimybox-android-assistant/components/images/download.svg"></a>
</p>

This repository contains all you need to embed intelligent voice assistant into your existing Android application, any Android device or Raspberry Pi.

# Key Features

* Modular and independent from ASR and TTS vendors
* Open source under Apache 2.0, written in pure Kotlin
* Embeddable into any application or device under Android
* Voice skills logic is not limited by any restrictions
* Works with any NLU providers like Aimylogic or Dialogflow
* Can interact with any local device services and local networks
* Fully customizable and extendable

# How to start using

```kotlin
    repositories {
        maven("https://dl.bintray.com/aimybox/aimybox-android-assistant/")
        maven("https://dl.bintray.com/aimybox/aimybox-android-sdk/")
    }
    
    dependencies {
        /* Core Aimybox package */
        implementation("com.justai.aimybox:core:${version}")
        
        /* UI components package */
        implementation("com.justai.aimybox:components:${version}")
        
        /* Optional modules */
        implementation("com.justai.aimybox:google-platform-speechkit:${version}")
        implementation("com.justai.aimybox:yandex-speechkit:${version}")
        implementation("com.justai.aimybox:snowboy-speechkit:${version}")
        implementation("com.justai.aimybox:houndify-speechkit:${version}")
    }
```

Please refer to the [demo app](https://github.com/just-ai/aimybox-android-assistant/tree/master/app) to see how to use Aimybox library in your own project.

# Documentation

There is a full Aimybox documentation available [here](https://help.aimybox.com)
