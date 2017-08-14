package jb;

import java.io.*;
import java.nio.file.*;

/**
 * Converts a file or nested directories to use JUnit 5 syntax where possible.
 * The resulting code may not compile, but it will be closer than the original
 * code so less to convert manually.
 * 
 * @author jeanne
 *
 */
public class Updater {

	/**
	 * Update to use JUnit 5 syntax where possible
	 * 
	 * @param path
	 *            a file or directory to update recursively
	 * @throws IOException
	 */
	public void update(Path path) throws IOException {
		if (path.toFile().isFile()) {
			updateSingleFile(path);
		} else {
			Files.walk(path)
					// only update java files
					.filter(p -> p.toFile().isFile())
					.filter(p -> p.toString().endsWith(".java"))
					.forEach(this::updateSingleFile);
		}
	}

	private void updateSingleFile(Path path) {
		try {
			String originalText = new String(Files.readAllBytes(path));
			String updatedText = originalText.replace("import static org.junit.Assert.*;",
					"import static org.junit.jupiter.api.Assertions.*;");
			updatedText = updatedText.replace("import org.junit.*", "import org.junit.jupiter.api.*;");
			if (!originalText.equals(updatedText)) {
				Files.write(path, updatedText.getBytes());
			}
		} catch (IOException e) {
			// convert to runtime exception so can use inside stream operation
			throw new RuntimeException(e);
		}
	}

}
