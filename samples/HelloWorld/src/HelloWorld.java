import org.rythmengine.Rythm;

import java.util.HashMap;
import java.util.Map;

/**
 * A Hello world sample
 */
public class HelloWorld {
    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("who", "rythm");
        System.out.println(Rythm.render("@args String @who\nhello @who!", params));
        System.out.println(Rythm.render("@args String @who\nhello @who!", "rythm"));
    }
}
