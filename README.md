# JUnit 4 to JUnit 5

As the name indicates this tool can assist to convert tests written in JUnit 5 to JUnit 5 (Jupiter, to be precise).
This programm was always driven by individual needs and is by no means a fully working and supported migration tool.   

# Some History

[Jeanne Boayrsky](https://github.com/boyarsky) originally wrote this program in September 2016 to [facilitate converting a good number of tests to JUnit 5 syntax](https://www.selikoff.net/2017/09/02/converting-2k-tests-to-junit-5-in-one-day/).
It was since donated to JUnit Pioneer.
It used a mixture of regex search and replace and AST transformations.
Nowadays, IntelliJ covers some aspects to convert from JUnit 4 to 5 but backe then this feature was not available.
Even with this feature available this tool can still be useful if you have to convert custom JUnit 4 logic to JUnit 5.

At the start of 2019 [signed](https://github.com/signed/) extended the programm to convert the tests in [javaparser](https://github.com/javaparser/javaparser/pull/2002).
The regex search and replace approach was no longer enough, and the migration logic changed to only use AST transformations.
This provides more control and safety compared to regex search and replace.
In addition to some new features the program also became more configurable.

# How to use it

## Side effects

As a side effect of using [Java Parser](https://github.com/javaparser/javaparser), this program updates every class and reformats your code.
If you have a standard coding format, you can always reapply it after running.
Regardless, make sure to commit your code before running the program so this commit only has the refactoring to JUnit 5.

## Pre-reqs

Before starting out, the program assumes you have:

* This program assumes you are on JUnit 4.
  JUnit 3.8 code will continue to work in JUnit 5 (as will JUnit 4 code, by the way).
  The goal of this program is to convert JUnit 4 syntax to JUnit 5 syntax.
* Java 8 (or later) and [Maven](https://maven.apache.org/download.cgi)
* Updated your Maven POM/Groovy build file/whatever to reference the new JUnit 5 JAR

## Running the update from code

1. clone this repository
2. open [`UpdateWithAdditionalOptions`](convert-junit4-to-junit5/src/main/java/jb/UpdateWithAdditionalOptions.java)
3. replace `/path/to/your/test/directory` with the path to the directory containing your tests.
4. run `UpdateWithAdditionalOptions`

The program outputs the name of each file as it goes through them.
It runs quickly.
Under a few seconds for two thousand files.
Then make manual edits at the end (or rollback those test classes and deal with them later).
In particular the following will not compile after running the program and require manual conversion.

* Parameterized tests
* Runners
* Rules
* Categories (there is only basic support for categories)

## Configure and Extend

There are [configuration options](convert-junit4-to-junit5/src/main/java/jb/configuration/Configuration.java) like `preserve formatting` and `dry run` that are not exposed as command line flags.
Have a look at [`UpdateWithAdditionalOptions`](convert-junit4-to-junit5/src/main/java/jb/UpdateWithAdditionalOptions.java) to see how to enable those options from the code.

This tool is build around the idea to have a list of conversions that are executed in sequence one after the other.
Have a look at the [available conversions](convert-junit4-to-junit5/src/main/java/jb/convert/ast).
Conversion you do not need can be excluded by commenting them out in [`JunitConversionLogic`](convert-junit4-to-junit5/src/main/java/jb/convert/JunitConversionLogic.java).

In case your project has custom JUnit 4 code e.g. Rules, you can implement your own [`Conversion`](convert-junit4-to-junit5/src/main/java/jb/convert /ast/Conversion.java) and add it to the [JunitConversionLogic](convert-junit4-to-junit5/src/main/java/jb/convert/JunitConversionLogic.java). 
You will need to have a look at how to work with the AST produced by [Java Parser](https://github.com/javaparser/javaparser).
Looking at the existing conversions for inspiration may help too.
If you write a new conversion that might be helpful to others, feel free to open a [pull request](https://github.com/junit-pioneer/convert-junit4-to-junit5/pulls).
