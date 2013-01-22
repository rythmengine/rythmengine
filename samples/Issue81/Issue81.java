import com.greenlaw110.rythm.*;
import com.greenlaw110.rythm.template.*;
import com.greenlaw110.rythm.utils.*;
import java.util.*;
import java.io.File;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/04/12
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Issue81 {
    public static void main (String [] args) {
        try {
            Properties props = new Properties();
            props.put("rythm.tmpDir", "c:\\temp");
            props.put("rythm.mode", "dev");
            Rythm.init(props);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("name", "foo");
            m.put("en", false);
            String s = Rythm.render("foo.txt", m);
            if (!"Hola, foo!\n".equals(s)) {
                System.out.println("expected: Hola, foo!\n");
                System.out.println("found: " + s);
            }
            m.put("en", true);
            s = Rythm.render("foo.txt", m);
            if (!"Hello, foo!\n".equals(s)) {
                System.out.println("expected: Hello, foo!\n");
                System.out.println("found: " + s);
            } else {
                System.out.println("Success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Rythm.engine.cacheService.shutdown();
        }
    }
}
