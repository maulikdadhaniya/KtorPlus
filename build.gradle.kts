import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
    id("maven-publish")
    id("signing")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "KtorPlus"
            isStatic = true
        }
    }
    
    jvm()
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            api(libs.ktor.client.logging)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
    }
}

android {
    namespace = "com.maulik.ktorplus"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Publishing Configuration
group = findProperty("LIBRARY_GROUP") as String? ?: "com.maulik.ktorplus"
version = findProperty("LIBRARY_VERSION") as String? ?: "1.0.0"

publishing {
    publications {
        // Configure all publications
        withType<MavenPublication> {
            groupId = group.toString()
            version = project.version.toString()
            
            pom {
                name.set(findProperty("LIBRARY_NAME") as String? ?: "KtorPlus")
                description.set(
                    findProperty("LIBRARY_DESCRIPTION") as String? 
                        ?: "A powerful Kotlin Multiplatform networking library built on Ktor"
                )
                url.set(findProperty("LIBRARY_URL") as String? ?: "https://github.com/yourusername/ktorplus")
                
                licenses {
                    license {
                        name.set(findProperty("LIBRARY_LICENSE") as String? ?: "Apache-2.0")
                        url.set(
                            findProperty("LIBRARY_LICENSE_URL") as String? 
                                ?: "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        )
                    }
                }
                
                developers {
                    developer {
                        id.set(findProperty("DEVELOPER_ID") as String? ?: "maulik")
                        name.set(findProperty("DEVELOPER_NAME") as String? ?: "Maulik Dadhaniya")
                        email.set(findProperty("DEVELOPER_EMAIL") as String? ?: "your.email@example.com")
                    }
                }
                
                scm {
                    connection.set(
                        findProperty("SCM_CONNECTION") as String? 
                            ?: "scm:git:git://github.com/yourusername/ktorplus.git"
                    )
                    developerConnection.set(
                        findProperty("SCM_DEVELOPER_CONNECTION") as String? 
                            ?: "scm:git:ssh://github.com/yourusername/ktorplus.git"
                    )
                    url.set(findProperty("SCM_URL") as String? ?: "https://github.com/yourusername/ktorplus")
                }
            }
        }
    }
    
    repositories {
        // Local Maven repository
        maven {
            name = "Local"
            url = uri("${rootProject.buildDir}/repo")
        }
        
        // Maven Central (uncomment when ready to publish)
        /*
        maven {
            name = "MavenCentral"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
                password = findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
            }
        }
        */
        
        // GitHub Packages (alternative)
        /*
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/yourusername/ktorplus")
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
        */
    }
}

// Signing configuration (optional, required for Maven Central)
/*
signing {
    // Use GPG agent for signing
    useGpgCmd()
    sign(publishing.publications)
}
*/

// Configure existing tasks (Kotlin Multiplatform plugin creates these automatically)
afterEvaluate {
    tasks.findByName("sourcesJar")?.apply {
        (this as? Jar)?.archiveClassifier?.set("sources")
    }
    
    tasks.findByName("javadocJar")?.apply {
        (this as? Jar)?.archiveClassifier?.set("javadoc")
    }
}
