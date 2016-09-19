/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.issue;

import org.junit.Test;
import org.rythmengine.TestBase;

/**
 * Test for https://github.com/rythmengine/rythmengine/issues/266
 *
 * @author wf
 */
public class GithubIssue266Test extends TestBase {

  @Test
  public void testEnums() throws Exception {
  	   t = "@def static {\n" + 
  	       "  public enum Language {\n" + 
  	       "     de, en,fr,es\n" + 
  	       "  };\n" + 
  	       "  \n" + 
  	       "  static class Foo {\n" + 
  	       "    \n" + 
  	       "    public Language lang;\n" + 
  	       "    Foo(Language lang) {\n" + 
  	       "      this.lang=lang;\n" + 
  	       "    }\n" + 
  	       "  }\n" + 
  	       "}\n" + 
  	       "@{\n" + 
  	       " Foo foo = new Foo(Language.de);\n" + 
  	       "}\n" + 
  	       "@foo.lang";
  	   // debug=true;
  	   if (debug) {
  	     System.out.println(t);
  	   }
  	   s = r(t).trim();
  	   eq("de");
  }

}
