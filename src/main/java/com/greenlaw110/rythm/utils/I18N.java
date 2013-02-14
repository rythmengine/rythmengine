/* 
 * Copyright (C) 2013 The Rythm Engine project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.RythmEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * I18N utils
 */
// Most of the code come from Play!Framework I18N.java, under Apache License 2.0
public class I18N {

    static final Map<String, String> symbols = new HashMap<String, String>();

    static {
        symbols.put("ALL", "Lek");
        symbols.put("USD", "$");
        symbols.put("AFN", "؋");
        symbols.put("ARS", "$");
        symbols.put("AWG", "ƒ");
        symbols.put("AUD", "$");
        symbols.put("AZN", "ман");
        symbols.put("BSD", "$");
        symbols.put("BBD", "$");
        symbols.put("BYR", "p.");
        symbols.put("EUR", "€");
        symbols.put("BZD", "BZ$");
        symbols.put("BMD", "$");
        symbols.put("BOB", "$b");
        symbols.put("BAM", "KM");
        symbols.put("BWP", "P");
        symbols.put("BGN", "лв");
        symbols.put("BRL", "R$");
        symbols.put("GBP", "£");
        symbols.put("BND", "$");
        symbols.put("KHR", "៛");
        symbols.put("CAD", "$");
        symbols.put("KYD", "$");
        symbols.put("CLP", "$");
        symbols.put("CNY", "¥");
        symbols.put("COP", "$");
        symbols.put("CRC", "₡");
        symbols.put("HRK", "kn");
        symbols.put("CUP", "₱");
        symbols.put("EUR", "€");
        symbols.put("CZK", "Kč");
        symbols.put("DKK", "kr");
        symbols.put("DOP", "RD$");
        symbols.put("XCD", "$");
        symbols.put("EGP", "£");
        symbols.put("SVC", "$");
        symbols.put("GBP", "£");
        symbols.put("EEK", "kr");
        symbols.put("EUR", "€");
        symbols.put("FKP", "£");
        symbols.put("FJD", "$");
        symbols.put("EUR", "€");
        symbols.put("GHC", "¢");
        symbols.put("GIP", "£");
        symbols.put("EUR", "€");
        symbols.put("GTQ", "Q");
        symbols.put("GGP", "£");
        symbols.put("GYD", "$");
        symbols.put("EUR", "€");
        symbols.put("HNL", "L");
        symbols.put("HKD", "$");
        symbols.put("HUF", "Ft");
        symbols.put("ISK", "kr");
        symbols.put("INR", "");
        symbols.put("IDR", "Rp");
        symbols.put("IRR", "﷼");
        symbols.put("EUR", "€");
        symbols.put("IMP", "£");
        symbols.put("ILS", "₪");
        symbols.put("EUR", "€");
        symbols.put("JMD", "J$");
        symbols.put("JPY", "¥");
        symbols.put("JEP", "£");
        symbols.put("KZT", "лв");
        symbols.put("KPW", "₩");
        symbols.put("KRW", "₩");
        symbols.put("KGS", "лв");
        symbols.put("LAK", "₭");
        symbols.put("LVL", "Ls");
        symbols.put("LBP", "£");
        symbols.put("LRD", "$");
        symbols.put("CHF", "CHF");
        symbols.put("LTL", "Lt");
        symbols.put("EUR", "€");
        symbols.put("MKD", "ден");
        symbols.put("MYR", "RM");
        symbols.put("EUR", "€");
        symbols.put("MUR", "₨");
        symbols.put("MXN", "$");
        symbols.put("MNT", "₮");
        symbols.put("MZN", "MT");
        symbols.put("NAD", "$");
        symbols.put("NPR", "₨");
        symbols.put("ANG", "ƒ");
        symbols.put("EUR", "€");
        symbols.put("NZD", "$");
        symbols.put("NIO", "C$");
        symbols.put("NGN", "₦");
        symbols.put("KPW", "₩");
        symbols.put("NOK", "kr");
        symbols.put("OMR", "﷼");
        symbols.put("PKR", "₨");
        symbols.put("PAB", "B/.");
        symbols.put("PYG", "Gs");
        symbols.put("PEN", "S/.");
        symbols.put("PHP", "Php");
        symbols.put("PLN", "zł");
        symbols.put("QAR", "﷼");
        symbols.put("RON", "lei");
        symbols.put("RUB", "руб.");
        symbols.put("SHP", "£");
        symbols.put("SAR", "﷼");
        symbols.put("RSD", "Дин.");
        symbols.put("SCR", "₨");
        symbols.put("SGD", "$");
        symbols.put("EUR", "€");
        symbols.put("SBD", "$");
        symbols.put("SOS", "S");
        symbols.put("ZAR", "R");
        symbols.put("KRW", "₩");
        symbols.put("EUR", "€");
        symbols.put("LKR", "₨");
        symbols.put("SEK", "kr");
        symbols.put("CHF", "CHF");
        symbols.put("SRD", "$");
        symbols.put("SYP", "£");
        symbols.put("TWD", "NT$");
        symbols.put("THB", "฿");
        symbols.put("TTD", "TT$");
        symbols.put("TRY", "TL");
        symbols.put("TRL", "₤");
        symbols.put("TVD", "$");
        symbols.put("UAH", "₴");
        symbols.put("GBP", "£");
        symbols.put("USD", "$");
        symbols.put("UYU", "$U");
        symbols.put("UZS", "лв");
        symbols.put("EUR", "€");
        symbols.put("VEF", "Bs");
        symbols.put("VND", "₫");
        symbols.put("YER", "﷼");
        symbols.put("ZWD", "Z$");
    }

    static final Map<String, String> dateFormats = new HashMap<String, String>();

    static {
        dateFormats.put("us", "MM/dd/yyyy");
        dateFormats.put("au", "dd/MM/yyyy");
    }

    /**
     * Retrieve currency symbol for a currency
     *
     * @param currency (JPY,USD,EUR,GBP,...)
     * @return ($, €, ...)
     */
    public static String getCurrencySymbol(String currency) {
        if (symbols.containsKey(currency)) {
            return symbols.get(currency);
        }
        return currency;
    }

    public static String getDateFormat() {
        RythmEngine engine = RythmEngine.get();
        String locale = engine.conf().locale();
        final String dateFormat = dateFormats.get(locale);
        if (null != dateFormat) {
            return dateFormat;
        } else {
            return "yyyy-MM-dd";
        }
    }

}
