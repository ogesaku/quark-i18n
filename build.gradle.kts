plugins {
    id("build.java")
    id("build.test")
    id("build.coverage")
    id("build.publish")
}

group = "com.coditory.quark"
description = "Coditory Quark I18N - Message Internationalization Library"

dependencies {
    api(libs.slf4j.api)
    api(libs.jetbrains.annotations)
    implementation(libs.snakeyaml)
    implementation(libs.gson)
    implementation(libs.icu4j)
    testImplementation(libs.logback.classic)
    testImplementation(libs.spock.core)
    testImplementation(libs.awaitility)
    integrationTestImplementation(libs.jimfs)
}