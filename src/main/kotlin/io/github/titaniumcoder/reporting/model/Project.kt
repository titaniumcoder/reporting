package io.github.titaniumcoder.reporting.model

import org.bson.codecs.pojo.annotations.*
import javax.validation.constraints.Size

@BsonDiscriminator
data class Project(
        @BsonId val id: String,
        @BsonProperty("active") val active: Boolean,
        @BsonProperty("name") @Size(max = 100) val name: String,
        @BsonProperty("billable") val billable: Boolean,
        @BsonIgnore val clientId: String,
        @BsonProperty("timeEntries", useDiscriminator = true) val timeEntries: List<TimeEntry>
) {
    @BsonCreator
    constructor(
            @BsonId id: String,
            @BsonProperty("active") active: Boolean = true,
            @BsonProperty("name") @Size(max = 100) name: String,
            @BsonProperty("billable") billable: Boolean = true,
            @BsonProperty("timeEntries", useDiscriminator = true) timeEntries: List<TimeEntry> = listOf()
    ) : this(id = id, active = active, name = name, billable = billable, clientId = "", timeEntries = timeEntries)

}