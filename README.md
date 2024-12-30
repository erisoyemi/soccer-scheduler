# Soccer League Scheduler

This algorithm contains a set-based approach utilizing a Genetic Algorithm in combination with an Or-Tree-based search. The Or-Tree is used to generate the initial set of valid schedules used by the set-based search, and then used throughout to ensure that hard-constraints are satisfied whenever an assignments is produced by the Crossover or Mutation extension function.

## Authors
```bash
Chris Axten (UCID: 30140609)
Erioluwa Soyemi (UCID: 30127678)
Grace Kelly Osena (UCID: 30074352)
Cole Briggs (UCID: 30149709)
```

# Installation

If not already downloaded, navigate to https://github.com/briggscole/soccer-league-scheduler and download the codebase.

# Running

`scheduler.jar` has been provided. In order to run the program, execute the following command from the root directory:

```bash
java -jar scheduler.jar <input filename> <wMinFilled> <wPref> <wPair> <wSecDiff> <penGameMin> <penPracticeMin> <penNotPaired> <penSection>
```

*NOTE: `<input filename>` must be the path of the input file, relative to the root directory. We recommend placing input files into the root, so that the full `<input filename>` can just be the name of the file.*

# Compiling and JARing from scratch
If you wish to compile the code and create the .jar file from scratch, run the following two commands from the root directory:

## Step 1) Compile:
```bash
javac -d bin -sourcepath src/main/java src/main/java/com/scheduler/Main.java
```

## Step 2) Jar:
```bash
jar cfm scheduler.jar manifest.txt -C bin .
```