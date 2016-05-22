/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.essential;

import org.rythmengine.TestBase;
import org.rythmengine.utils.S;
import org.junit.Test;

import java.text.NumberFormat;
import java.util.*;

import static org.rythmengine.utils.Eval.eval;

/**
 * Test utilities
 */
public class UtilsTest extends TestBase {
    boolean debug=false;
    @Test
    public void testTS() {
        long now = System.currentTimeMillis();
        t = "@ts()";
        s = r(t);
        long l = Long.valueOf(s);
        assertTrue(l > now);
        if (debug)
          System.out.println(l - now);
        assertTrue((l - now) < 4000);
    }
    
    private void f(Boolean b) {
        assertFalse(b);
    }
    
    private void t(Boolean b) {
        assertTrue(b);
    }
    
    Object o(Object o) {
        return o;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testEval() {
        String s = "";
        f(eval(s));
        f(eval(o(s)));
        
        s = " ";
        f(eval(s));
        f(eval(o(s)));
        
        s = "foo";
        t(eval(s));
        t(eval(o(s)));
        
        s = "false";
        f(eval(s));
        f(eval(o(s)));
        
        s = "no";
        f(eval(s));
        f(eval(o(s)));
        
        int i = 0;
        f(eval(i));
        f(eval(o(i)));
        
        i = 1;
        t(eval(i));
        t(eval(o(i)));
        
        double d = 0.00000000001D;
        f(eval(d));
        f(eval(o(d)));
        
        d = 0.121D;
        t(eval(d));
        t(eval(o(d)));
        
        Map m = Collections.emptyMap();
        f(eval(m));
        f(eval(o(m)));
        
        m = new HashMap();
        m.put("x", null);
        t(eval(m));
        t(eval(o(m)));

        Collection c = Collections.emptyList();
        f(eval(c));
        f(eval(o(c)));
        
        c = Arrays.asList(new String[]{"x"});
        t(eval(c));
        t(eval(o(c)));
        
        String[] sa = null;
        f(eval(sa));
        f(eval(o(sa)));
        
        sa = new String[]{};
        f(eval(sa));
        f(eval(o(sa)));
        
        sa = new String[]{"x"};
        t(eval(sa));
        t(eval(o(sa)));
    }
    
    @Test
    public void testEscape() {
        String s = null;
        System.out.println(S.escapeCSV(s));
    }
    
    @Test
    public void testLogTime() {
        t = "@__log_time__";
        s = r(t);
        // TODO how to test this?
    }
    
    @Test
    public void testCapitalizeWords() {
        s = S.capitalizeWords("[abc 123 xyz]");
        eq("[Abc 123 Xyz]");
        
        s = S.capitalizeWords("abc@xyz.com");
        eq("Abc@Xyz.Com");
    }

    @Test
    public void testFormatCurrency() {
        int n = 1000;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String expected=currencyFormatter.format(n);
        // "$1,000.00" is the expected result for the US locale
        // debug=true;
        if (debug)
          System.out.println(expected);
        eqs(expected, S.formatCurrency(n));
        eqs("$1,000.00", S.formatCurrency(null, n, "AUD", new Locale("en")));
    }
}
