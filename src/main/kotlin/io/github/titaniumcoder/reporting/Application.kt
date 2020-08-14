package io.github.titaniumcoder.reporting

import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("io.github.titaniumcoder.reporting")
		.start()
}
