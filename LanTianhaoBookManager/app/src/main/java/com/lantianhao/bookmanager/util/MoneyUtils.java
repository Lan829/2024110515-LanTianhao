package com.lantianhao.bookmanager.util;

import java.util.Locale;

/** 金额格式化工具，统一使用人民币符号并保留两位小数。 */
public final class MoneyUtils {

    private MoneyUtils() {
    }

    public static String format(double amount) {
        return String.format(Locale.CHINA, "¥%,.2f", amount);
    }
}
