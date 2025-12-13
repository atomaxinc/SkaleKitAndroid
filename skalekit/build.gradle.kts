plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.atomaxinc"
            artifactId = "skalekit"
            version = "1.0.0"

            artifact("${rootProject.projectDir}/releases/skalekit-1.0.0.aar")

            pom {
                name.set("SkaleKit")
                description.set("Android SDK for Skale Bluetooth coffee scales")
                url.set("https://github.com/atomaxinc/SkaleKitAndroid")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("atomaxinc")
                        name.set("Atomax Inc.")
                        email.set("nicebeans@atomax.cloud")
                    }
                }

                // 聲明依賴，讓用戶的專案自動引入
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")

                    val coreDep = dependenciesNode.appendNode("dependency")
                    coreDep.appendNode("groupId", "androidx.core")
                    coreDep.appendNode("artifactId", "core-ktx")
                    coreDep.appendNode("version", "1.12.0")
                    coreDep.appendNode("scope", "compile")

                    val annotationDep = dependenciesNode.appendNode("dependency")
                    annotationDep.appendNode("groupId", "androidx.annotation")
                    annotationDep.appendNode("artifactId", "annotation")
                    annotationDep.appendNode("version", "1.7.1")
                    annotationDep.appendNode("scope", "compile")
                }
            }
        }
    }
}
