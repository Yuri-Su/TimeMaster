package com.yuchang.timemaster.config

import com.intellij.openapi.options.Configurable
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.panel
import com.yuchang.timemaster.state.TimerMasterState

/**
 * @description 插件配置
 * @author yuchang.su
 * @version 1.0
 * @date 2024/5/7 16:33:50
 */
class TimerMasterConfig : Configurable {

    private val model = Model()

    private val state = TimerMasterState.getInstance()

    private var panel = panel {
        row("更新间隔(秒): ") {
            intTextField()
                .bindIntText(model::updateInterval)
                .comment("<icon src='AllIcons.General.Information'>&nbsp;不设置或者小于 10, 最终都为 10.")
        }

        row("活跃间隔(秒): ") {
            intTextField()
                .bindIntText(model::activeInterval)
                .comment("<icon src='AllIcons.General.Information'>&nbsp;不设置或者小于 30, 最终都为 30.")
        }
    }

    init {
        model.updateInterval = state.updateInterval
        model.activeInterval = state.activeInterval
    }

    override fun createComponent() = panel

    override fun isModified() = panel.isModified()

    override fun apply() {
        panel.apply()
        state.updateInterval = model.updateInterval.coerceAtLeast(10)
        state.activeInterval = model.activeInterval.coerceAtLeast(30)
    }

    override fun reset() {
        panel.reset()
    }

    override fun getDisplayName() = "TimerMaster"

    data class Model(
        var updateInterval: Int = 10,
        var activeInterval: Int = 30
    )

}