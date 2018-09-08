package jb;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

/**
 * Converts a file or nested directories to use JUnit 5 syntax where possible.
 * The resulting code may not compile, but it will be closer than the original
 * code so less to convert manually.
 * 
 * @author jeanne
 *
 */
public class Updater {

	public static void main(String... args) throws IOException {
		if (args.length == 0) {
			throw new IllegalArgumentException(
					"Please pass the absolute path of the file or directory you want to update.");
		}

		Path path = Paths.get(args[0]);
		if (! path.toFile().exists()) {
			throw new IllegalArgumentException(
					"Please point to a valid file or directory.");
		}
		Updater updater = new Updater();
		updater.update(path);
	}

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
			try (Stream<Path> stream = Files.walk(path)) {
					// only update java files
			  stream.filter(p -> p.toFile().isFile())
					.filter(p -> p.toString().endsWith(".java"))
					.forEach(this::updateSingleFile);
			}		
		}
	}

	private void updateSingleFile(Path path) {
		try {
			String originalText = new String(Files.readAllBytes(path));
			String updatedText = JunitConversionLogic.convert(originalText);
			if (!originalText.equals(updatedText)) {
				System.out.println("Updating " + path.toAbsolutePath());
				Files.write(path, updatedText.getBytes());
			} else {
				System.out.println("No updates in " + path.toAbsolutePath());
			}
		} catch (IOException e) {
			// convert to runtime exception so can use inside stream operation
			throw new RuntimeException(e);
		}
	}

}
