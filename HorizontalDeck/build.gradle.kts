import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.chris.horizontaldeck"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

publishing {
    repositories {
        maven {
            name = "LocalStaging"
            // This tells Gradle to build the final bundle inside your project's /build/ folder
            url = uri(layout.buildDirectory.dir("staging-repo"))
        }
    }
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.leochrish"
            artifactId = "horizontal-deck"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Horizontal Deck View")
                description.set("* Each child overlaps the previous one by 50%. The layout automatically calculates" +
                        " internal padding to allow the first and last items to reach the horizontal center" +
                        " of the viewport. It also features dynamic scaling and z-index adjustments based" +
                        " on the scroll position, making the centered item appear larger and on top.")
                url.set("https://github.com/leochrish/Deck_Layout")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("leochrish")
                        name.set("Leoni Christopher")
                        email.set("leoleeu14a1352@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/leochrish/Deck_Layout.git")
                    developerConnection.set("scm:git:ssh://github.com/leochrish/Deck_Layout.git")
                    url.set("https://github.com/leochrish/Deck_Layout/tree/main")
                }
            }
        }
    }
}

signing {
    val isCi = System.getenv("CI") == "true"

    if (isCi) {
        // We are on GitHub: Grab the text key directly from the server's memory
        val keyId = System.getenv("GPG_KEY_ID")
        val password = System.getenv("GPG_PASSWORD")
        val key = System.getenv("GPG_PRIVATE_KEY")

        useInMemoryPgpKeys(keyId, key, password)
        sign(publishing.publications["release"])

    } else {
        // We are on your Mac: Use the native terminal and local.properties
        val localProperties = Properties()
        val propsFile = rootProject.file("local.properties")

        if (propsFile.exists()) {
            propsFile.inputStream().use { localProperties.load(it) }
            val keyId = localProperties.getProperty("signing.keyId")
            val password = localProperties.getProperty("signing.password")

            if (keyId != null && password != null) {
                useGpgCmd()
                project.extra["signing.gnupg.executable"] = "/opt/homebrew/bin/gpg"
                project.extra["signing.gnupg.keyName"] = keyId
                project.extra["signing.gnupg.passphrase"] = password

                sign(publishing.publications["release"])
            }
        }
    }
}