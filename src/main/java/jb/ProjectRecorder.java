package jb;

import jb.convert.ast.tools.ClassName;

public interface ProjectRecorder {
    void containsClass(ClassName className);

    void referencesCategory(ClassName categoryClassName);
}
