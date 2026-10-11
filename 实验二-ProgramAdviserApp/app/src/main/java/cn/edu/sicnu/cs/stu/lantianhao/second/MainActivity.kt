package cn.edu.sicnu.cs.stu.lantianhao.second

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.edu.sicnu.cs.stu.lantianhao.second.databinding.ActivityMainBinding
import cn.edu.sicnu.cs.stu.lantianhao.second.model.AdviserModel

/**
 * 实验二：基础操作实验（两个功能区）
 *
 * 区 1：通过交互（输入 + 按钮），用代码动态生成 TextView，
 *       并放入 ScrollView 内部嵌套的 LinearLayout 中滚动显示。
 * 区 2：交互式 ProgramAdviser，按 MVC 模式实现：
 *       View       = activity_main.xml + MainActivity 里的界面操作
 *       Controller = MainActivity 的按钮事件（askAdviser）
 *       Model      = AdviserModel（只负责数据与查询逻辑）
 *
 * 全部界面文字都来自 res/values/strings.xml，布局与代码中没有硬编码字符串；
 * 控件通过 ViewBinding 访问，不写 findViewById。
 */
class MainActivity : AppCompatActivity() {

    /** ViewBinding 对象：编译期根据 activity_main.xml 自动生成 */
    private lateinit var binding: ActivityMainBinding

    /** MVC 中的 Model 层 */
    private lateinit var adviser: AdviserModel

    /** 区 1 已经动态添加了多少条 */
    private var addedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adviser = createAdviserModel()
        setUpZone1()
        setUpZone2()
    }

    // ========================================================
    // 区 1：用代码动态添加 TextView + ScrollView 嵌套 LinearLayout
    // ========================================================

    private fun setUpZone1() {
        binding.btnAddText.setOnClickListener { addTextViewByCode() }
        binding.btnClearZone1.setOnClickListener { clearZone1() }
        refreshZone1Count()
    }

    /** 交互核心：点击"添加"后，用代码创建 TextView 并加入容器 */
    private fun addTextViewByCode() {
        val content = binding.etZone1Input.text.toString().trim()
        if (content.isEmpty()) {
            showToast(getString(R.string.toast_input_empty))
            return
        }

        addedCount += 1

        // ① 代码 new 出一个 TextView（控件完全由代码生成，没有写在布局文件里）
        val itemView = TextView(this).apply {
            text = getString(R.string.zone1_item_format, addedCount, content)
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@MainActivity, R.color.primary_text))
            setPadding(dp(12), dp(10), dp(12), dp(10))
            setBackgroundResource(R.drawable.bg_item)
        }

        // ② 加入 ScrollView 内部的 LinearLayout，超出高度即可滚动查看
        binding.containerZone1.addView(
            itemView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8) }
        )

        binding.etZone1Input.text.clear()
        refreshZone1Count()
        scrollZone1ToBottom()
    }

    private fun clearZone1() {
        if (addedCount == 0) {
            showToast(getString(R.string.toast_nothing_to_clear))
            return
        }
        binding.containerZone1.removeAllViews()
        addedCount = 0
        refreshZone1Count()
        showToast(getString(R.string.toast_cleared))
    }

    private fun refreshZone1Count() {
        binding.tvZone1Count.text = if (addedCount == 0) {
            getString(R.string.zone1_count_empty)
        } else {
            getString(R.string.zone1_count_format, addedCount)
        }
    }

    /** 新内容加入后自动滚动到底部，保证最新添加的 TextView 可见 */
    private fun scrollZone1ToBottom() {
        binding.scrollZone1.post {
            binding.scrollZone1.fullScroll(View.FOCUS_DOWN)
        }
    }

    // ========================================================
    // 区 2：交互式 ProgramAdviser（MVC：View / Controller / Model）
    // ========================================================

    private fun setUpZone2() {
        binding.btnAsk.setOnClickListener { askAdviser() }
        buildShortcutButtons()
    }

    /**
     * Controller 的核心动作：接收按钮事件 → 调用 Model 查询 → 把结果交给 View 显示。
     * 这里用 when 把"命中 / 未输入 / 未收录"三种 Model 结果分别映射到三种界面反馈。
     */
    private fun askAdviser() {
        val input = binding.etZone2Input.text.toString()

        when (val result = adviser.advise(input)) {
            is AdviserModel.AdviserResult.Matched -> showZone2Text(
                getString(R.string.zone2_result_format, result.topic.keyword, result.topic.advice)
            )

            AdviserModel.AdviserResult.Empty -> showZone2Text(
                getString(R.string.zone2_result_empty_query)
            )

            is AdviserModel.AdviserResult.NotFound -> showZone2Text(
                getString(R.string.zone2_result_not_found, result.query)
            )
        }

        hideKeyboard()
    }

    private fun showZone2Text(text: String) {
        binding.tvZone2Result.text = text
        binding.scrollZone2.post {
            binding.scrollZone2.fullScroll(View.FOCUS_UP)
        }
    }

    /** 快捷主题按钮同样由代码动态生成，文字取自 Model 中的主题关键字 */
    private fun buildShortcutButtons() {
        adviser.keywords().forEach { keyword ->
            val shortcut = Button(this).apply {
                text = keyword
                textSize = 14f
                isAllCaps = false
                setOnClickListener {
                    binding.etZone2Input.setText(keyword)
                    askAdviser()
                }
            }
            binding.containerShortcuts.addView(
                shortcut,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = dp(8) }
            )
        }
    }

    /**
     * 从字符串资源里读出"主题关键字"和"对应的建议"来构造 Model：
     * 既保证代码中没有硬编码字符串，也让 Model 保持与 Context 无关。
     */
    private fun createAdviserModel(): AdviserModel {
        val keywords = resources.getStringArray(R.array.adviser_topic_keys)
        val advices = resources.getStringArray(R.array.adviser_topic_advices)
        val count = minOf(keywords.size, advices.size)

        val topics = (0 until count).map { index ->
            AdviserModel.Topic(keyword = keywords[index], advice = advices[index])
        }
        return AdviserModel(topics)
    }

    // ========================================================
    // 一些小工具方法
    // ========================================================

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}
