package jb.convert.ast.tools;

import jb.ClassName;

public class StaticImport {

    public final ClassName className;
    public final String method;

    public StaticImport(ClassName className, String method) {
        this.className = className;
        this.method = method;
    }
}
