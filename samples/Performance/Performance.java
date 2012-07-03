import com.greenlaw110.rythm.Rythm;

import java.util.HashMap;
import java.util.Map;

public class Performance {

    public static void main(String[] args) {
        // benchmark rythm.render vs. string.format
        Rythm.render("performance.txt", "hello"); // compile the template for the first time
        long ts = System.currentTimeMillis();
        for (int i = 0; i < 50000; ++i) {
            Rythm.render("performance.txt", "hello");
        }
        Long spent = System.currentTimeMillis() - ts;
        System.out.println("1st 50000: " + spent + "ms");
        
        ts = System.currentTimeMillis();
        for (int i = 0; i < 50000; ++i) {
            Rythm.render("performance.txt", "hello");
        }
        spent = System.currentTimeMillis() - ts;
        System.out.println("2nd 50000: " + spent + "ms");

        ts = System.currentTimeMillis();
        for (int i = 0; i < 50000; ++i) {
            Rythm.render("performance.txt", "hello");
        }
        spent = System.currentTimeMillis() - ts;
        System.out.println("3rd 50000: " + spent + "ms");
    }

}