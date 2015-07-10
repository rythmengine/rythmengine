import org.rythmengine.*;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

/**
 * A Hello world sample
 */
public class HelloWorld {
    public static void main(String[] args) {
        Map<String, Object> conf = new HashMap();
        conf.put("rythm.home.tmp.dir", new File("./temp"));
        RythmEngine engine = new RythmEngine(conf);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("who", "rythm");
        System.out.println(engine.render("@args String who\nhello @who!", params));
        System.out.println(engine.render("@args String who\nhello @who!", "rythm"));
    }
}
