# Notes

## Build

You will need to install the jbullet and stack-alloc Java libraries.
They are available in the [jMonkeyEngine github repository](https://github.com/jMonkeyEngine/jmonkeyengine/tree/master/lib).
Get them, put them in the lib subfolder and install them in the local maven
repository :

    mvn install:install-file -Dfile=./lib/jbullet.jar -DgroupId=jbullet -DartifactId=jbullet -Dversion=0.0.1 -Dpackaging=jar
    mvn install:install-file -Dfile=./lib/stack-alloc.jar -DgroupId=stack-alloc -DartifactId=stack-alloc -Dversion=0.0.1 -Dpackaging=jar

## Run

    ./gradlew core:run

## Debug

    ./gradlew core:run --debug-jvm

## Problems when porting to JME 3.2.1

### FixedPipeline

The FixedPipeline light mode is not supported anymore.

It impacts the MyTerrainLighting map, the references to the fixed pipeline 
have been commented.

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

According to the post in reference we can ignore these warnings.

#### References

- https://hub.jmonkeyengine.org/t/terrainlighting-in-jme-3-1/38079

### No loader registered for type "mesh.xml"

Added a dependency to compile "org.jmonkeyengine:jme3-plugins:$jmonkeyengine_version".

#### References

- https://hub.jmonkeyengine.org/t/problem-importing-ogre-models/23474/5
