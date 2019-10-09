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

tasks.register<Copy>("copyClientResources"/* , dependsOn: ":client:build" */) {
    group = "build"
    description = "Copy client resources into server"

    // from("${project(':client').buildDir}")
    // into("${project(':server').buildDir}/resources/main/static")
}

val assembleServerAndClient = tasks.register<Task>("assembleServerAndClient"/* , dependsOn:["copyClientResources", ":server:assemble"] */) {
    group = "build"
    description = "Build combined server & client JAR"
}

tasks.register<Task>("test") {

}

tasks.register<Task>("check") {

}

tasks.register<Task>("clean") {

}

tasks.register<Task>("stage")/* (dependsOn: ["assembleServerAndClient"]) */ {
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
        register<Task>("clean") {
            delete(project.buildDir)
        }
        register<NpmTask>("start") {
            /* , dependsOn: 'npm_install'*  */
            group = "application"
            description = "Run the client app"
            // args = listOf("run", "start")
        }

        register<NpmTask>("build") {
            /* , dependsOn: 'npm_install'*  */
            group = "build"
            description = "Build the client bundle"
            // args = listOf("run", "build")
        }

        register<NpmTask>("test") {
            /* , dependsOn: 'npm_install'*  */
            group = "verification"
            description = "Run the client tests"
            // args = listOf("run", "test")
        }
    }
}
