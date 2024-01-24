package com.tasnim.chowdhury.jmiweatherapp.util

enum class CurrentStatus {
    SUCCESS,
    ERROR,
    LOADING
}
data class CurrentViewState<out T>(val status: CurrentStatus, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): CurrentViewState<T> {
            return CurrentViewState(CurrentStatus.SUCCESS, data, null)
        }
        fun <T> error(msg: String): CurrentViewState<T> {
            return CurrentViewState(CurrentStatus.ERROR, null, msg)
        }
        fun <T> loading(): CurrentViewState<T> {
            return CurrentViewState(CurrentStatus.LOADING, null, null)
        }
    }
}