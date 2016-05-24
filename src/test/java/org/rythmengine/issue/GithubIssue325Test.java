/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.rythmengine.RythmEngine;
import org.rythmengine.TestBase;
import org.rythmengine.conf.RythmConfigurationKey;

/**
 * Test for https://github.com/rythmengine/rythmengine/issues/325
 *
 * @author wf
 */
public class GithubIssue325Test extends TestBase {

  @Test
  public void testNonExistingTemplate() throws Exception {
    File templateDir = GithubIssue321Test.getTemplateDir();
    File template = new File(templateDir, "testNonExistingXYZ.rythm");
    assertFalse(template.exists());
    Map<String, Object> conf = new HashMap<String, Object>();
    conf.put(RythmConfigurationKey.HOME_TEMPLATE.getKey(),
        templateDir.getAbsolutePath());
    RythmEngine engine = new RythmEngine(conf);
    Map<String, Object> rootMap = new HashMap<String, Object>();
    try {
      String result = engine.render(template, rootMap);
      fail("There should be an exception");
      // just to avoid warning for result not being assigned to
      assertNull(result);
    } catch (Throwable th) {
      // debug=true;
      if (debug) {
        th.printStackTrace();
        System.out.println(th.getMessage());
      }
      assertFalse(th.getMessage().contains("Null"));
      assertTrue(th.getMessage().contains("exist"));
    }
  }

}
