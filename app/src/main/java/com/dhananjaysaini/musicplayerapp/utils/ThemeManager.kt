package com.dhananjaysaini.musicplayerapp.utils

import android.app.Activity
import android.content.Context
import android.view.View
import com.dhananjaysaini.musicplayerapp.R

object ThemeManager {

    fun applyTheme(rootView: View, context: Context) {
        val prefs = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
//        val themeId = prefs.getInt("selected_theme", 0)

//        val root = activity.window.decorView
//        applyTheme(root, activity)

//        val prefs = activity.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
        val themeId = prefs.getInt("selected_theme", 0)

        val isLightTheme = themeId in listOf(1, 7, 9, 10)


        val drawableRes = when (themeId) {
            0 -> R.drawable.theme0
            1 -> R.drawable.theme1
            2 -> R.drawable.theme2
            3 -> R.drawable.theme3
            4 -> R.drawable.theme4
            5 -> R.drawable.theme5
            6 -> R.drawable.theme6
            7 -> R.drawable.theme7
            8 -> R.drawable.theme8
            9 -> R.drawable.theme9
            10 -> R.drawable.theme10
            11 -> R.drawable.theme11

            else -> R.drawable.theme0
        }

        rootView.setBackgroundResource(drawableRes)
    }

    fun saveTheme(context: Context, themeId: Int) {
        context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
            .edit()
            .putInt("selected_theme", themeId)
            .apply()
    }

    fun applyThemeToActivity(activity: Activity) {
      //  applyTheme(activity.window.decorView, activity)
        val root = activity.findViewById<View>(android.R.id.content)
        applyTheme(root, activity)

//        val prefs = activity.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
//        val themeId = prefs.getInt("selected_theme", 1)
//
//        val isLightTheme = themeId in listOf(1, 7, 9, 10)
//
//        WindowInsetsControllerCompat(activity.window, root)
//            .isAppearanceLightStatusBars = isLightTheme

    }
}