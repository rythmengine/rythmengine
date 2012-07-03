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
public class Issue60 {
    public static void main(String[] args) throws Exception {
        String[] names = {"aa","bb","cc"};
        String result=Rythm.render("@args String[] names;@names.length", names);
        System.out.println(result);
    }
}

