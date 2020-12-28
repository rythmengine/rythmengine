package org.rythmengine;

class _VM {

    public static final String INFO = System.getProperty("java.vm.name");
    public static final Boolean IS_SERVER = INFO.toUpperCase().contains("SERVER");
    public static final Boolean IS_64 = INFO.contains("64");
    public static final String SPEC_VERSION = System.getProperty("java.specification.version");

    public static final int VERSION; static {
        int _pos = SPEC_VERSION.lastIndexOf('.');
        VERSION = Integer.parseInt(SPEC_VERSION.substring(_pos + 1));
    }

    private _VM() {}

}
