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
public class Issue88 {
    public static void main (String [] args) {
        try {
            Properties props = new Properties();
            props.put("rythm.tmpDir", "c:\\temp");
            props.put("rythm.mode", "dev");
            props.put("rythm.implicitRenderArgProvider", new IImplicitRenderArgProvider(){

                @Override
                public Map<String, ?> getRenderArgDescriptions() {
                    final Map<String, String> descriptions = new HashMap<String, String> ();
                    descriptions.put("implicitMessage", "java.lang.String");
                    return descriptions;
                }

                @Override
                public void setRenderArgs(ITemplate template) {
                    final Map<String, Object> implicitArgs = new HashMap<String, Object>();
                    implicitArgs.put("implicitMessage", "I am implicit message");
                    template.setRenderArgs(implicitArgs);
                }

                @Override
                public List<String> getImplicitImportStatements() {
                    return new ArrayList<String>();
                }

            });
            Rythm.init(props);
            String s = Rythm.render("issue88.txt", "Hello World");
            System.out.println("Render result from template file:");
            System.out.println(s);
            s = Rythm.render("@args String message, String implicitMessage \n [@message] [@implicitMessage]", "Hello World");
            System.out.println("Render result from inline String:");
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Rythm.engine.cacheService.shutdown();
        }
    }
}
