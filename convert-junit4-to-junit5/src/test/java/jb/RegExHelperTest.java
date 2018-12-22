package jb;

import jb.convert.regex.RegExHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegExHelperTest {

	@Test
	void replaceFollowedBy() {
		String actual = RegExHelper.replaceUnlessFollowedBy("replace after", "replace", " after", "new");
		assertEquals("replace after", actual);
	}

	@Test
	void replaceNotFollowedBy() {
		String actual = RegExHelper.replaceUnlessFollowedBy("replace after", "replace", " other", "new");
		assertEquals("new after", actual);
	}

	// -------------------------------------------------------------

	@Test
	void replaceDotsWithBackslash() {
		String actual = RegExHelper.replaceUnlessFollowedByEscapingPackageName("a.b.c", "a.b", "xxx", "new");
		assertEquals("new.c", actual);
	}

	// -------------------------------------------------------------

	@Test
	void replacePreceededBy() {
		String actual = RegExHelper.replaceUnlessPreceededBy("before replace", "replace", "before ", "new");
		assertEquals("before replace", actual);
	}

	@Test
	void replaceNotPreceededBy() {
		String actual = RegExHelper.replaceUnlessPreceededBy("before replace", "replace", "other ", "new");
		assertEquals("before new", actual);
	}

}
