android {
    defaultConfig {
        applicationId = "com.justai.aimybox.assistant"
    }
}

dependencies {
    implementation(project(":components"))

    implementation(Libraries.Android.appCompat)
    implementation(Libraries.Android.constraintLayout)
    implementation(Libraries.Android.ktx)
    implementation(Libraries.Android.material)
    implementation(Libraries.Android.lifecycle)

    implementation(Libraries.Kotlin.stdLib)
    batchImplementation(Libraries.Kotlin.coroutines)

    implementation("com.justai.aimybox:core:${Versions.aimybox}")
    implementation("com.justai.aimybox:google-platform-speechkit:${Versions.aimybox}")
}
