import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.runtime.ITag;
import com.greenlaw110.rythm.template.JavaTagBase;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 29/04/12
 * Time: 6:42 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JavaTags {

    public static class Hello extends JavaTagBase {
        @Override
        public String getName() {
            return "hello";
        }

        @Override
        protected void call(ParameterList params, Body body) {
            Object o = params.getDefault();
            String who = null == o ? "who" : o.toString();
            p("Hello ").p(who);
        }
    }

    public static class Bye extends JavaTagBase {
        @Override
        public String getName() {
            return "bye";
        }

        @Override
        protected void call(ParameterList params, Body body) {
            Object o = params.getDefault();
            String who = null == o ? "who" : o.toString();
            p("bye ").p(who);
            if (null != body) body.render(out(), who);
        }
    }

    public static void main(String[] args) {
        Rythm.registerTag(new Hello());
        Rythm.registerTag(new Bye());
        String s = Rythm.render("javaTagDemo.txt");
        System.out.println(s);
    }
}
