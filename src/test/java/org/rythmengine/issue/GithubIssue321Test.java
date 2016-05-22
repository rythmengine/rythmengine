/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.rythmengine.RythmEngine;
import org.rythmengine.TestBase;

/**
 * Test for https://github.com/rythmengine/rythmengine/issues/321
 * @author wf
 *
 */
public class GithubIssue321Test extends TestBase {

  @Test
  public void testHomeTemplate() throws Exception {
    // http://rythmengine.org/doc/template_guide.md#invoke_template
    File tmpFile = File.createTempFile("Home", "Template");
    File templateDir=tmpFile.getParentFile();
    File template = new File(templateDir,"test.html");
    String test="@include(\"common.html\")\n" + 
        "@show(\"test\")";
    FileUtils.writeStringToFile(template, test);
    String common="@def show(String param) {\n" + 
        "common @param\n" + 
        "}";
    File commonTemplate = new File(templateDir,"common.html");
    FileUtils.writeStringToFile(commonTemplate,common);
    Map<String,Object> conf = new HashMap<String,Object>();
    conf.put("home.template", templateDir.getAbsolutePath());
    RythmEngine engine = new RythmEngine(conf);
    Map<String,Object> rootMap = new HashMap<String,Object>();
    String result = engine.render(template, rootMap);
    System.out.println(result);
  }

}
