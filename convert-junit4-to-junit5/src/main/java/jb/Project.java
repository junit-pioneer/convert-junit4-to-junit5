package jb;

import jb.convert.ast.tools.ClassName;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class Project {

    private final Map<ClassName, Path> classToPath = new HashMap<>();
    private final Set<ClassName> categories = new HashSet<>();

    void trackClasses(Collection<ClassName> classNames, Path sourceFile) {
        classNames.forEach( className -> classToPath.put(className, sourceFile));
    }

    void trackCategories(Set<ClassName> categoriesClassName) {
        categories.addAll(categoriesClassName);
    }

    Set<Path> categoriesToConvert() {
        return categories.stream().map(classToPath::get).collect(Collectors.toSet());
    }
}
