/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.extension;

import org.rythmengine.template.ITag;
import org.rythmengine.template.ITemplate;
import org.rythmengine.template.TemplateBase;

/**
 * Listen to rythm event
 */
public interface IRythmListener {

    void onRender(ITemplate template);
    void rendered(ITemplate template);
    
    void enterInvokeTemplate(TemplateBase caller);
    void exitInvokeTemplate(TemplateBase caller);

    void onInvoke(ITag tag);
    void invoked(ITag tag);
    
    public static class ListenerAdaptor implements IRythmListener{
        @Override
        public void onRender(ITemplate template) {
        }

        @Override
        public void rendered(ITemplate template) {
        }

        @Override
        public void onInvoke(ITag tag) {
        }

        @Override
        public void invoked(ITag tag) {
        }

        @Override
        public void enterInvokeTemplate(TemplateBase caller) {
        }

        @Override
        public void exitInvokeTemplate(TemplateBase caller) {
        }
    }
}
