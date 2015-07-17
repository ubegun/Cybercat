#!/bin/bash

java -jar ./lib/selenium-server-standalone-2.46.0.jar &> selenium.log &

echo selenium server has been run, created of log file ./selenium.log

mvn clean compile test-compile antrun:run -Dconfig.properties=test.properties -DfailIfNoTests=false -Dconfig.basicArtifactsDir=$HOME/cyberTest

ant -f green.xml

firefox $HOME/cyberTest/Cybercat/Report/html/rootIndex.html

