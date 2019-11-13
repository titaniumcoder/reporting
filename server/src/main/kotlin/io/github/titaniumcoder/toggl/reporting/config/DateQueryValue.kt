package io.github.titaniumcoder.toggl.reporting.config

import io.micronaut.core.convert.format.Format
import io.micronaut.http.annotation.QueryValue
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

// @Documented
@Target(VALUE_PARAMETER, ANNOTATION_CLASS)
@QueryValue("from")
@Format("yyyy-MM-dd")
@Retention(RUNTIME)
annotation class DateQueryValue
