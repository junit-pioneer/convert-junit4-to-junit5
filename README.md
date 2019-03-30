This is a project @boyarsky wrote to [facilitate convert a good number of tests to JUnit 5 syntax](https://www.selikoff.net/?p=7915&preview=true). It was since donated to junit-pioneer.

Note that IntelliJ has functionality to convert from JUnit 4 to 5. Consider trying that before this program. (This program was originally written in September 2016 when no IDEs had the functionality).

# Pre-reqs
Before starting out, the program assumes you have:
* Updated your Maven POM/Groovy build file/whatever to reference the new JUnit 5 jars
* Conversion off JUnit 3.8. This program assumes you are on JUnit 4. JUnit 3.8 code will continue to work in JUnit 5. (as will JUnit 4 code.) But the goal of this program is to convert JUnit 4 syntax to JUnit 5 syntax.

# Running the program
Pass the absolute path of the directory containing your tests (ex /my/dir/src/test/java) when running class jb.Updater. The program outputs the name of each file as it goes through them. It runs quickly. Under a few seconds for two thousand files.
Then make manual edits at end (or rollback those test classes and deal with them later). In particular the following will not compile after running the program and require manual conversion.
1. Parameterized tests
1. Runners
1. Rules
1. Categories (there is only basic support for categories)

This program is also available as a shaded jar. To run:
java -jar convert-junit4-to-to-junit5-0.0.1.jar /my/dir/src/test/java

# Side effects
This program updates every class and reformats your code.
This is a side effect of using [Java Parser](https://github.com/javaparser/javaparser).
If you have a standard coding format, you can always reapply it after running. Regardless, make sure to commit your code before running the program so this commit only has the refactoring to JUnit 5.

# Running the update from code
There are [configuration options] (convert-junit4-to-junit5/src/main/java/jb/configuration/Configuration.java) like `preserve formatting` and `dry run` that are not exposed as command line flags.
Have a look at [ConvertProgrammatically](convert-junit4-to-junit5/src/main/java/jb/UpdateWithAdditionalOptions.java) to see how to enable those options from the code. 

# Other notes
* The program can be run multiple times. That way you can rollback the classes that require manual intervention and commit the others. Then later when you re-run, it will only update the remaining classes.
