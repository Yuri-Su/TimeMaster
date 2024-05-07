package com.yuchang.timemaster.listener

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.yuchang.timemaster.state.TimerMasterState
import com.yuchang.timemaster.util.Utils

/**
 * @description 复制粘贴监听
 * @author yuchang.su
 * @version 1.0
 * @date 2024/5/7 16:33:50
 */
class CopyPasteListener : CopyPastePreProcessor {

    private val state = TimerMasterState.getInstance()

    override fun preprocessOnCopy(p0: PsiFile?, p1: IntArray?, p2: IntArray?, p3: String?): String? {
        val data = Utils.initData()
        ++data.copyCount
        state.statisticsData = Utils.stringify(data)
        return null
    }

    override fun preprocessOnPaste(p0: Project?, p1: PsiFile?, p2: Editor?, text: String?, p4: RawText?): String {
        val data = Utils.initData()
        ++data.pasteCount
        state.statisticsData = Utils.stringify(data)
        return text ?: ""
    }

}