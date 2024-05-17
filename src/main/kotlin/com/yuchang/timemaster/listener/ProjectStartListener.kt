package com.yuchang.timemaster.listener

import com.google.gson.reflect.TypeToken
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.startup.ProjectActivity
import com.yuchang.timemaster.data.StatisticsData
import com.yuchang.timemaster.service.ProjectCountingService
import com.yuchang.timemaster.state.TimerMasterState
import com.yuchang.timemaster.util.Utils
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * @description 项目启动监听器
 * @author yuchang.su
 * @version 1.0
 * @date 2024/5/7 16:33:50
 */
class ProjectStartListener : ProjectActivity, Disposable {

    private val state = TimerMasterState.getInstance()

    override suspend fun execute(project: Project) {
        // 启动项目统计
        val application = ApplicationManager.getApplication()
        val projectCountingService = application.getService(ProjectCountingService::class.java)
        projectCountingService.increaseOpenProjectCount()
        val data = Utils.parse(state.statisticsData, TypeToken.get(StatisticsData::class.java))
        if (data.createDate == Utils.getTodayYmd()) {
            data.startCount = projectCountingService.openProjectCount
            state.statisticsData = Utils.stringify(data)
        }
        while (true) {
            delay(TimeUnit.SECONDS.toMillis(state.updateInterval.toLong()))
            run {
                // 避免多个项目运行时间统计多次, 增加满足以下规则才进行时间统计: 
                // 当前项目与配置信息一致, 或配置信息为空, 或配置信息内的项目不处于打开状态
                val projectPath = project.locationHash
                val firstOrNull =
                    ProjectManager.getInstance().openProjects.firstOrNull { it.locationHash == state.runProjectPath }
                val active = (System.currentTimeMillis() - state.activeTime) / 1000 <= state.activeInterval
                state.runProjectPath.takeIf { it == projectPath || it.isBlank() || firstOrNull == null }?.let {
                    state.runProjectPath = projectPath
                    val statisticsData = Utils.parse(state.statisticsData, TypeToken.get(StatisticsData::class.java))
                    if (statisticsData.createDate == Utils.getTodayYmd()) {
                        statisticsData.runTime += state.updateInterval
                        if (active) {
                            statisticsData.activeTime += state.updateInterval
                        }
                        state.statisticsData = Utils.stringify(statisticsData)
                    } else {
                        // 存储的如果不是当日的数据, 则将数据加入到历史数据, 然后再初始化数据
                        val arr = Utils.parse(state.historyData, object : TypeToken<MutableList<String>>() {})
                        arr.add(state.statisticsData)
                        state.historyData = Utils.stringify(arr)
                        val newData = StatisticsData()
                        newData.runTime = state.updateInterval.toLong()
                        if (active) {
                            newData.activeTime = state.updateInterval.toLong()
                        }
                        state.statisticsData = Utils.stringify(newData)
                    }
                }
            }
        }
    }

    override fun dispose() {

    }

}