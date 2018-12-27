package jb.convert.ast;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jb.ClassName;
import jb.ProjectRecorder;
import jb.convert.ast.tools.PackageDeclarations;

public class ProjectProbe extends VoidVisitorAdapter<Void> {

    private final ProjectRecorder projectRecorder;

    public ProjectProbe(ProjectRecorder projectRecorder) {
        this.projectRecorder = projectRecorder;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        String packageName = PackageDeclarations.packageNameFor(n);
        ClassName className = new ClassName(packageName + "." + n.getName().toString());
        projectRecorder.containsClass(className);
    }
}
