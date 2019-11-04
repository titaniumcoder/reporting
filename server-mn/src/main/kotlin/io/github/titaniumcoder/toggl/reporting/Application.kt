package io.github.titaniumcoder.toggl.reporting

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("io.github.titaniumcoder.toggl.reporting")
                .mainClass(Application.javaClass)
                .start()
    }
}
