The main purpose of Cybercat project is:
 - speed up automation tests creation
 - minimize familiarization time with the project environment
 - minimize efforts for maintenance large number of tests
 - providing detailed informaton about tests execution process



﻿*************Recource files *************

Below you can find the list of files necessary for starting the project. The structure of their files in the project tree look at the CyberCat-test sample project.

Locators_en.properties – contains localization pair for Xpath. (see the chapter “Localization mechanism”)
MetaData.properties – this file contains the authorization parameters  for some modules of the system (not used currently and will be reworked or removed in the upper versions of the product)
pageObjectContext.xml – this file describes the way of initializing objects AbstractPageObject (see the chapter Page Objects)
test.properties – a configuration file containing the main system settings (see the chapter Configuration file)


 *************Configuration file**********

This file contains different settings necessary for project working. By default the framework looks up for test.properties file:
test
  `---resources
        `--test.properties  
but you can change the file name and its place in the project structure by using the system variable in the command line - Dconfig.properties=[my project].properties.
Now more details on the properties:

The settings file is divided into two sections: mandatory (system) settings and the settings defined by the user. All the mandatory settings are ennumerated in the /CyberCat-test/src/test/resources/test.properties.null file, which is not used by the system, but only demonstrates the minimum set necessary for starting the system.

#Web driver settings
In this block the way of accessing the selenium driver is described.

#this option works with no-remote web driver configuration only
this option is necessary only in that case if you use a custom profile to work with a browser (for a more detailed information see at SeleniumHQ site).
While working with a remote selenium driver there is no need to define a custom profile and the way of configuration is described at SeleniumHQ site for that case.

#Site language
This option defines site language localization and is used in the localization mechanism that will be described in the separate chapter.

#Jira Addon
this block is used for the integration with Jira issue tracking system. If you don`t use this mechanism, just leave the lines blank.

#Version control
This block of settings is responsible for the version system control settings. F or the current moment version system control works with the classes implementing Ifeature,IIntegrationService interfaces.
The marker interface for this mechanism is IversionControl interface.
If you don`t want to use this mechanism at the moment, you can set the following values:
app.version=1
version.control.root.package=org.myproject

There will be a chapter devoted to the more detailed way of the working of this mechanism.
Here you can also add any settings you wish. Any settings downloaded into the system can be get with the help of the following methods of the AutomationMain class:

  getProperty(String)
  getPropertyBoolean(String)
  getPropertyLong(String)
  getPropertyUrl(String)

where String is the name of the option in the configuration file
for example:
AutomationMain.getProperty("version.control.root.package")
 вернет строку содержащую "org.myproject"
 
 
 ************** Test Case****************
 
 
 *************** Feature ***************
 
 
 **************Page Objects**************
 
 
 *************Integration Service ******************



**************Localization mechanism**************



The mechanism providing life cycle of PageObject has been reworked. Hurray! For now, Cybercat can be called Automation Tests Container based on BDD process.

Now Cybercat totally provides lifecycles of all business objects created while writing test cases. Those mechanisms are quite simple and hopefully are easy to understand  intuitively.

@CCPageURL - this annotation is used in page objects for validation of the current url in browser, i.e. when we start working with a page object which has a field annotated by CCPageURL, the correspondance of that url and the current url in browser downloaded by web driver is checked.

 
  







–	    
