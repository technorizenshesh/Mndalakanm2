package com.app.mndalakanm.utils

import com.app.mndalakanm.model.ChildData
import com.app.mndalakanm.model.SuccessChildsListRes
import com.app.mndalakanm.model.SuccessScreenshotRes
import com.app.mndalakanm.model.SuccessSubsRes

interface ChildClickListener {
    fun onClick(position:Int,model: ChildData)
}
interface SubClickListener {
    fun onClick(position:Int,model: SuccessSubsRes.SubsRes)
}
interface ScreenShotClickListener {
    fun onClick(position:Int,model: SuccessScreenshotRes.ScreenshotList)
}