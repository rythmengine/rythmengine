import com.greenlaw110.rythm.Rythm;

import java.io.File;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 20/04/12
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Issue22 {
    public static void main (String [] args) {
        try {
            Properties properties = new Properties();
            properties.put("rythm.mode", "dev");
            properties.put("rythm.root", new File("t:/tmp/issue22/"));
            Rythm.init(properties);
            String s = "s";
            System.out.println (Rythm.render("index.html", s));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
