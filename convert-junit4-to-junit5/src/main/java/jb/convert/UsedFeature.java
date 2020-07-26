package jb.convert;

import com.github.javaparser.Position;

import java.util.function.Predicate;

public class UsedFeature {

    public static Predicate<UsedFeature> usedFeatureMatching(String featureName, String detail) {
        return usedFeature -> detail.equals(usedFeature.details) && featureName.equals(usedFeature.name);
    }

    public final boolean convertible;
    public final String name;
    public final String details;

    public final Position position;

    public UsedFeature(boolean convertible, String name, String details, Position position) {
        this.convertible = convertible;
        this.name = name;
        this.details = details;
        this.position = position;
    }
}
