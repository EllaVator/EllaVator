# EllaVator

[![Join the chat at https://gitter.im/EllaVator/EllaVator](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/EllaVator/EllaVator?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/EllaVator/EllaVator.svg?branch=master)](https://travis-ci.org/EllaVator/EllaVator)

## How to build

**Java 8** is required.
If you have multiple Java installations, make sure you export `JAVA_HOME` to point to the correct version.

### Get the source code

This project contains [git submodules](http://git-scm.com/docs/git-submodule).
Therefore, it should be cloned *recursively*, i.e.,
```
$ git clone --recursive https://github.com/EllaVator/EllaVator.git
```

If this is not done at clone time, it can of course be done later, by running
```
$ git submodule update --init
```

### Build with Gradle

Simply run
```
$ ./gradlew shadowJar
```
to build the standalone application, which will be put into `build/libs`.
The standalone application is a jar file, which can be run with
```
$ java -jar build/libs/EllaVator.jar
```

### Run with Gradle

Instead of building a standalone jar file, it is also possible to just run the application directly, like this:
```
$ ./gradlew :run
```

### known issues

After running the command **$ ./gradlew :run**, if it ouput error like:
```
repository_ellaVator/src/main/java/ModuleElla.java:17: error: package opendial does not exist
import opendial.DialogueState;
               ^
repository_ellaVator/src/main/java/ModuleElla.java:18: error: package opendial does not exist
import opendial.DialogueSystem;
               ^
repository_ellaVator/src/main/java/ModuleElla.java:19: error: package opendial.modules does not exist
import opendial.modules.Module;
                       ^
repository_ellaVator/src/main/java/ModuleElla.java:31: error: cannot find symbol
public class ModuleElla implements Module {
                                   ^
  symbol: class Module
repository_ellaVator/src/main/java/ModuleElla.java:48: error: cannot find symbol
	public ModuleElla(DialogueSystem system) {
	                  ^
```

May be one of the submodule is not installed correctly. Try to run the following command:
```
$ git submodule update --init
```
