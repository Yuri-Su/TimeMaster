package com.yuchang.timemaster.data

import com.yuchang.timemaster.util.Utils

/**
 * @description 统计数据存储
 * @author yuchang.su
 * @version 1.0
 * @date 2024/5/7 16:33:50
 */
data class StatisticsData(
    var runTime: Long = 0,
    var activeTime: Long = 0,
    var keyCount: Long = 0,
    var addLineCount: Long = 0,
    var removeLineCount: Long = 0,
    var copyCount: Long = 0,
    var pasteCount: Long = 0,
    var pushCount: Long = 0,
    var startCount: Long = 0,
    var createDate: String = Utils.getTodayYmd()
)