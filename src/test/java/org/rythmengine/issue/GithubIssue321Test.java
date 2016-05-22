/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import java.io.File;
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
    debug=true;
    // http://rythmengine.org/doc/template_guide.md#invoke_template
    // first create some arbitrary temporary file
    File tmpFile = File.createTempFile("Home", "Template");
    // now get the parent directory of it and create a subdirectory for it
    // - this shall be our root directory for templates
    File templateDir=new File(tmpFile.getParentFile(),"templates");
    if (!templateDir.isDirectory())
      templateDir.mkdir();
    if (debug)
      System.out.println(templateDir.getAbsolutePath());
    File template = new File(templateDir,"test.html");
    String test="@include(\"common.html\")\n" + 
        "@show(\"test\")";
    FileUtils.writeStringToFile(template, test);
    if (debug)
      System.out.println(template.getAbsolutePath());
    String common="@def show(String param) {\n" + 
        "common @param\n" + 
        "}";
    File commonTemplate = new File(templateDir,"common.html");
    FileUtils.writeStringToFile(commonTemplate,common);
    if (debug)
      System.out.println(commonTemplate.getAbsolutePath());
    Map<String,Object> conf = new HashMap<String,Object>();
    conf.put("home.template", templateDir.getAbsolutePath());
    RythmEngine engine = new RythmEngine(conf);
    Map<String,Object> rootMap = new HashMap<String,Object>();
    String result = engine.render(template, rootMap);
    if (debug)
      System.out.println(result);
  }

}
