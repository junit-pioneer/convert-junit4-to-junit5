package jb.convert.ast.tools;

public class StaticImport {

    public static final String SymbolForStarImport = "*";
    public final ClassName className;
    public final String method;

    public StaticImport(ClassName className, String method) {
        this.className = className;
        this.method = method;
    }

    public boolean isStarImport() {
        return SymbolForStarImport.equals(this.method);
    }
}
