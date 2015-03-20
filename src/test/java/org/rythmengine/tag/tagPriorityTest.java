package org.rythmengine.tag;

import org.junit.Test;
import org.rythmengine.TestBase;

/**
 * tag mechanisms has the following priorities:
 * inline tag > macro > template invocation
 */
public class tagPriorityTest extends TestBase {
    @Test
    public void inlineTagShallHasHigherPriorityThanMacro() {
        t = "@def foo(){inline_tag}@macro(foo){macro}@foo()";
        s = r(t);
        eq("inline_tag");
    }

    @Test
    public void inlineTagWithArgsShallNotCountIn() {
        t = "@def foo(String s){inline_tag}@macro(foo){macro}@foo()";
        s = r(t);
        eq("macro");
    }

    @Test
    public void explicitlyExecMacroShallBeDoable() {
        t = "@def foo(){inline_tag}@macro(foo){macro}@exec(foo)";
        s = r(t);
        eq("macro");
    }

    @Test
    public void macroShallHasHigherPriorityThanTemplateInvocation() {
        t = "@tagPriority()";
        s = r(t);
        eq("template_invocation");
        t = "@macro(tagPriority){macro}@tagPriority()";
        s = r(t);
        eq("macro");
    }

    @Test
    public void explicitlyInvokeTemplateShallBeDoable() {
        t = "@macro(tagPriority){macro}@invoke(\"tagPriority\")";
        s = r(t);
        eq("template_invocation");
    }
}
