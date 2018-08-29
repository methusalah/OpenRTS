# Notes

## Build

You will need to install the jbullet and stack-alloc Java libraries.
They are available on the [jMonkeyEngine github repository]().
Get them, put them in the lib subfolder and install them in the local
maven repository :

    mvn install:install-file -Dfile=./lib/jbullet.jar -DgroupId=jbullet -DartifactId=jbullet -Dversion=0.0.1 -Dpackaging=jar
    mvn install:install-file -Dfile=./lib/stack-alloc.jar -DgroupId=stack-alloc -DartifactId=stack-alloc -Dversion=0.0.1 -Dpackaging=jar

## Debug

    gradle run --debug-jvm

## Problems

### FixedPipeline

The FixedPipeline light mode is not supported anymore.

It impacts the MyTerrainLighting map.

#### References

- https://hub.jmonkeyengine.org/t/heightbasedterrain-with-lighting-support/27623
- https://hub.jmonkeyengine.org/t/hacking-jme-3-1-to-support-opengl-1/34776/8

### Multiple warnings for 'non-existent material parameter' at start

    AVERTISSEMENT: In technique 'GBuf':
    Define 'VERTEX_COLOR' mapped to non-existent material parameter 'UseVertexColor', ignoring.
    août 05, 2018 12:55:45 PM com.jme3.material.plugins.J3MLoader readDefine
    AVERTISSEMENT: In technique 'GBuf':
    Define 'MATERIAL_COLORS' mapped to non-existent material parameter 'UseMaterialColors', ignoring.
    août 05, 2018 12:55:45 PM com.jme3.material.plugins.J3MLoader readDefine
    AVERTISSEMENT: In technique 'GBuf':
    Define 'V_TANGENT' mapped to non-existent material parameter 'VTangent', ignoring.
    août 05, 2018 12:55:45 PM com.jme3.material.plugins.J3MLoader readDefine
    AVERTISSEMENT: In technique 'GBuf':
    Define 'MINNAERT' mapped to non-existent material parameter 'Minnaert', ignoring.
    août 05, 2018 12:55:45 PM com.jme3.material.plugins.J3MLoader readDefine
    AVERTISSEMENT: In technique 'GBuf':
    Define 'PARALLAXMAP' mapped to non-existent material parameter 'ParallaxMap', ignoring.

#### References

- https://hub.jmonkeyengine.org/t/terrainlighting-in-jme-3-1/38079

### No loader registered for type "mesh.xml"

Added a dependency to compile "org.jmonkeyengine:jme3-plugins:$jmonkeyengine_version".

# References

## IOS

- https://wiki.jmonkeyengine.org/jme3/ios.html
- https://hub.jmonkeyengine.org/t/my-experience-with-jme-on-ios/31192/6
- https://www.paintcodeapp.com/news/ultimate-guide-to-iphone-resolutions

## Art

- https://www.pinterest.fr/search/pins/?q=low%20poly

## jMonkey community

- https://hub.jmonkeyengine.org/
