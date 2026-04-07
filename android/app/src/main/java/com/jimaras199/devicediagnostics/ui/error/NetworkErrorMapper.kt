package com.jimaras199.devicediagnostics.ui.error

import retrofit2.HttpException
import java.io.IOException

object NetworkErrorMapper {

    fun message(ex: Throwable): String = when (ex) {
        is HttpException -> httpMessage(ex.code())
        is IOException -> "Server unreachable. Check your connection."
        else -> ex.localizedMessage ?: "Unexpected error."
    }

    fun message(ex: Throwable, context: Context): String = when (context) {
        Context.AuthLogin -> when (ex) {
            is HttpException -> when (ex.code()) {
                400 -> "Invalid input. Please check email/password."
                401 -> "Wrong email or password."
                409 -> "Conflict."
                else -> "Server error (${ex.code()}). Try again."
            }
            is IOException -> "Server unreachable. Check your connection."
            else -> ex.localizedMessage ?: "Unexpected error."
        }

        Context.AuthRegister -> when (ex) {
            is HttpException -> when (ex.code()) {
                400 -> "Invalid input. Please check email/password."
                409 -> "Email already registered."
                else -> "Server error (${ex.code()}). Try again."
            }
            is IOException -> "Server unreachable. Check your connection."
            else -> ex.localizedMessage ?: "Unexpected error."
        }

        Context.DemoSeed -> when (ex) {
            is HttpException -> when (ex.code()) {
                409 -> "Demo already loaded"
                401 -> "Session expired. Please login again."
                else -> "Failed to load demo data (${ex.code()})"
            }
            is IOException -> "Server unreachable. Check your connection."
            else -> ex.localizedMessage ?: "Failed to load demo data."
        }

        Context.GenericLoad -> message(ex)
    }

    private fun httpMessage(code: Int): String = when (code) {
        401 -> "Session expired. Please login again."
        403 -> "Forbidden."
        404 -> "Not found."
        else -> "Server error ($code)."
    }

    enum class Context {
        AuthLogin,
        AuthRegister,
        DemoSeed,
        GenericLoad
    }
}