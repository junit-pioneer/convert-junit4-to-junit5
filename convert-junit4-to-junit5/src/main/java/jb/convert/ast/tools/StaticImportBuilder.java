package jb.convert.ast.tools;

import jb.ClassName;

public class StaticImportBuilder {

    public static StaticImportBuilder staticImportFrom(Class<?> clazz) {
        return staticImportFrom(clazz.getCanonicalName());
    }

    public static StaticImportBuilder staticImportFrom(String className) {
        return new StaticImportBuilder().className(className);
    }

    private String className;
    private String method;

    public StaticImportBuilder className(String className) {
        this.className = className;
        return this;
    }

    public StaticImportBuilder star() {
        return method("*");
    }

    public StaticImportBuilder method(String method){
        this.method = method;
        return this;
    }

    public StaticImport build(){
        return new StaticImport(ClassName.createClassName(className), method);
    }
}
