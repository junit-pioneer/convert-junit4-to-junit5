package jb;

import jb.configuration.Configuration;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CommandLineRunner {

	private static class ExcludesConverter implements IStringConverter<Predicate<Path>> {
		@Override
		public Predicate<Path> convert(String value) {
			return path -> FileSystems.getDefault().getPathMatcher("glob:" + value).matches(path);
		}
	}

	@Parameter(description = "directory...")
	List<String> parameters = new ArrayList<>();

	@Parameter(names = { "-h", "--help" }, help = true, description = "Show this help message")
	private boolean help = false;

	@Parameter(names = { "-w", "--write" }, description = "Overwrite files, instead of just reporting what would happen")
	private boolean write = false;

	@Parameter(names = { "-s", "--skip-unsupported-features" }, description = "Skip files with unsupported JUnit4 features")
	private boolean skipFilesWithUnsupportedFeatures = false;

	@Parameter(names = { "-p", "--preserve-formatting" }, description = "Do no re-format source files")
	private boolean preserveFormatting = false;

	@Parameter(names = { "-x", "--exclude" }, description = "Glob pattern of file names to exclude",
		converter = ExcludesConverter.class)
	private Predicate<Path> exclude = path -> false;


	private void run() throws IOException {
		Configuration.ConfigurationBuilder configuration = new Configuration.ConfigurationBuilder()
			.excludeMatching(exclude);

		if (!write) {
			configuration.dryRun();
		}
		if (skipFilesWithUnsupportedFeatures) {
			configuration.skipFilesWithUnsupportedFeatures();
		}
		if (preserveFormatting) {
			configuration.preserveFormatting();
		}

		for (String arg : parameters) {
			try {
				new Updater(configuration.build()).update(Paths.get(arg));
			} catch (NoSuchFileException nsfe) {
				System.err.printf("Error: Directory '%s' not found", nsfe.getMessage());
				System.exit(1);
			}
		}
	}

	public static void main(String[] argv) throws IOException {
		CommandLineRunner main = new CommandLineRunner();
		JCommander jCommander = JCommander.newBuilder()
			.addObject(main)
			.build();

		jCommander.parse(argv);
		if (main.help || main.parameters.isEmpty()) {
			jCommander.usage();
			return;
		}
		main.run();
	}
}