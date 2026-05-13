package com.example.loginmvvm.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.loginmvvm.data.local.dao.UserDao
import com.example.loginmvvm.data.local.entity.UserEntity
import java.security.MessageDigest

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun register(username: String, password: String): RegisterResult {
        val normalizedUsername = username.trim()
        if (userDao.findByUsername(normalizedUsername) != null) {
            return RegisterResult.UsernameTaken
        }

        return try {
            userDao.insertUser(
                UserEntity(
                    username = normalizedUsername,
                    passwordHash = password.sha256()
                )
            )
            RegisterResult.Success
        } catch (_: SQLiteConstraintException) {
            RegisterResult.UsernameTaken
        }
    }

    suspend fun login(username: String, password: String): UserEntity? {
        return userDao.login(username.trim(), password.sha256())
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { "%02x".format(it) }
    }
}

enum class RegisterResult {
    Success,
    UsernameTaken
}
