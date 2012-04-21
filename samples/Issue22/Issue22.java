import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.utils.IO;

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
            File root = new File("tmp/issue22");
            root.mkdirs();
            File index = new File(root, "index.html");
            IO.writeContent("@args String s\n@common(s)", index);
            File common = new File(root, "common.html");
            IO.writeContent("@args String s\nhello @s", common);
            properties.put("rythm.root", root);
            Rythm.init(properties);
            String s = "rythm";
            System.out.println (Rythm.render("index.html", s));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Rythm.engine.cacheService.shutdown();
        }
    }
}
