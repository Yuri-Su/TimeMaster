package com.yuchang.timemaster.listener

import com.yuchang.timemaster.state.TimerMasterState
import com.yuchang.timemaster.util.Utils
import git4idea.push.GitPushRepoResult
import git4idea.repo.GitRepository
import git4idea.push.GitPushListener

/**
 * @description git 监听
 * @author yuchang.su
 * @version 1.0
 * @date 2024/5/7 16:33:50
 */
class GitListener: GitPushListener {
    
    private val state = TimerMasterState.getInstance()
    
    override fun onCompleted(repository: GitRepository, pushResult: GitPushRepoResult) {
        val data = Utils.initData()
        ++data.pushCount
        state.statisticsData = Utils.stringify(data)
    }
    
}