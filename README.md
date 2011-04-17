# Gesture Drawing

## About

Gesture Drawing is a simple web application that provides a way to have a slideshow of pictures based on the categories you choose.
The pictures should be organized in a hierarchy of folders that will be the different categories you can choose:

    pictures\
     `-- male
          `-- standing
          `-- sitting
     `-- female
          `-- laying
     ...

See the Configuration section to know how to configure the pictures path.

It was mainly done to play with Scala, [Scalatra](https://github.com/scalatra/scalatra) and some JSON libraries.

**The application is not visually polished...**

## Configure

The configuration is done in the file `~/.gdrawing/gdrawing.conf`. For now you can only configure the root path where the hierarchy of pictures will be found

    pictures_path = "/path/to/pictures"

## Run

### Using sbt


You can launch the application using:

    $ java -jar sbt.jar run

You can also use the `jetty-run` sbt command:

    $ sbt
    $ jetty-run

### Packaging a WAR file

To package the application, use:

    $ sbt package

You will end up with a `gesture-drawing*.war` file in the `target` folder. You can then deploy it inside Tomcat or Jetty...


