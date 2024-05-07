package com.yuchang.timemaster.window

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.yuchang.timemaster.util.Utils

/**
 * @description 控制台输出
 * @author yuchang.su
 * @version 1.0
 * @date 2024/5/7 16:33:50
 */
class ConsoleWindow : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        if (Utils.getConsoleViews()[project] == null) {
            Utils.createToolWindow(project, toolWindow)
        }
        Utils.toolWindows[project] = toolWindow
    }

}