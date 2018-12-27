package jb.convert.ast;

import jb.ClassName;

class StaticImport {

    final ClassName className;
    final String method;

    StaticImport(ClassName className, String method) {
        this.className = className;
        this.method = method;
    }
}
