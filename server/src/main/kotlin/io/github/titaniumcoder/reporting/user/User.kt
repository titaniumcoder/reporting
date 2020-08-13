package io.github.titaniumcoder.reporting.user

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

//@Table("reporting_user")
data class User(
        @NotBlank
        @Email
//        @Id
        val email: String,

//        @Column("can_book")
        val canBook: Boolean = true,

//        @Column("can_view_money")
        val canViewMoney: Boolean = true,

        val admin: Boolean = true,

        @Transient val newUser: Boolean = true
) {
     fun getId(): String? = email

     fun isNew(): Boolean = newUser

//    @PersistenceConstructor
    constructor(email: String, canBook: Boolean, canViewMoney: Boolean, admin: Boolean) :
            this(email, canBook, canViewMoney, admin, false)
}

data class UserDto(
        val email: String,
        val canBook: Boolean,
        val canViewMoney: Boolean,
        val admin: Boolean,
        val clients: List<UserClientDto> = listOf()
)

data class UserClientDto(
        val clientId: String,
        val name: String
)

data class UserUpdateDto(
        @NotBlank
        @Email
        val email: String,

        val admin: Boolean,
        val canBook: Boolean,
        val canViewMoney: Boolean,

        val clients: List<String>
)
