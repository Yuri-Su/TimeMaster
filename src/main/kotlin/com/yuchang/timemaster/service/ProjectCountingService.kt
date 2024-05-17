package com.yuchang.timemaster.service

import com.intellij.openapi.components.Service

@Service
class ProjectCountingService {

    var openProjectCount: Long = 0
        get() = if (field > 0) field else 0

    fun increaseOpenProjectCount() {
        openProjectCount++
    }

    fun decreaseOpenProjectCount() {
        if (openProjectCount > 0) {
            openProjectCount--
        }

    }

    val isOpenProjectsLimitExceeded: Boolean
        get() = openProjectCount > MAX_OPEN_PROJECTS_LIMIT

    companion object {
        private const val MAX_OPEN_PROJECTS_LIMIT = 3
    }
}