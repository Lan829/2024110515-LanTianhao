package cn.edu.sicnu.cs.stu.lantianhao.first

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

/**
 * 多语言交互式 HelloWorld。
 *
 * 特点：
 *  1. 界面「纯代码」构建 —— 不使用任何布局 XML（没有 activity_main.xml），
 *     所有控件（ImageView / TextView / Button）都在 Kotlin 里 new 出来并 addView 组装。
 *  2. 支持中文 / English / Português 三种语言的 strings 资源（values-zh 默认、values-en、values-pt），
 *     点击按钮即可切换语言，同时显示对应国家的国旗。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var flagView: ImageView
    private lateinit var greetingView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 用代码创建并设置界面根布局，完全不引用布局文件
        setContentView(buildContentView())
        // 默认显示中文
        switchLanguage("zh", R.drawable.flag_cn)
    }

    /** 完全用 Kotlin 代码搭建界面 */
    private fun buildContentView(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
            setPadding(dp(24), dp(24), dp(24), dp(24))
        }

        // ① 国旗
        flagView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        root.addView(
            flagView,
            LinearLayout.LayoutParams(dp(240), dp(150)).apply { bottomMargin = dp(28) }
        )

        // ② 问候语（随语言切换）
        greetingView = TextView(this).apply {
            textSize = 30f
            setTextColor(Color.parseColor("#1A1A1A"))
            gravity = Gravity.CENTER
        }
        root.addView(greetingView)

        // ③ 提示文字
        val hint = TextView(this).apply {
            text = getString(R.string.tap_hint)
            textSize = 14f
            setTextColor(Color.parseColor("#9E9E9E"))
            gravity = Gravity.CENTER
        }
        root.addView(
            hint,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
                bottomMargin = dp(36)
            }
        )

        // ④ 三个语言按钮（点击交互）
        root.addView(makeButton(R.string.btn_chinese) { switchLanguage("zh", R.drawable.flag_cn) })
        root.addView(makeButton(R.string.btn_english) { switchLanguage("en", R.drawable.flag_us) })
        root.addView(makeButton(R.string.btn_portuguese) { switchLanguage("pt", R.drawable.flag_br) })

        return root
    }

    private fun makeButton(textRes: Int, onClick: () -> Unit): Button =
        Button(this).apply {
            text = getString(textRes)
            isAllCaps = false
            textSize = 18f
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(12) }
        }

    /** 切换语言：更新国旗图片 + 从对应语言的资源里取问候语 */
    private fun switchLanguage(localeTag: String, flagRes: Int) {
        flagView.setImageResource(flagRes)
        val greeting = localized(localeTag, R.string.greeting)
        greetingView.text = greeting
        Toast.makeText(this, greeting, Toast.LENGTH_SHORT).show()
    }

    /** 按语言标签读取字符串资源（真实使用 values-en / values-pt 等多语言资源） */
    private fun localized(tag: String, resId: Int): String {
        val config = Configuration(resources.configuration)
        config.setLocale(Locale.forLanguageTag(tag))
        return createConfigurationContext(config).getString(resId)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
