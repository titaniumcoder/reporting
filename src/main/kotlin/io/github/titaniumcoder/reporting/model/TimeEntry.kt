package io.github.titaniumcoder.reporting.model

import org.bson.codecs.pojo.annotations.*
import org.bson.types.ObjectId
import java.time.LocalDateTime
import javax.validation.constraints.Size

@BsonDiscriminator
data class TimeEntry(
        @BsonId val id: ObjectId,
        @BsonProperty("starting") val starting: LocalDateTime,
        @BsonProperty("ending") val ending: LocalDateTime,
        @BsonProperty("description") @Size(max = 200) val description: String?,
        @BsonProperty("billed") val billed: Boolean,
        @BsonIgnore val projectId: String?
) {
    @BsonCreator
    constructor(
            @BsonId id: ObjectId = ObjectId(),
            @BsonProperty("starting") starting: LocalDateTime,
            @BsonProperty("ending") ending: LocalDateTime,
            @BsonProperty("description") @Size(max = 200) description: String?,
            @BsonProperty("billed") billed: Boolean = false
    ) : this(id = id, starting = starting, ending = ending, description = description, billed = billed, projectId = null)
}