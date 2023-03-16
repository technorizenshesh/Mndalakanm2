package com.app.mndalakanm.utils

import com.app.mndalakanm.model.*

interface ChildClickListener {
    fun onClick(position: Int, model: ChildData,type:String)
}
interface DayClickListener {
    fun onClick(position: Int, model: WeekDays)
}
interface SechduleClickListener {
    fun onClick(position: Int, model: SuccesSchedulCategory.Result)
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
interface ChildRequestListClickListener {
    fun onClick(position: Int, model: SuccessChildrReqTime.Result,status:String)
}
interface ChildRequestListClickListener2 {
    fun onClick(position: Int, model: SuccessChildrRewardReqTime.Result,status:String)
}
interface ParentRewardListClickListener {
    fun onClick(position: Int, model: SucessRewardList.Result, status:String , type:String )
}
interface ParentScheduleListClickListener {
    fun onClick(position: Int, model: SuccessScheduleTime.Result, status:String , type:String )
}