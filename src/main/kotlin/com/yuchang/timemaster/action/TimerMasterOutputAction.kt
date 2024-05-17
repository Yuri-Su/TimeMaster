package com.yuchang.timemaster.action

import com.google.gson.reflect.TypeToken
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.yuchang.timemaster.data.StatisticsData
import com.yuchang.timemaster.state.TimerMasterState
import com.yuchang.timemaster.util.Utils
import java.time.LocalDate

/**
 * @description 输出操作
 * @author yuchang.su
 * @version 1.0
 * @date 2024/5/7 16:33:50
 */
class TimerMasterOutputAction : AnAction() {

    private val state = TimerMasterState.getInstance()

    override fun actionPerformed(e: AnActionEvent) {
        val data = Utils.parse(state.statisticsData, TypeToken.get(StatisticsData::class.java))
        // 获取今日数据
        val today = "🐻 Today (${Utils.getTodayYmd()})${System.lineSeparator()}${getBodyContent(data)}"
        // 对历史数据进行倒序排序
        val list = Utils.parse(state.historyData, object : TypeToken<MutableList<String>>() {})
            .map { Utils.parse(it, TypeToken.get(StatisticsData::class.java)) }
            .sortedByDescending { LocalDate.parse(it.createDate) }
        // 获取昨天的数据
        val yesterdayYmd = Utils.getYmd(LocalDate.now().minusDays(1))
        val yesterdayData = if (list.isNotEmpty() && list[0].createDate == yesterdayYmd) list[0] else StatisticsData()
        val yesterday = "🦁 Yesterday ($yesterdayYmd)${System.lineSeparator()}${getBodyContent(yesterdayData)}"
        // 获取过去 7 天的数据
        val weekYmd = Utils.getYmd(LocalDate.now().minusDays(7))
        val weekData = statistics(list, weekYmd)
        val week = "🐯 Last 7 days ($weekYmd ~ $yesterdayYmd)${System.lineSeparator()}${getBodyContent(weekData)}"
        // 获取每天的平均数据(不含当天)
        val averageData = statistics(list).let {
            val count = list.size
            if (count != 0) {
                it.runTime /= count
                it.activeTime /= count
                it.addLineCount /= count
                it.removeLineCount /= count
                it.keyCount /= count
                it.copyCount /= count
                it.pasteCount /= count
                it.pushCount /= count
            }
            it
        }
        val average = "🐲 Average of Day ${System.lineSeparator()}${getBodyContent(averageData)}"
        e.project?.let {
            Utils.consoleInfo(
                it, "$today${System.lineSeparator().repeat(2)}" +
                        "$yesterday${System.lineSeparator().repeat(2)}$week${System.lineSeparator().repeat(2)}$average"
            )
            Utils.info("报告生成成功 @ 时间管理大师")
        }
    }


    private fun statistics(list: List<StatisticsData>, date: String? = null): StatisticsData {
        val data = StatisticsData()
        for (statisticsData in list) {
            if (date != null && LocalDate.parse(statisticsData.createDate).isBefore(LocalDate.parse(date))) {
                break
            }
            data.runTime += statisticsData.runTime
            data.activeTime += statisticsData.activeTime
            data.addLineCount += statisticsData.addLineCount
            data.removeLineCount += statisticsData.removeLineCount
            data.keyCount += statisticsData.keyCount
            data.copyCount += statisticsData.copyCount
            data.pasteCount += statisticsData.pasteCount
            data.pushCount += statisticsData.pushCount
        }
        return data
    }

    private fun getBodyContent(data: StatisticsData) = """
        ${"-".repeat(64)}
        编辑器使用时间: ${Utils.formatTime(data.runTime)}
        编辑器活跃时间: ${Utils.formatTime(data.activeTime)}
        添加的代码行数: ${data.addLineCount}
        删除的代码行数: ${data.removeLineCount}
        总的键入数: ${data.keyCount}
        打开的项目数量: ${data.startCount}
        CTRL C 次数: ${data.copyCount}
        CTRL V 次数: ${data.pasteCount}
        代码提交次数: ${data.pushCount}
    """.trimIndent()
}