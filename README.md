#Cybercat

Test automation framework

##Helpful links 
https://www.facebook.com/af.cybercat - You can read last news & short notes about current project release

https://twitter.com/CyberCatAF - project timeline 

##What features are available in this project?

- functional feature boxing (BDD process)
- step-by-step video on tests execution 
- exception screenshots
- functional steps screenshots
- summary report on run tests
- short/detail execution logs
- any page objects may contain any subpage objects (called page fragments)
- any feature may contain another feature
- all functional objects are autowired on runtime 
- Feature version control
- compatible for running integration tests


##How does the project structure look like? 

`
core 
-> page object & fragments
--> feature steps
---> test cases
`

##Cybercat maven repository configuration:

####pom.xml

```xml
...
<repositories>
    <repository>
        <id>Cybercat-mvn-repo</id>
        <url>https://raw.github.com/ubegun/Cybercat/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>  
...
<dependency>
  <groupId>org.cybercat.autotest</groupId>
  <artifactId>cybercat-automation-core</artifactId>
  <version>0.5-SNAPSHOT</version>
</dependency>
...
```

## License
* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
