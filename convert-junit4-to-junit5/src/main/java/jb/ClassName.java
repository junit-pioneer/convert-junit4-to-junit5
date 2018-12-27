package jb;

import java.util.Objects;

public class ClassName {
    public static ClassName createClassName(String className) {
        return new ClassName(className);
    }

    public final String string;

    public ClassName(String className) {
        this.string = className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassName className = (ClassName) o;
        return string.equals(className.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
    }

    @Override
    public String toString() {
        return string;
    }
}
