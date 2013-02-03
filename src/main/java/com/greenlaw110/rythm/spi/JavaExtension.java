package com.greenlaw110.rythm.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JavaExtension could be used to annotate on a public static methods 
 * that works on a given object with or without parameters. For example,
 * 
 * <pre><code>@JavaExtensions public static String format(Number number, String formatStr)</code></pre>
 * 
 * <p>When <code>rythm.enableJavaExtensions</code> is set to true (by default 
 * it is true), template author can use JavaExtension methods in way that 
 * it looks like the object's methods:</p>
 * 
 * <pre><code>@args Number n\n...@n.format("###.##")</code></pre>
 * 
 * <p>The <code>@n.format(...)</code> in the above example will be 
 * translated into <code>MyClass.format(n, "###.##")</code> which
 * is very handy for template author to use.</p>
 * 
 * <p>If you want all the public static methods of a class be registered
 * as java extensions, you can annotate <code>@JavaExtension</code>
 * to the class instead of methods</p>
 * 
 * <p>You can register them to RythmEngine by {@link com.greenlaw110.rythm.RythmEngine#registerJavaExtension(Class)}
 * method</p>, once you have registered your Java extension methods, the template author can use them
 * in template. Be careful of the name conflict of your Java extension and Rythm's built-in 
 * Java extension. You can turn off rythm's built java extension by set "rythm.disableBuiltInJavaExtension"
 * to <code>true</code></p>
 * 
 * <p>JavaExtensions mechanism is also found in other template engine
 * solutions, but with different names. Freemarker call it
 * <a href="http://freemarker.sourceforge.net/docs/ref_builtins_number.html">built-ins</a>,
 * Velocity called it <a href="http://stackoverflow.com/questions/8820240/how-to-format-numbers-in-velocity-templates">
 * velocity tools</a>. But none of them are as easy to use as Rythm JavaExtensions</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface JavaExtension {
}
