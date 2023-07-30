package com.equationl.common.platform

// TODO

actual fun showFloatWindows() {

/*    val context = ActivityUtils.getTopActivity()

    if (context == null) {
        Log.e("EL", "showFloatWindows: get Context is null!")
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (Settings.canDrawOverlays(context)) {
            context.startService(Intent(context, OverlayService::class.java))

            // 返回主页
            Intent(Intent.ACTION_MAIN).apply{
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }.let { context.startActivity(it) }
        }
        else {
            Toast.makeText(context, "请授予“显示在其他应用上层”权限后重试", Toast.LENGTH_LONG).show()
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
    }
    else {
        Toast.makeText(context, "当前系统不支持！", Toast.LENGTH_LONG).show()
    }*/
}

actual fun changeKeyBoardType(changeTo: Int) {
/*    if (!isFromUser) return
    platform.vibrateOnClick()
    val activity = ActivityUtils.getTopActivity()
    activity?.requestedOrientation =
        if (changeTo == KeyboardTypeStandard)
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE*/
}