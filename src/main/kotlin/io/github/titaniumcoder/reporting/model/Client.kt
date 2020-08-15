package io.github.titaniumcoder.reporting.model

import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@BsonDiscriminator
class Client constructor(
        id: String,
        active: Boolean,
        @Size(max = 100) name: String,
        @Size(max = 255) notes: String?,
        @Min(0) @Max(5260320) maxMinutes: Int?,
        @Min(0) @Max(200000) rateInCentsPerHour: Int?,
        projects: List<Project> = listOf()) {

    constructor() : this("", false, "", null, null, null, listOf())

    @BsonId
    var id: String = id

    @BsonProperty("active")
    var active: Boolean = active

    @BsonProperty("name")
    var name: String = name

    @BsonProperty("notes")
    var notes: String? = notes

    @BsonProperty("maxMinutes")
    var maxMinutes: Int? = maxMinutes

    @BsonProperty("rateInCentsPerHour")
    var rateInCentsPerHour: Int? = rateInCentsPerHour

    @BsonProperty("projects", useDiscriminator = true)
    var projects: List<Project> = projects
}
