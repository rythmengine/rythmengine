/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import org.junit.Ignore;
import org.rythmengine.TestBase;

/**
 * Test for https://github.com/rythmengine/rythmengine/issues/334
 *
 * @author wf
 */
public class GithubIssue334Test extends TestBase {

  @Ignore
  public void testArgsWithBlanks() throws Exception {
  	   t = "@args() {\n" + 
  	   		"  String name, \n" + 
  	   		"  String firstname;\n" + 
  	   		"}\n" + 
  	   		"Hello @(firstname) @(name)!";
     s = r(t,"Luo","Green");
     eq("Hello Green Luo!");
  }

}
