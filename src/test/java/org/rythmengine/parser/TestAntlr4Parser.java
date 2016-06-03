/* 
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.parser;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;
import org.rythmengine.rythmparser.Rythm;
import org.rythmengine.rythmparser.Rythm.RythmContext;
import org.rythmengine.rythmparser.RythmTokenLexer;

/**
 * first Test class for ANTLR4 based parser
 * @author wf
 *
 */
public class TestAntlr4Parser {
  boolean debug=false;
  /**
   * get the ANTLRInputStream for the given text
   * 
   * @param text
   * @return
   * @throws IOException
   */
  public static ANTLRInputStream streamForText(String text) throws IOException {
    InputStream stream = new ByteArrayInputStream(
        text.getBytes(StandardCharsets.UTF_8));
    ANTLRInputStream in = new ANTLRInputStream(stream);
    return in;
  }
  
  /**
   * get the tokens
   * @param lexer
   * @return
   */
  protected TokenStream getTokens(Lexer lexer) {
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    return tokens;
  }
  
  /**
   * Test based on:
   *  http://fiddle.rythmengine.org/#/editor/590e5d475704421db16e59b16bb1730a
   * @throws Exception 
   */
  @Test
  public void testHello() throws Exception {
    String inputText="@args String who\n" + 
        "Hello @who.capFirst()!\n";
    RythmTokenLexer lexer = new RythmTokenLexer(streamForText(inputText));
    Rythm parser = new Rythm(getTokens(lexer));
    RythmContext parsetree = parser.rythm();
    assertNotNull(parsetree);
    String parseTreeString=parsetree.toStringTree(Arrays.asList(parser.getRuleNames()));
    if (debug)
      System.out.println(parseTreeString);
    String expected="(rythm (args @args) (statement  String who\\nHello ) (statement (atexpression @ expression)) (statement who.capFirst()!\\n) <EOF>)";
    assertEquals(expected,parseTreeString);
  }

}
