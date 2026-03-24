package com.crumbatelier.util

import com.crumbatelier.data.model.UserRole
import javax.inject.Qualifier

data class AppSession(
    val userId: String,
    val userName: String,
    val email: String,
    val role: UserRole
)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope