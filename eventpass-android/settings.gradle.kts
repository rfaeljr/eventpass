pluginManagement {
    repositories {
        google {
            content {
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
                includeGroupAndSubgroups("androidx")
            }
            isAllowInsecureProtocol = true
        }
        mavenCentral {
            isAllowInsecureProtocol = true
        }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            isAllowInsecureProtocol = true
        }
        mavenCentral {
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "eventpass-android"
include(":app")
