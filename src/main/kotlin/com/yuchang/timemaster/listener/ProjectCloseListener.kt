package com.yuchang.timemaster.listener

import com.google.gson.reflect.TypeToken
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.yuchang.timemaster.data.StatisticsData
import com.yuchang.timemaster.service.ProjectCountingService
import com.yuchang.timemaster.state.TimerMasterState
import com.yuchang.timemaster.util.Utils

internal class ProjectCloseListener : ProjectManagerListener {

    private val state = TimerMasterState.getInstance()

    override fun projectClosed(project: Project) {
        val projectCountingService =
            ApplicationManager.getApplication().getService(
                ProjectCountingService::class.java
            )
        projectCountingService.decreaseOpenProjectCount()
        val data = Utils.parse(state.statisticsData, TypeToken.get(StatisticsData::class.java))
        if (data.createDate == Utils.getTodayYmd()) {
            data.startCount = projectCountingService.openProjectCount
            state.statisticsData = Utils.stringify(data)
        }
    }
}