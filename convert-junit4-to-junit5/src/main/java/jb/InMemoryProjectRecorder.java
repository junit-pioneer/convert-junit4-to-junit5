package jb;

import java.util.HashSet;
import java.util.Set;

public class InMemoryProjectRecorder implements ProjectRecorder {

    public final Set<ClassName> foundClassNames = new HashSet<>();
    public final Set<ClassName> referencedCategories = new HashSet<>();


    @Override
    public void containsClass(ClassName className) {
        foundClassNames.add(className);
    }

    @Override
    public void referencesCategory(ClassName categoryClassName) {
        referencedCategories.add(categoryClassName);
    }
}
