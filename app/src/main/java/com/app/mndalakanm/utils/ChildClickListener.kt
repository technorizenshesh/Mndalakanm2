package com.app.mndalakanm.utils

import com.app.mndalakanm.model.*

interface ChildClickListener {
    fun onClick(position: Int, model: ChildData)
}

interface SubClickListener {
    fun onClick(position: Int, model: SuccessSubsRes.SubsRes)
}

interface RedeemCodeClickListener {
    fun onClick(position: Int, model: SuccessRedeemCodeRes.Result)
}

interface ScreenShotClickListener {
    fun onClick(position: Int, model: SuccessScreenshotRes.ScreenshotList)
}

interface TimerListClickListener {
    fun onClick(position: Int, model: SuccessTimerListRes.TimerList)
}