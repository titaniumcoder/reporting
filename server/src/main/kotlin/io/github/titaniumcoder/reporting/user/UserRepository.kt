package io.github.titaniumcoder.reporting.user

import javax.inject.Singleton

@Singleton
class UserRepository {
    fun count(): Int = TODO()
    fun findAll(): List<User> = TODO()
    fun findById(email: String): User? = TODO()
    fun existsById(email: String): Boolean = TODO("not implemented")
    fun save(user: User): User = TODO("not implemented")
    fun deleteById(email: String): Unit = TODO()
}
