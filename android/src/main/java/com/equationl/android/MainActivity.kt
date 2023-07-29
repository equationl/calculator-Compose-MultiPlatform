package com.equationl.android

//import com.equationl.common.overlay.OverlayService
import APP
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO 没有增加旋转监听

        setContent {
            APP()
        }
    }

    override fun onResume() {
        super.onResume()

        // 每次打开主页都要把悬浮界面关闭
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // TOOD stopService(Intent(this, OverlayService::class.java))
        }
    }
}