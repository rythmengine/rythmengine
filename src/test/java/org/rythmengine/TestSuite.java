/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * TestSuite to be run interactively e.g. in Eclipse Integrated development
 * environment
 * 
 * @author wf
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ org.rythmengine.advanced.JSONParameterTest.class,
    org.rythmengine.advanced.NaturalTemplateTest.class,
    org.rythmengine.advanced.SmartEscapeTest.class,
    org.rythmengine.advanced.TransformerTest.class,
    org.rythmengine.advanced.TypeInferenceTest.class,
    org.rythmengine.cache.EhCacheServiceTest.class,
    org.rythmengine.cache.SimpleCacheServiceTest.class,
    org.rythmengine.essential.ArgsParserTest.class,
    org.rythmengine.essential.AssignParserTest.class,
    org.rythmengine.essential.BraceParserTest.class,
    org.rythmengine.essential.CacheParserTest.class,
    org.rythmengine.essential.CommentTest.class,
    org.rythmengine.essential.CompactModeTest.class,
    org.rythmengine.essential.CompactParserTest.class,
    org.rythmengine.essential.DebugParserTest.class,
    org.rythmengine.essential.EscapeParserTest.class,
    org.rythmengine.essential.ExpressionTest.class,
    org.rythmengine.essential.FinallyParserTest.class,
    org.rythmengine.essential.ForParserTest.class,
    org.rythmengine.essential.I18nTest.class,
    org.rythmengine.essential.IfParserTest.class,
    org.rythmengine.essential.ImportParserTest.class,
    org.rythmengine.essential.IncludeParserTest.class,
    org.rythmengine.essential.NoCompactParserTest.class, 
    org.rythmengine.essential.RawParserTest.class,
    org.rythmengine.essential.ReturnParserTest.class,
    org.rythmengine.essential.UtilsTest.class,
    org.rythmengine.essential.VerbatimParserTest.class,
    org.rythmengine.issue.GhIssueTest70_140.class,
    org.rythmengine.issue.GhIssueTest141_176.class,
    org.rythmengine.issue.GhIssueTest185_202.class,
    org.rythmengine.issue.GhIssueTest211_249.class,
    org.rythmengine.issue.GhIssue248Test.class,
    org.rythmengine.issue.GithubIssue321Test.class, 
    org.rythmengine.issue.GithubIssue325Test.class, 
    org.rythmengine.layout.LayoutTest.class,
    org.rythmengine.render_mode.sandbox.SandboxTest.class,
    org.rythmengine.render_mode.substitute.SubstituteTest.class,
    org.rythmengine.render_mode.to_string.ToStringTest.class,
    org.rythmengine.tag.InlineTagTest.class,
    org.rythmengine.tag.InvokeParserTest.class,
    org.rythmengine.tag.InvokeTemplateTest.class,
    org.rythmengine.tag.MacroTest.class,
    org.rythmengine.tag.tagPriorityTest.class, 
    org.rythmengine.essential.ForParserTest.class})
public class TestSuite {

}
