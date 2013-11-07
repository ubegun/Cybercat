package org.apache.automation.pages;

import java.util.HashMap;

import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.annotations.CCPageFragment;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.Button;
import org.cybercat.automation.components.PageElement;

public class WelcomePage extends AbstractPageObject {

    @CCPageFragment
    private TopFragment topFragment;

    @CCPageFragment
    private TableOfContentsFragment tableOfContentsFragment;

    public WelcomePage() {
    }

    public WelcomePage(String pageUrl) {
        super(pageUrl);
    }

    public WelcomePage(String baseUrl, String pageUrl) {
        super(baseUrl, pageUrl);
    }

    @Override
    protected void initPageElement() {
        addElement(new Button("logo", PathType.byXPath, ".//*[@id='header']/h1"));
    }

    @Override
    protected PageElement getUniqueElement() throws PageObjectException {
        return getButton("logo");
    }
    
    public void validateTopFragment() throws PageObjectException{
        topFragment.validateNavigationBar();
    }

    public void selectProject(String projectName) throws AutomationFrameworkException{
        tableOfContentsFragment.navigateToProject(projectName);
    }
}
