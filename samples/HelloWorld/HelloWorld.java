import com.greenlaw110.rythm.Rythm;

import java.util.HashMap;
import java.util.Map;

public class HelloWorld {

    public String who;

    public HelloWorld(String who) {
        this.who = who;
    }

    public static void main(String[] args) {
        // render a file and pass args by position
        System.out.println(Rythm.render("hello.txt", "rythm"));

        // render a string and pass args by position
        System.out.println(Rythm.render("hello @who!", "java"));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("who", "world");

        // render a file and pass args by name
        System.out.println(Rythm.render("hello.txt", params));

        // render a string and pass args by name
        System.out.println(Rythm.render("hello @who!", params));

        // toString
        System.out.println(Rythm.toString("hello @_.who!", new HelloWorld("world")));

        // benchmark rythm.render vs. string.format
        Rythm.render("now: @i", 0); // compile the template for the first time
        long ts = System.currentTimeMillis();
        for (int i = 0; i < 1000000; ++i) {
            Rythm.render("now: @i", i);
        }
        long rythm = System.currentTimeMillis() - ts;

        ts = System.currentTimeMillis();
        for (int i = 0; i < 1000000; ++i) {
            String.format("now: %s", i);
        }
        long string = System.currentTimeMillis() - ts;

        System.out.println(String.format("rythm: %s; string: %s", rythm, string));
    }

}