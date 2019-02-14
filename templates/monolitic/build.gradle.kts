import de.itemis.mps.gradle.BuildLanguages
import de.itemis.mps.gradle.TestLanguages

buildscript {
    repositories {
        maven {
            url = uri("https://projects.itemis.de/nexus/content/repositories/mbeddr")
        }
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        "classpath"(group = "de.itemis.mps", name = "mps-gradle-plugin", version = "1.2+")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://projects.itemis.de/nexus/content/repositories/mbeddr")
    }
}


val mps = configurations.create("mps")
val antLib = configurations.create("antLib")

val mpsVersion = "2018.3.3"

val mpsHomeDir = file(project.findProperty("mpsHomeDir") ?: "$buildDir/mps")

dependencies {
    mps("com.jetbrains:mps:$mpsVersion")
    antLib("org.apache.ant:ant-junit:1.10.1")
}




ext["itemis.mps.gradle.ant.defaultScriptArgs"] = listOf("-Dmps.home=$mpsHomeDir", "-Dexample.home=$projectDir", "-Dmps.marco./example.home=$projectDir")
ext["itemis.mps.gradle.ant.defaultScriptClasspath"] = files(antLib.resolve()) + project.files("${project.getJdkHome()}/lib/tools.jar")


val resolveMps = tasks.register<Copy>("resolveMps") {
    from(mps.resolve().map {zipTree(it)})
    into(mpsHomeDir)
}

val allScripts = tasks.register<BuildLanguages>("allScripts") {
    script = file("$projectDir/scripts/build.xml")
    dependsOn(resolveMps)
}

val buildLanguages = tasks.register<BuildLanguages>("buildLanguages") {
    script = file("$buildDir/scripts/build.xml")
    dependsOn(allScripts)
}

tasks.register<TestLanguages>("testLanguages") {
    script = file("$buildDir/scripts/build-tests.xml")
    dependsOn(buildLanguages)
}
