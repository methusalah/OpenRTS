# Notes

## Build

You will need to install the jbullet and stack-alloc Java libraries.
They are available on the [jMonkeyEngine github repository]().
Get them, put them in the lib subfolder and install them in the local
maven repository :

    mvn install:install-file -Dfile=./lib/jbullet.jar -DgroupId=jbullet -DartifactId=jbullet -Dversion=0.0.1 -Dpackaging=jar
    mvn install:install-file -Dfile=./lib/stack-alloc.jar -DgroupId=stack-alloc -DartifactId=stack-alloc -Dversion=0.0.1 -Dpackaging=jar

# References

## IOS

- https://wiki.jmonkeyengine.org/jme3/ios.html
- https://hub.jmonkeyengine.org/t/my-experience-with-jme-on-ios/31192/6
- https://www.paintcodeapp.com/news/ultimate-guide-to-iphone-resolutions

## Art

- https://www.pinterest.fr/search/pins/?q=low%20poly

## jMonkey community

- https://hub.jmonkeyengine.org/
