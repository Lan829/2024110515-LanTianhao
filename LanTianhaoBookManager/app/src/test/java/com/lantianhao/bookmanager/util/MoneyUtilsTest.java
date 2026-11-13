package com.lantianhao.bookmanager.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MoneyUtilsTest {

    @Test
    public void formatsWithTwoDecimalsAndCurrencySymbol() {
        assertEquals("¥22.00", MoneyUtils.format(22));
        assertEquals("¥39.50", MoneyUtils.format(39.5));
        assertEquals("¥0.00", MoneyUtils.format(0));
    }

    @Test
    public void formatsThousandsSeparator() {
        assertEquals("¥1,255.30", MoneyUtils.format(1255.3));
    }

    @Test
    public void roundsToTwoDecimals() {
        assertEquals("¥19.99", MoneyUtils.format(19.994));
    }
}
