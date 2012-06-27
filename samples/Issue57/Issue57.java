import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.utils.IO;
import com.greenlaw110.rythm.utils.S;

import java.io.File;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/06/12
 * Time: 6:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class Issue57 {
    public static void main(String[] args) throws Exception {
        File d = new File(System.getProperty("java.io.tmpdir"));
        Properties p = new Properties();
        p.setProperty("rythm.root", d.getAbsolutePath());
        p.setProperty("rythm.mode", "dev");
        Rythm.init(p);

        File f1 = new File(d, "f1.html");
        IO.writeContent("@extends(f2)@set(title=\"foo\")", f1);
        File f2 = new File(d, "f2.html");
        IO.writeContent("@get(title)", f2);
        assertEquals(Rythm.render("f1.html"), "foo\n");

        IO.writeContent("@extends(f2)@set(title=\"bar\")", f1);
        assertEquals(Rythm.render("f1.html"), "foo\n");

        for (int i = 0; i < 5; ++i) {
            sleep(6);
            IO.writeContent("@extends(f2)@set(title=\"baz\")", f1);
            assertEquals(Rythm.render("f1.html"), "baz\n");
        }
    }

    private static void assertEquals(String s1, String s2) {
        if (!S.isEqual(s1, s2)) throw new RuntimeException(s1 + " doesn't match " + s2);
    }

    private static void sleep(int n) throws InterruptedException {
        for (int i = 0; i < n; ++i) {
            Thread.sleep(1000L);
            System.out.print(".");
        }
        System.out.println();
    }
}

