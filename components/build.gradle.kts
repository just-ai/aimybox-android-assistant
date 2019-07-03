dependencies {
    implementation(Libraries.Android.appCompat)
    implementation(Libraries.Android.constraintLayout)
    implementation(Libraries.Android.ktx)
    implementation(Libraries.Android.material)
    implementation(Libraries.Android.lifecycle)

    implementation(Libraries.Kotlin.stdLib)
    batchImplementation(Libraries.Kotlin.coroutines)

    implementation("com.justai.aimybox:core:${Versions.Aimybox.core}")

    implementation(Libraries.Android.glide)
    annotationProcessor(Libraries.Android.glideCompiler)
}
