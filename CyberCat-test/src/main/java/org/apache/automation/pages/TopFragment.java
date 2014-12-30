package org.apache.automation.pages;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.Button;
import org.cybercat.automation.components.PageElement;

public class TopFragment extends AbstractPageObject{

    
    @Override
    protected void initPageElement() {
        addElement(new Button("Foundation", PathType.byXPath, ".//*[@href='/foundation/']"));
        addElement(new Button("Projects", PathType.byXPath, ".//*[@href='http://projects.apache.org']"));
        addElement(new Button("People", PathType.byXPath, ".//*[@href='http://people.apache.org']"));
        addElement(new Button("Get Involved", PathType.byXPath, ".//*[@href='/foundation/getinvolved.html']"));
        addElement(new Button("Download", PathType.byXPath, ".//*[@href='/dyn/closer.cgi']"));
        addElement(new Button("Support Apache", PathType.byXPath, ".//*[@href='/foundation/sponsorship.html']"));
    }

    @Override
    protected PageElement getUniqueElement() throws AutomationFrameworkException {
        return getButton("Foundation");
    }

    
    public void validateNavigationBar() throws AutomationFrameworkException{
        validateText("Foundation" , getButton("Foundation"));
        validateText("Projects" , getButton("Projects"));
        validateText("People" , getButton("People"));
        validateText("Get Involved" , getButton("Get Involved"));
        validateText("Download" , getButton("Download"));
        validateText("Support Apache" , getButton("Support Apache"));
    }
    
    
    private void validateText(String expected, PageElement value) throws PageObjectException{ 
        if(!StringUtils.contains(expected, value.getText())){
            throw new PageObjectException(value.getName() + "field have text:" + value.getText() + " but expected value is :" + expected);
        }
    }
    
}
