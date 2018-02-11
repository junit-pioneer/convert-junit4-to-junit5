This is a project I wrote for myself to [facilitate migrating a good number of tests to JUnit 5 syntax](https://www.selikoff.net/?p=7915&preview=true). Feel free to use it/enhance it/etc if you find it useful.

Note that IntelliJ has functionality to migrate from JUnit 4 to 5. I recommend trying that before this program. (This program was written in September 2016 when no IDEs had the functionality).

# Pre-reqs
Before starting out, the program assumes you have:
* Updated your Maven POM/Groovy build file/whatever to reference the new JUnit 5 jars
* Migrated off JUnit 3.8. This program assumes you are on JUnit 4. JUnit 3.8 code will continue to work in JUnit 5. (as will JUnit 4 code.) But the goal of this program is to migrate JUnit 4 syntax to JUnit 5 syntax.

# Running the program
Pass the absolute path of the directory containing your tests (ex /my/dir/src/test/java) when running class jb.Updater. The program outputs the name of each file as it goes through them. It runs quickly. Under a few seconds for two thousand files.
Then make manual edits at end (or rollback those test classes and deal with them later). In particular the following will not compile after running the program and require manually migration.
1. Parameterized tests
1. Tests that use expected parameter
1. Tests that use timeout parameter


# Side effects
This program updates every class and reformats your code. This is a side effect of using [Java Parser](https://github.com/javaparser/javaparser). If you have a standard coding format, you can always reapply it after running. Regardless, make sure to commit your code before running the program so this commit only has the refactoring to JUnit 5.

# Other notes
* The program can be run multiple times. That way you can rollback the classes that require manual intervention and commit the others. Then later when you re-run, it will only update the remaining classes.
