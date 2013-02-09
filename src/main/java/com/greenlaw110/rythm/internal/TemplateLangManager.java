package com.greenlaw110.rythm.internal;

import com.greenlaw110.rythm.extension.ILang;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage {@link com.greenlaw110.rythm.extension.ILang template language} implementations
 */
public class TemplateLangManager {
    private List<ILang> _templateLangList = new ArrayList<ILang>();

    public TemplateLangManager registerTemplateLang(ILang lang) {
        _templateLangList.add(lang);
        return this;
    }

    public Iterable<ILang> templateLangs() {
        return _templateLangList;
    }

    public boolean hasTemplateLangs() {
        return !_templateLangList.isEmpty();
    }
}
