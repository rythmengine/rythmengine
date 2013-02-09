package com.greenlaw110.rythm.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <code>Transformer</code> could be used to annotate on a public static methods
 * that works on a given object with or without parameters. For example,
 * <p/>
 * <pre><code>@Transformer public static String format(Number number, String formatStr)</code></pre>
 * <p/>
 * <p>When <code>feature.transformer.enabled</code> is set to true (by default
 * it is true), template author can use Transformer to further process an
 * expression value:</p>
 * <p/>
 * <pre><code>@args Date dueDate\n...@dueDate.format("dd/MM/yyyy")</code></pre>
 * <p/>
 * <p>The <code>@dueDate.format("dd/MM/yyyy")</code> in the above example will be
 * transformed into <code>S.format(dueDate, "dd/MM/yyyy")</code> in the generated
 * java source</p>
 * <p/>
 * <p>Note, the above sample code demonstrates a transformer named <code>format</code>, which is
 * built into Rythm engine. However when you want to define your own transformer, you
 * need to use this <code>Transformer</code> annotation to mark on your methods or classes.
 * When the annotation is marked on a class, then all public static methods with return
 * value and at least one parameter will be treated as transformer</p>
 * <p/>
 * <p>You can register them to RythmEngine by {@link com.greenlaw110.rythm.RythmEngine#registerTransformer(Class)}
 * method</p>, once you have registered your Java extension methods, the template author can use them
 * in template. Be careful of the name conflict of your Java extension and Rythm's built-in
 * Java extension. You can turn off rythm's built java extension by set "rythm.disableBuiltInTransformer"
 * to <code>true</code></p>
 * <p/>
 * <p>Transformers mechanism is also found in other template engine
 * solutions, but with different names. Freemarker call it
 * <a href="http://freemarker.sourceforge.net/docs/ref_builtins_number.html">built-ins</a>,
 * Velocity called it <a href="http://stackoverflow.com/questions/8820240/how-to-format-numbers-in-velocity-templates">
 * velocity tools</a>. But none of them are as easy to use as Rythm Transformers</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Transformer {
}
