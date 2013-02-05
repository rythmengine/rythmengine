package com.greenlaw110.rythm.exception;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.conf.RythmConfigurationKey;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.utils.F;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 14/02/12
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class RythmException extends FastRuntimeException {

    public int javaLineNumber = 0;
    public int templateLineNumber = -1;
    public String errorMessage = "";
    public String originalMessage = "";
    private TemplateClass templateClass = null;
    public String javaSource;
    public String templateSource;
    public String templateName;
    public String templateSourceInfo;
    public String javaSourceInfo;
    private RythmEngine engine;

    public RythmException(RythmEngine engine, Throwable t, String templateName, String javaSource, String templateSource, int javaLineNumber, int templateLineNumber, String message) {
        super(message, t);
        boolean isRuntime = !(this instanceof CompileException || this instanceof ParseException);
        boolean logJava = engine.conf.get(RythmConfigurationKey.LOG_SOURCE_JAVA_ENABLED);
        boolean logTmpl = engine.conf.get(RythmConfigurationKey.LOG_SOURCE_TEMPLATE_ENABLED);
        F.T4<String, Integer, String, String> t4 = parse(message, logJava || (this instanceof CompileException), logTmpl || (this instanceof ParseException), javaLineNumber, templateLineNumber, javaSource, templateSource, null);
        this.engine = engine;
        this.templateName = templateName;
        this.javaSource = javaSource;
        this.templateSource = templateSource;
        this.javaLineNumber = javaLineNumber;
        this.templateLineNumber = t4._2;
        this.originalMessage = message;
        this.errorMessage = t4._1;
        this.templateSourceInfo = t4._3;
        this.javaSourceInfo = t4._4;
    }

    public RythmException(RythmEngine engine, String templateName, String javaSource, String templateSource, int javaLineNumber, int templateLineNumber, String message) {
        this(engine, null, templateName, javaSource, templateSource, javaLineNumber, templateLineNumber, message);
    }

    public RythmException(RythmEngine engine, String templateName, String javaSource, String templateSource, int javaLineNumber, String message) {
        this(engine, null, templateName, javaSource, templateSource, javaLineNumber, -1, message);
    }

    public RythmException(RythmEngine engine, Throwable t, TemplateClass tc, int javaLineNumber, int templateLineNumber, String message) {
        super(message, t);
        boolean isRuntime = !(this instanceof CompileException || this instanceof ParseException);
        boolean logJava = engine.conf.get(RythmConfigurationKey.LOG_SOURCE_JAVA_ENABLED);
        boolean logTmpl = engine.conf.get(RythmConfigurationKey.LOG_SOURCE_TEMPLATE_ENABLED);
        F.T4<String, Integer, String, String> t4 = parse(message, logJava/* || (this instanceof CompileException)*/, logTmpl/*|| (this instanceof ParseException)*/, javaLineNumber, templateLineNumber, tc.javaSource, tc.getTemplateSource(), tc);
        this.engine = engine;
        this.javaLineNumber = javaLineNumber;
        this.templateClass = tc;
        this.javaSource = tc.javaSource;
        this.templateSource = tc.getTemplateSource();
        this.templateLineNumber = t4._2;
        this.originalMessage = message;
        this.errorMessage = t4._1;
        this.templateSourceInfo = t4._3;
        this.javaSourceInfo = t4._4;
    }

    public String javaSourceInfo() {
        if (null != javaSourceInfo) return javaSourceInfo;
        String javaSource = getJavaSource();
        if (S.isEmpty(javaSource)) return "No java source available";
        TextBuilder tb = new TextBuilder();
        tb.p("Relevant Java source lines:\n-------------------------------------------------\n");
        String[] lines = javaSource.split("(\\n\\r|\\r\\n|\\r|\\n)");
        int start = 0, end = lines.length;
        int javaLineNumber = this.javaLineNumber;
        if (javaLineNumber > -1 && javaLineNumber < lines.length) {
            start = Math.max(0, javaLineNumber - 6);
            end = Math.min(end, javaLineNumber + 6);
        }
        for (int line = start; line < end; ++line) {
            if ((line + 1) == javaLineNumber) tb.p(">> ");
            else tb.p("   ");
            tb.p(line + 1).p(": ").p(lines[line]).p("\n");
        }
        javaSourceInfo = tb.toString();
        return javaSourceInfo;
    }

    public String templateSourceInfo() {
        if (null != templateSourceInfo) return templateSourceInfo;
        String tmplSource = getTemplateSource();
        if (S.isEmpty(tmplSource)) return "No template source available";
        TextBuilder tb = new TextBuilder();
        tb.p("Relevant template source lines:\n-------------------------------------------------\n");
        String[] lines = tmplSource.split("(\\n\\r|\\r\\n|\\r|\\n)");
        int start = 0, end = lines.length ;
        if (templateLineNumber > -1 && templateLineNumber < lines.length) {
            start = Math.max(0, templateLineNumber - 6);
            end = Math.min(end, templateLineNumber + 6);
        }
        for (int line = start;line < end; ++line) {
            if ((line + 1) == templateLineNumber) tb.p(">> ");
            else tb.p("   ");
            tb.p(line+1).p(": ").p(lines[line]).p("\n");
        }
        templateSourceInfo = tb.toString();
        return templateSourceInfo;
    }

    public RythmException(RythmEngine engine, TemplateClass tc, int javaLineNumber, int templateLineNumber, String message) {
        this(engine, null, tc, javaLineNumber, templateLineNumber, message);
    }

    public RythmException(RythmEngine engine, TemplateClass tc, int javaLineNumber, String message) {
        this(engine, null, tc, javaLineNumber, -1, message);
    }

    private static final Pattern P = Pattern.compile(".*\\/\\/line:\\s*([0-9]+).*");
    private static int resolveTemplateLineNumber(int javaLineNumber, int templateLineNumber, String javaSource, TemplateClass templateClass) {
        if (javaLineNumber != -1 && templateLineNumber == -1) {
            String[] lines = getJavaSource(javaSource, templateClass).split("(\\r\\n|\\n\\r|\\n|\\r)");
            if (javaLineNumber < lines.length) {
                String errorLine = lines[javaLineNumber - 1];
                Matcher m = P.matcher(errorLine);
                if (m.matches()) {
                    return  Integer.parseInt(m.group(1));
                }
            }
        }
        return templateLineNumber;
    }

    private static F.T4<String, Integer, String, String> parse(String message, boolean logJava, boolean logTmpl, int javaLineNumber, int templateLineNumber, String javaSource, String templateSource, TemplateClass templateClass) {
        //Logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>> logJava: %s", logJava);
        javaSource = getJavaSource(javaSource, templateClass);
        String tmplSource = getTemplateSource(templateSource, templateClass);
        String tmplSourceInfo = null;
        String javaSourceInfo = null;
        templateLineNumber = resolveTemplateLineNumber(javaLineNumber, templateLineNumber, javaSource, templateClass);
        if (!logJava && !logTmpl) {
            return new F.T4(message, templateLineNumber, tmplSourceInfo, javaSourceInfo);
        }
        TextBuilder tb = new TextBuilder();
        tb.pn(message);
        tb.p("\nTemplate: ").p(templateClass.getKey()).p("\n");
        if (logTmpl && !S.isEmpty(tmplSource)) {
            TextBuilder tbTmpl = new TextBuilder();
            tbTmpl.p("\nRelevant template source lines:\n-------------------------------------------------\n");
            String[] lines = tmplSource.split("(\\n\\r|\\r\\n|\\r|\\n)");
            int start = 0, end = lines.length;
            if (templateLineNumber > -1) {
                start = Math.max(0, templateLineNumber - 6);
                end = Math.min(end, templateLineNumber + 6);
            }
            for (int line = start; line < end; ++line) {
                if ((line + 1) == templateLineNumber) tbTmpl.p(">> ");
                else tbTmpl.p("   ");
                tbTmpl.p(line + 1).p(": ").p(lines[line]).p("\n");
            }
            tmplSourceInfo = tbTmpl.toString();
        }
        // log java source anyway if template line number is not resolved
        if ((logJava || templateLineNumber < 0) && !S.isEmpty(javaSource)) {
            TextBuilder tbJava = new TextBuilder();
            tbJava.p("\nRelevant Java source lines:\n-------------------------------------------------\n");
            String[] lines = javaSource.split("(\\n\\r|\\r\\n|\\r|\\n)");
            int start = 0, end = lines.length;
            if (javaLineNumber > -1) {
                start = Math.max(0, javaLineNumber - 6);
                end = Math.min(end, javaLineNumber + 6);
            }
            for (int line = start; line < end; ++line) {
                if ((line + 1) == javaLineNumber) tbJava.p(">> ");
                else tbJava.p("   ");
                tbJava.p(line + 1).p(": ").p(lines[line]).p("\n");
            }
            javaSourceInfo = tbJava.toString();
        }
        tb.pn(tmplSourceInfo);
        tb.pn(javaSourceInfo);
        return new F.T4(tb.toString(), templateLineNumber, tmplSourceInfo, javaSourceInfo);
    }

    private static String getJavaSource(String javaSource, TemplateClass templateClass) {
        if (null != javaSource) return javaSource;
        return (null == templateClass.javaSource) ? "" : templateClass.javaSource;
    }

    public String getJavaSource() {
        if (null != javaSource) return javaSource;
        return (null == templateClass.javaSource) ? "" : templateClass.javaSource;
    }

    private static String getTemplateSource(String templateSource, TemplateClass templateClass) {
        if (null != templateSource) return templateSource;
        return templateClass.templateResource.asTemplateContent();
    }

    public String getTemplateSource() {
        if (null != templateSource) return templateSource;
        return templateClass.templateResource.asTemplateContent();
    }

    public String getTemplateName() {
        if (null != templateName) return templateName;
        return templateClass.getKey().toString();
    }

    public void clearDetailErrorMessage() {
        errorMessage = super.getMessage();
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    public String getSimpleMessage() {
        return super.getMessage();
    }
}
