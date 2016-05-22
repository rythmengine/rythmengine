/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.advanced;

import org.rythmengine.TestBase;
import org.rythmengine.utils.JSONWrapper;
import org.junit.Test;

/**
 * Test passing JSON string as template parameter
 */
public class JSONParameterTest extends TestBase {

  public static class User {
    public String name;
    public int age;
  }

  @Test
  public void testSimple() {
    t = "@args String name;hello @name";
    s = r(t, JSONWrapper.wrap("{\"name\":\"world\"}"));
    eq("hello world");

    String s0 = "{\"name\":\"\\\"world\\\"\"}";
    s = r(t, JSONWrapper.wrap(s0));
    eq("hello \"world\"");
  }

  @Test
  public void testArray() {
    t = "@args List<org.rythmengine.advanced.JSONParameterTest.User> users\n<ul>@for(users){\n@_.name: @_.age\n}</ul>";
    String params = "{users: [{\"name\":\"\\\"Tom\\\"\", \"age\": 12}, {\"name\":\"Peter\", \"age\": 11}]}";
    s = r(t, JSONWrapper.wrap(params));
    eq("<ul>\n\"Tom\": 12\nPeter: 11\n</ul>");
  }

  @Test
  public void testArray2() {
    t = "@args List<org.rythmengine.advanced.JSONParameterTest.User> users\n<ul>@for(users){\n@_.name: @_.age\n}</ul>";
    System.out.println(t);
    String params = "[{\"name\":\"Tom\", \"age\": 12}, {\"name\":\"Peter\", \"age\": 11}]";
    s = r(t, JSONWrapper.wrap(params));
    eq("<ul>\nTom: 12\nPeter: 11\n</ul>");
  }

  @Test
  public void testObject() {
    t = "@args org.rythmengine.advanced.JSONParameterTest.User user\n@user.name: @user.age";
    String params = "{user: {\"name\":\"Tom\", \"age\": 12}}";
    s = r(t, JSONWrapper.wrap(params));
    eq("Tom: 12");
  }

  /**
   * main routine
   * 
   * @param args
   */
  public static void main(String[] args) {
    run(JSONParameterTest.class);
  }
}
