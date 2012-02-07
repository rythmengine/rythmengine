package com.greenlaw110.rythm.utils;

import com.greenlaw110.rythm.template.ITemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 2/02/12
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IImplicitRenderArgProvider {
    Map<String, ?> getRenderArgDescriptions();
    void setRenderArgs(ITemplate template);
    List<String> getImplicitImportStatements();
}
