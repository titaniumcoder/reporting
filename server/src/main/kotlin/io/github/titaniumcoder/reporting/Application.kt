package io.github.titaniumcoder.reporting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServerSpringApplication

fun main(args: Array<String>) {
    runApplication<ServerSpringApplication>(*args)
}
