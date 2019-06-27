import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.batchImplementation(batch: List<String>) =
    batch.forEach { add("implementation", it) }

fun DependencyHandlerScope.batchTestImplementation(batch: List<String>) =
    batch.forEach { add("testImplementation", it) }

fun DependencyHandlerScope.batchAndroidTestImplementation(batch: List<String>) =
    batch.forEach { add("androidTestImplementation", it) }
