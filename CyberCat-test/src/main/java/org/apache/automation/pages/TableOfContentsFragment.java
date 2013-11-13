package org.apache.automation.pages;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.Button;
import org.cybercat.automation.components.GroupElements;
import org.cybercat.automation.components.PageElement;
import org.cybercat.automation.components.TextContainer;

public class TableOfContentsFragment  extends AbstractPageObject {

    private static Logger log = Logger.getLogger(TableOfContentsFragment.class); 
    

    @Override
    protected void initPageElement() {
        addElement(new TextContainer("project paragraph", PathType.byXPath ,".//*[@id='footer']/div[1]/div[1]/h4"));
        addElement(new GroupElements<Button>("project links", PathType.byXPath, ".//*[@id='footer']/div[1]/div[1]/ul/*//a", Button.class));
    }

    @Override
    protected PageElement getUniqueElement() throws AutomationFrameworkException {
        return getTextContainer("project paragraph");
    }

    @SuppressWarnings("unchecked")
    public void navigateToProject(String projectName) throws AutomationFrameworkException{
        GroupElements<Button> links = (GroupElements<Button>) getGroupElements("project links");
        links.getElementByText("Tomcat").click();
    }
}
