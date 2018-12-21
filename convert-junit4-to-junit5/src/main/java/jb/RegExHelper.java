package jb;

import java.util.regex.Pattern;

public class RegExHelper {

	private RegExHelper() {
		super();
	}

	public static String replaceUnlessFollowedByEscapingPackageName(String originalText,
			String targetFullyQualifiedName, String exceptIfFollowing, String replacement) {
		String escapedDotsInPackageName = targetFullyQualifiedName.replace(".", "\\.");
		return replaceUnlessFollowedBy(originalText, escapedDotsInPackageName, exceptIfFollowing,
				replacement);
	}

	public static String replaceUnlessFollowedBy(String originalText,
			String targetFullyQualifiedName, String exceptIfFollowing, String replacement) {
		String notFollowedBy = "(?!" + exceptIfFollowing + ")";
		String regex = targetFullyQualifiedName + notFollowedBy;
		return originalText.replaceAll(regex, replacement);
	}

	public static String replaceUnlessPreceededBy(String originalText,
			String targetFullyQualifiedName, String exceptIfAfter, String replacement) {
		String notAfter = "(?<!" + exceptIfAfter + ")";
		String regex = notAfter + targetFullyQualifiedName;
		return originalText.replaceAll(regex, replacement);
	}

	static String replaceAllLiterals(String result, String regex, String replacement) {
        return result.replaceAll(Pattern.quote(regex), replacement);
    }
}
