package models;

import org.rythmengine.Rythm;
import org.rythmengine.RythmEngine;
import org.rythmengine.utils.S;

/**
 * Created by luog on 20/06/2014.
 */
public class SandboxModel {
    private String id = S.random();
    private RythmEngine engine;

    public SandboxModel(String id, RythmEngine engine) {
        this.id = id;
        this.engine = engine;
    }

    @Override
    public String toString() {
        return engine.substitute("Bar[@1]", id);
    }
}
