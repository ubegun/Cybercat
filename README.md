#Cybercat

Test automation framework

##What features are available in this project?

- functional feature boxing
- step-by-step video on tests execution 
- exception screenshots
- functional steps screenshots
- short/detail execution logs
- any page objects may contain any subpage objects (called page fragments)
- any feature may contain another feature
- all functional objects are autowired on runtime 
- Feature version control


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

##Coming soon

- exclude Ant step from build process 
- new framework entity @CCProperty fields & Integration Services for integration test engine
- update the Browser object for new methods like getCurrent Browser (return browser instance for current thread ) or state control method - void open()

## License
* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
