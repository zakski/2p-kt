kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":dsl-theory"))
            }
        }

        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                api(project(":test-solve"))
            }
        }

        jvm {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
            }
            test.defaultSourceSet {
                dependsOn(commonTest)
                dependsOn(main.defaultSourceSet)
            }
        }

        js {

            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
            }
            test.defaultSourceSet {
                dependsOn(commonTest)
                dependsOn(main.defaultSourceSet)
            }
        }
    }
}
