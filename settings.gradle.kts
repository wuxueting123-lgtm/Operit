pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://dl.bintray.com/rikkaw/Shizuku") }
        maven { url = uri("https://api.xposed.info/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}

rootProject.name = "Operit"
include(":app")
// [NDK禁用] include(":dragonbones")
// project(":dragonbones").projectDir = file("avator/dragonbones")
include(":terminal")
// [NDK禁用] include(":mnn")
// project(":mnn").projectDir = file("llm/mnn")
// [NDK禁用] include(":llama")
// project(":llama").projectDir = file("llm/llama")
// [NDK禁用] include(":mmd")
// project(":mmd").projectDir = file("avator/mmd")
// [NDK禁用] include(":fbx")
// project(":fbx").projectDir = file("avator/fbx")
include(":showerclient")
// [NDK禁用] include(":quickjs")
