package com.zues.searchable_spinner.utils

import android.content.Context

/* ---  Created by akhtarz on 7/23/2019. ---*/
object SpinnerUtils {

    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}