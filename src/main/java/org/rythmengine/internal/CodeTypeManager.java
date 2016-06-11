/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal;

import org.rythmengine.extension.ICodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage {@link org.rythmengine.extension.ICodeType template language} implementations
 */
public class CodeTypeManager {
    private List<ICodeType> _codeTypeList = new ArrayList<ICodeType>();

    public CodeTypeManager registerCodeType(ICodeType type) {
        _codeTypeList.add(type);
        return this;
    }

    public Iterable<ICodeType> codeTypes() {
        return _codeTypeList;
    }

    public boolean hasCodeType() {
        return !_codeTypeList.isEmpty();
    }
}
