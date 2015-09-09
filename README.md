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
