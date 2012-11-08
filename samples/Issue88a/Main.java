import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.IImplicitRenderArgProvider;

public class Main {

    public static void main(String[] args) {

        final Properties props = new Properties();
        props.put("rythm.root", "p:\\rythm\\sample\\Issue88a");
        //props.put("rythm.mode", "dev");
        Rythm.init(props);
        
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Hello World");
        
        System.out.println(Rythm.render("test.html", params));
    }

}
