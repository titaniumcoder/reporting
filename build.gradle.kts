import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask

plugins {
    id("com.moowork.node") version "1.3.1" apply false

    kotlin("jvm") version "1.3.61" apply false
    kotlin("kapt") version "1.3.61" apply false
    kotlin("plugin.allopen") version "1.3.61" apply false
    kotlin("plugin.noarg") version "1.3.61" apply false

    id("com.github.johnrengelman.shadow") version "5.0.0" apply false
}

tasks {
    val test by registering(Task::class) {

    }

    val check by registering(Task::class) {

    }

    val clean by registering(Task::class) {

    }

    val copyClientResources by registering(Copy::class) {
        group = "build"
        description = "Copy client resources into server"

        from("${project(":client").buildDir}")
        into("${project(":server").buildDir}/resources/main/public")

        dependsOn(":client:build")
    }


    val assembleServerAndClient by registering(Task::class) {
        group = "build"
        description = "Build combined server & client JAR"

        dependsOn(copyClientResources, ":server:assemble")
    }

    val stage by registering {
        dependsOn(assembleServerAndClient)
    }
}

subprojects {
}

project(":client") {
    // apply(plugin = "com.moowork.node")
    apply<NodePlugin>()

    configure<NodeExtension> {
        version = "10.16.3"
        npmVersion = "6.11.2"
        download = true
    }

    tasks {

        val npmInstall by existing

        register<Task>("clean") {
            delete(project.buildDir)
        }
        register<NpmTask>("start") {
            group = "application"
            description = "Run the client app"
            setArgs(listOf("run", "start"))

            dependsOn(npmInstall)
        }

        register<NpmTask>("build") {
            group = "build"
            description = "Build the client bundle"
            setArgs(listOf("run", "build"))

            dependsOn(npmInstall)
        }

        val test by registering(NpmTask::class) {
            group = "verification"
            description = "Run the client tests"
            setArgs(listOf("run", "test"))

            dependsOn(npmInstall)
        }
    }
}
