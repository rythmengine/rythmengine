/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package models;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 14/03/13
 * Time: 8:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Foo {
    public Bar bar() {
        return new Bar();
    }
    
    public Bar bar(String b) {
        return new Bar(b);
    }    
    
    public Bar bar(Bar b) {
        return new Bar(b.toString());
    }
}
