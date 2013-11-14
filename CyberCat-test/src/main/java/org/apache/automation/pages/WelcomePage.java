package org.apache.automation.pages;

import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.annotations.CCPageFragment;
import org.cybercat.automation.annotations.CCPageURL;
import org.cybercat.automation.annotations.CCProperty;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.Button;
import org.cybercat.automation.components.PageElement;

public class WelcomePage extends AbstractPageObject {

    
    @CCPageURL
    @CCProperty("apache.welcome.url")
    private String welcomePageURL;
    
    @CCPageFragment
    private TopFragment topFragment;

    @CCPageFragment
    private TableOfContentsFragment tableOfContentsFragment;


    @Override
    protected void initPageElement() {
        addElement(new Button("logo", PathType.byXPath, ".//*[@id='header']/h1"));
    }

    @Override
    protected PageElement getUniqueElement() throws AutomationFrameworkException {
        return getButton("logo");
    }
    
    public void validateTopFragment() throws AutomationFrameworkException{
        topFragment.validateNavigationBar();
    }

    public void selectProject(String projectName) throws AutomationFrameworkException{
        tableOfContentsFragment.navigateToProject(projectName);
    }
}
