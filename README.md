# jd-dependency-decompiler-maven-plugin

This maven plugin enables you to decompile your dependencies when there is no source download available. In order to use this plugin you to declare following into your projects pom.xml:

```xml
<pluginRepositories>
    <pluginRepository>
      <id>github-asbachb-releases</id>
      <url>https://raw.github.com/asbachb/mvn-repo/master/releases</url>   
    </pluginRepository>
</pluginRepositories>
```

In order to generated (decompile) or download all of your project dependencies use following command:

```
mvn com.exxeta.oses:jd-dependency-decompiler-maven-plugin:1.0.0:dependency:sources
```

If you to decompile only a specific dependency you might use the parameters artifactIdRegex, groupIdRegex or versionRegex

```
mvn com.exxeta.oses:jd-dependency-decompiler-maven-plugin:1.0.0:dependency:sources -DartifactIdRegex="com\.exxeta.*"
```

The build is verified with apache maven 3.3.3
