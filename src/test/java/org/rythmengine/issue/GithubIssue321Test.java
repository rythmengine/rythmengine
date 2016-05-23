/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.rythmengine.RythmEngine;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;
import org.rythmengine.extension.ICodeType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.rythmengine.conf.RythmConfigurationKey.HOME_TEMPLATE;

/**
 * Test for https://github.com/rythmengine/rythmengine/issues/321
 *
 * @author wf
 */
public class GithubIssue321Test extends TestBase {

    @Test
    public void testHomeTemplate() throws Exception {
        System.getProperties().remove(HOME_TEMPLATE.getKey());
        debug = true;
        // http://rythmengine.org/doc/template_guide.md#invoke_template
        // first create some arbitrary temporary file
        File tmpFile = File.createTempFile("Home", "Template");
        // now get the parent directory of it and create a subdirectory for it
        // - this shall be our root directory for templates
        File templateDir = new File(tmpFile.getParentFile(), "templates");
        if (!templateDir.isDirectory())
            templateDir.mkdir();
        if (debug)
            System.out.println(templateDir.getAbsolutePath());
        // try extensions e.g. macro - this will make this test fail
        String extensions[] = RythmEngine.VALID_SUFFIXES;
        for (String extension : extensions) {
            if (debug) {
                System.out.println("Trying extension: " + extension);
            }
            File template = new File(templateDir, "test" + extension);
            String test = "@include(\"common" + extension + "\")\n"
                    + "@show(\"test <>\")";
            FileUtils.writeStringToFile(template, test);
            if (debug)
                System.out.println(template.getAbsolutePath());
            String common = "@def show(String param) {\n" + "common @param\n" + "}";
            File commonTemplate = new File(templateDir, "common" + extension);
            FileUtils.writeStringToFile(commonTemplate, common);
            if (debug)
                System.out.println(commonTemplate.getAbsolutePath());
            String keys[] = {RythmConfigurationKey.HOME_TEMPLATE.getKey(),
                    // Aliases (which don't work ...)
                    "home.template", "rythm.home.template.dir", "rythm.home.template"
            };
            // "home.template.dir" works
            for (String key : keys) {
                Map<String, Object> conf = new HashMap<String, Object>();
                conf.put(key, templateDir.getAbsolutePath());
                conf.put(RythmConfigurationKey.FEATURE_SMART_ESCAPE_ENABLED.getKey(), false);
                conf.put(RythmConfigurationKey.BUILT_IN_CODE_TYPE_ENABLED.getKey(), false);
                conf.put(RythmConfigurationKey.BUILT_IN_TRANSFORMER_ENABLED.getKey(), false);
                conf.put(RythmConfigurationKey.FEATURE_TRANSFORM_ENABLED.getKey(), false);
                if (debug) {
                    System.out.println("Trying home key: " + key);
                }
                RythmEngine engine = new RythmEngine(conf).prepare(ICodeType.DefImpl.RAW);
                Map<String, Object> rootMap = new HashMap<String, Object>();
                String result = engine.render(template, rootMap);
                if (debug)
                    System.out.println(result);
                assertTrue(result.contains("common test <>"));
            }
        }
    }

}
