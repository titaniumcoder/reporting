package io.github.titaniumcoder.reporting.user

import io.github.titaniumcoder.reporting.Routines
import io.github.titaniumcoder.reporting.Tables.REPORTING_USER
import io.github.titaniumcoder.reporting.tables.records.ReportingUserRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL
import javax.inject.Singleton

@Singleton
class UserService(val create: DSLContext) {
    fun listUsers(): List<UserDto> {
        return create.selectFrom(REPORTING_USER)
                .orderBy(REPORTING_USER.USERNAME)
                .fetch()
                .map { r ->
                    UserDto(
                            r.username,
                            r.email,
                            r.canBook,
                            r.canViewMoney,
                            r.admin
                    )
                }.toList()
    }

    fun saveUser(user: User): UserDto {
        val r = ReportingUserRecord()
        r.username = user.username
        r.password = encryptPassword(user.password)
        r.email = user.email
        r.canBook = user.canBook
        r.admin = user.admin
        r.canViewMoney = user.canViewMoney

        // TODO isnert clients!!
        val result = create.executeInsert(r)
        if (result > 0) {
            return UserDto(
                    r.username,
                    r.email,
                    r.canBook,
                    r.canViewMoney,
                    r.admin,
                    listOf()
            )
        } else {
            // TODO throw a useful exception here
            throw IllegalArgumentException("username probably already exists!")
        }
    }

    private fun encryptPassword(password: String): String =
            create.select(Routines.crypt(DSL.concat(password), Routines.genSalt1("bf")))
                    .fetchOne().component1()
}
