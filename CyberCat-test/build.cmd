mvn clean compile test-compile antrun:run -Dconfig.properties=test.properties -DfailIfNoTests=false -Dconfig.basicArtifactsDir=%USERPROFILE%&& ant -f green.xml

#firefox $HOME/cyberTest/Cybercat/Report/html/rootIndex.html

