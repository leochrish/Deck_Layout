import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.chris.verticaldeck"
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
            artifactId = "vertical-deck"
            version = "1.0.3"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("Vertical Deck View")
                description.set("* Each child overlaps the previous one by 50%. The layout automatically calculates" +
                        " internal padding to allow the first and last items to reach the vertical center" +
                        " of the viewport. It also features dynamic scaling and z-index adjustments based" +
                        " on the scroll position, making the centered item appear larger and on top.")
                url.set("https://github.com/leochrish/Deck_Layout")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
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

    // 1. ALWAYS bypass Java BouncyCastle and use the native OS terminal GPG
    useGpgCmd()

    if (isCi) {
        // We are on GitHub: The key was just imported into Ubuntu's native keyring
        project.extra["signing.gnupg.keyName"] = System.getenv("GPG_KEY_ID")
        project.extra["signing.gnupg.passphrase"] = System.getenv("GPG_PASSWORD")
    } else {
        // We are on your Mac: Read from local.properties
        val localProperties = Properties()
        val propsFile = rootProject.file("local.properties")

        if (propsFile.exists()) {
            propsFile.inputStream().use { localProperties.load(it) }
            val keyId = localProperties.getProperty("signing.keyId")
            val password = localProperties.getProperty("signing.password")

            if (keyId != null && password != null) {
                project.extra["signing.gnupg.executable"] = "/opt/homebrew/bin/gpg" // Keep your Mac path
                project.extra["signing.gnupg.keyName"] = keyId
                project.extra["signing.gnupg.passphrase"] = password
            }
        }
    }

    sign(publishing.publications["release"])
}
