package com.yuchang.timemaster.listener

import com.intellij.openapi.editor.event.*
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.yuchang.timemaster.state.TimerMasterState
import com.yuchang.timemaster.util.Utils

/**
 * @description 编辑器事件监听
 * @author yuchang.su
 * @version 1.0
 * @date 2024/5/7 16:33:50
 */
class EditorListener : EditorFactoryListener, BulkAwareDocumentListener, CaretListener {

    private val state = TimerMasterState.getInstance()

    private val fileSet = mutableSetOf<String>()

    override fun editorCreated(event: EditorFactoryEvent) {
        val file = FileDocumentManager.getInstance().getFile(event.editor.document) ?: return
        if (file.path in fileSet) {
            return
        }
        fileSet.add(file.path)
        // 监听编辑操作
        event.editor.document.addDocumentListener(this)
        // 监听光标移动事件
        event.editor.caretModel.addCaretListener(this)
    }

    override fun documentChangedNonBulk(event: DocumentEvent) {
        val data = Utils.initData()
        event.takeIf { (it.oldFragment.isNotEmpty() or it.newFragment.isNotEmpty()) or !it.isWholeTextReplaced }?.let {
            // 只对字符长度为 1 和非空空白符的情况进行统计
            if (it.newFragment.isNotEmpty() && (it.newFragment.length == 1 || it.newFragment.trim().isEmpty())) {
                ++data.keyCount
            }
            // 根据文档代码段变更信息判断是新增还是删除行
            if (it.oldFragment.contains('\n')) {
                data.removeLineCount += it.oldFragment.count { item -> item == '\n' }
            }
            if (it.newFragment.contains('\n')) {
                data.addLineCount += it.newFragment.count { item -> item == '\n' }
            }
        }
        state.statisticsData = Utils.stringify(data)
    }

    override fun caretPositionChanged(event: CaretEvent) {
        state.statisticsData = Utils.stringify(Utils.initData())
    }

}