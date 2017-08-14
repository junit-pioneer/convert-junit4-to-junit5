package jb;

import java.io.*;
import java.nio.file.*;

public class Updater {

	public void update(Path path) throws IOException {
		String originalText = new String(Files.readAllBytes(path));
		String updatedText = originalText.replace("import static org.junit.Assert.*;",
				"import static org.junit.jupiter.api.Assertions.*;");
		updatedText = updatedText.replace("import org.junit.*", "import org.junit.jupiter.api.*;");
		if (!originalText.equals(updatedText)) {
			Files.write(path, updatedText.getBytes());
		}

	}

}
