package jb.convert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchDetector {

    public Set<String> publicStaticMethodsWithMatchingNames(Class<?> junit4Class, Class<?> junit5Class) {
        Set<String> junit4Methods = publicStaticMethodsOf(junit4Class);
        Set<String> junit5Methods = publicStaticMethodsOf(junit5Class);
        junit4Methods.retainAll(junit5Methods);
        return junit4Methods;
    }

    private Set<String> publicStaticMethodsOf(Class<?> type) {
        Method[] declaredMethods = type.getDeclaredMethods();
        return Arrays.stream(declaredMethods).filter(this::isPublicStatic).map(Method::getName).collect(Collectors.toSet());
    }

    private boolean isPublicStatic(Method method) {
        int modifiers = method.getModifiers();
        boolean isStatic = Modifier.isStatic(modifiers);
        boolean isPublic = Modifier.isPublic(modifiers);
        return isStatic && isPublic;
    }
}
