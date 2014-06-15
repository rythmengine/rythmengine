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
package org.rythmengine.essential;

import org.junit.Test;
import org.rythmengine.Rythm;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.utils.S;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Test i18n
 */
public class I18nTest extends TestBase {

    @Test
    public void testBasic() {
//        t = "@i18n('foo.bar')";
//        s = r(t);
//        eq("foobar");

        t = "@i18n('foo.bar', Locale.CHINA)";
        s = r(t);
        eq("福吧");
    }

    @Test
    public void testLocaleBlock() {
        t = "@i18n('foo.bar') @locale(java.util.Locale.CHINA){@i18n('foo.bar')} @i18n('foo.bar')";
        s = r(t);
        eq("foobar 福吧 foobar");
    }

    @Test
    public void testCompound() {
        t = "@i18n('template', \"planet\", 7, new Date())";
        s = r(t);
        assertContains(s, "we detected 7 spaceships on the planet Mars.");
        assertContains(s, DateFormat.getDateInstance(DateFormat.LONG).format(new Date()));
    }

    @Test
    public void testConfiguration() {
        System.getProperties().put(RythmConfigurationKey.I18N_LOCALE.getKey(), Locale.CHINA);
        System.setProperty(RythmConfigurationKey.FEATURE_TYPE_INFERENCE_ENABLED.getKey(), "true");
        Rythm.shutdown();
        t = "@i18n(@1, @2, @3, @4)";
        s = r(t, "template", "planet", 7, new Date());
        assertContains(s, "在火星上发现了7艘宇宙飞船");
        assertContains(s, DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA).format(new Date()));
    }

    @Test
    public void testTransformer() {
        t = "@args String @1;@1.i18n()";
        s = r(t, "foo.bar");
        eq("foobar");

        Date date = new Date(1364122714992L);

        t = "@args Date today;@today.format()";
        s = r(t, date);
        //eq("24/03/2013");
        eq(S.format(date));

        t = "@args Date today;@today.format()\n@locale(\"zh\", \"CN\"){@today.format()}\n@today.format()";
        s = r(t, date);
        //eq("24/03/2013\n2013-3-24\n24/03/2013");
        eq(S.format(date) +
                "\n" +
                S.format(date, null, new Locale("zh", "CN")) +
                "\n" +
                S.format(date));

    }

    @Test
    public void testDateFormatInChineseLocale() {
        t = "@args Date date;@locale(\"zh\", \"CN\"){@date.format()}";
        Date date = new Date();
        s = r(t, date);
        eq(DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.CHINESE).format(date));
    }


    public static void main(String[] args) {
        run(I18nTest.class);
    }
}
