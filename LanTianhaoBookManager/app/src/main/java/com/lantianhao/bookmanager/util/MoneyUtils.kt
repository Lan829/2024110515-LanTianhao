package com.lantianhao.bookmanager.util

import java.util.Locale

/** 金额格式化工具，统一使用人民币符号并保留两位小数。 */
object MoneyUtils {

    @JvmStatic
    fun format(amount: Double): String = String.format(Locale.CHINA, "¥%,.2f", amount)
}
