package org.apache.automation.pages;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.*;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;

public class TopFragment extends AbstractPageObject{

    
    @Override
    protected void initPageElement() {
        addElement(new Button("Foundation", PathType.byXPath, ".//*[@href='/foundation/']"));
        addElement(new Button("FoundationWithMissingAttr", PathType.byXPath, ".//*[@href='%s']"));
        addElement(new Button("Projects", PathType.byXPath, ".//*[@href='http://projects.apache.org']"));
        addElement(new Button("People", PathType.byXPath, ".//*[@href='http://people.apache.org']"));
        addElement(new Button("Get Involved", PathType.byXPath, ".//*[@href='/foundation/getinvolved.html']"));
        addElement(new Button("Download", PathType.byXPath, ".//*[@href='/dyn/closer.cgi']"));
        addElement(new Button("Support Apache", PathType.byXPath, ".//*[@href='/foundation/sponsorship.html']"));

        addElement(new TextContainer("SearchForm.Hidden.Input",PathType.byXPath,"//input[@name='sitesearch']"));
        addElement(new TextContainer("FakeElement",PathType.byXPath,"//input[@name = 'fake']"));
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

    public void validateElementStatuses() throws AutomationFrameworkException {

        Map<String,String> map = new HashMap<>();
        map.put("href","http://apache.org/foundation/");

        Assert.assertTrue(validateElement("Foundation"));
        Assert.assertTrue(validateElement("Foundation", StatefulElement.PresentStatus.VISIBLE));
        Assert.assertTrue(validateElement("Foundation", map));
        Assert.assertTrue(validateElement("Foundation", "href", "http://apache.org/foundation/", StatefulElement.PresentStatus.ATTRIBUTE_PRESENT));
        Assert.assertTrue(validateElement("Foundation", map, StatefulElement.PresentStatus.ATTRIBUTE_PRESENT));

        Assert.assertTrue(validateElementWithTimeOut("Foundation", 10));
        Assert.assertTrue(validateElementWithTimeOut("Foundation", StatefulElement.PresentStatus.VISIBLE, 10));
        Assert.assertTrue(validateElementWithTimeOut("Foundation", "href", "http://apache.org/foundation/", 10));
        Assert.assertTrue(validateElementWithTimeOut("Foundation", map, 10));
        Assert.assertTrue(validateElementWithTimeOut("Foundation", "href", "http://apache.org/foundation/", StatefulElement.PresentStatus.VISIBLE, 10));
        Assert.assertTrue(validateElementWithTimeOut("Foundation", map, StatefulElement.PresentStatus.VISIBLE, 10));

        // test with arg substitution

        Assert.assertTrue(validateElement("FoundationWithMissingAttr","/foundation/"));
        Assert.assertTrue(validateElement("FoundationWithMissingAttr", StatefulElement.PresentStatus.VISIBLE,"/foundation/"));
        Assert.assertTrue(validateElement("FoundationWithMissingAttr", map,"/foundation/"));
        Assert.assertTrue(validateElement("FoundationWithMissingAttr", "href", "http://apache.org/foundation/", StatefulElement.PresentStatus.ATTRIBUTE_PRESENT,"/foundation/"));
        Assert.assertTrue(validateElement("FoundationWithMissingAttr", map, StatefulElement.PresentStatus.ATTRIBUTE_PRESENT,"/foundation/"));


        Assert.assertTrue(validateElementWithTimeOut("FoundationWithMissingAttr", 10,"/foundation/"));
        Assert.assertTrue(validateElementWithTimeOut("FoundationWithMissingAttr", StatefulElement.PresentStatus.VISIBLE, 10,"/foundation/"));
        Assert.assertTrue(validateElementWithTimeOut("FoundationWithMissingAttr", "href", "http://apache.org/foundation/", 10,"/foundation/"));
        Assert.assertTrue(validateElementWithTimeOut("FoundationWithMissingAttr", map, 10,"/foundation/"));
        Assert.assertTrue(validateElementWithTimeOut("FoundationWithMissingAttr", "href", "http://apache.org/foundation/", StatefulElement.PresentStatus.VISIBLE, 10,"/foundation/"));
        Assert.assertTrue(validateElementWithTimeOut("FoundationWithMissingAttr", map, StatefulElement.PresentStatus.VISIBLE, 10,"/foundation/"));


        //validate element visibility
        Assert.assertTrue(validateElement("Foundation", StatefulElement.PresentStatus.VISIBLE));
        Assert.assertFalse(validateElement("Foundation", StatefulElement.PresentStatus.PRESENT_NOT_VISIBLE));
        Assert.assertFalse(validateElement("Foundation", StatefulElement.PresentStatus.NOT_PRESENT_ON_DOM));

        //validate element invisibility
        Assert.assertTrue(validateElement("SearchForm.Hidden.Input", StatefulElement.PresentStatus.PRESENT_NOT_VISIBLE));
        Assert.assertFalse(validateElement("SearchForm.Hidden.Input", StatefulElement.PresentStatus.NOT_PRESENT_ON_DOM));
        Assert.assertFalse(validateElement("SearchForm.Hidden.Input", StatefulElement.PresentStatus.VISIBLE));

        // validate element not present on dom
        Assert.assertFalse(validateElement("FakeElement", StatefulElement.PresentStatus.PRESENT_NOT_VISIBLE));
        Assert.assertTrue(validateElement("FakeElement", StatefulElement.PresentStatus.NOT_PRESENT_ON_DOM));
        Assert.assertFalse(validateElement("FakeElement", StatefulElement.PresentStatus.VISIBLE));





    }
    
}
