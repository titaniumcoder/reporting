package io.github.titaniumcoder.reporting.model

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.time.LocalDateTime
import javax.validation.constraints.Size

@BsonDiscriminator
data class CurrentTimeEntry @BsonCreator constructor(
        @BsonId val id: ObjectId = ObjectId(),
        @BsonProperty("starting") val starting: LocalDateTime,
        @BsonProperty("description") @Size(max = 200) val description: String?,
        @BsonProperty("billed") val billed: Boolean = false,
        @BsonProperty("projectId") val projectId: String?
)