package jb;

import java.io.*;
import java.nio.file.*;

public class Updater {

	public void update(Path path) throws IOException {
		String text = new String(Files.readAllBytes(path));
		text = text.replace("import static org.junit.Assert.*;", "import static org.junit.jupiter.api.Assertions.*;");
		text = text.replace("import org.junit.*", "import org.junit.jupiter.api.*;");
		Files.write(path, text.getBytes());
		
	}

}
