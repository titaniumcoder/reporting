import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.moowork.node") version "1.3.1" apply false

    id("org.springframework.boot") version "2.2.0.M6" apply false
    id("io.spring.dependency-management") version "1.0.8.RELEASE" apply false
    kotlin("jvm") version "1.3.50" apply false
    kotlin("plugin.spring") version "1.3.50" apply false
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
        into("${project(":server").buildDir}/resources/main/static")

        dependsOn(":client:build")
    }


    val assembleServerAndClient by registering(Task::class/* , dependsOn:["copyClientResources", ":server:assemble"] */) {
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