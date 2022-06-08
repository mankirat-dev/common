package com.mankirat.common.lib.util

import cat.ereza.customactivityoncrash.config.CaocConfig

object ApplicationSetUp {

    fun initCustomCrash() {
        //log("initCustomCrash")
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
            .showErrorDetails(true)
            .trackActivities(true)
            .apply()
    }
}