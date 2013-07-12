package com.mailinator.pages;

import org.apache.log4j.Logger;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.components.Button;
import org.cybercat.automation.components.PageElement;
import org.cybercat.automation.components.TextContainer;
import org.cybercat.automation.components.TextField;

public class MailinatorInboxPage  extends AbstractPageObject {
    
    private static final String PAGE_NAME = "Mailinator inbox page";
    private static final Logger log = Logger.getLogger(MailinatorInboxPage.class);

    protected MailinatorInboxPage(String baseUrl, String pageUrl) {
            super(baseUrl, pageUrl);
    }

    @Override
    protected void initPageElement() {
            addElement(new TextContainer("checkInbox.text", PathType.byXPath, "//div[@id='checkInbox']"));
            addElement(new TextField("checkInbox.email_field", PathType.byXPath, "//div[@id='checkInbox']//input[@id='check_inbox_field']"));
            addElement(new Button("checkInbox.go_button", PathType.byXPath, "//div[@id='checkInbox']//input[@class='buttonGo']"));
            addElement(new Button("checkInbox.confirmationMail_link", PathType.byXPath, "//table[@id='inboxList']//a[text()='adidas micoach registration confirmation']")); 
            addElement(new Button("checkInbox.confirmationLink_link", PathType.byXPath, "//div[@id='message']//a[contains(@href, 'cmd=') and contains(@href, 'code=')]"));
    }

    @Override
    public String getPageName() {
            return PAGE_NAME;
    }

    @Override
    protected PageElement getUniqueElement() throws PageObjectException {
            return getTextContainer("checkInbox.text");
    }
    /**
     * Performs account confirmation after registration of new user
     * @param email
     * @return LoggedInHomePage
     * @throws PageObjectException
     */
    public void confirmUser(String email) throws PageObjectException{
            getTextField("checkInbox.email_field").typeText(email);
            getButton("checkInbox.go_button").click();
            getButton("checkInbox.confirmationMail_link").click();
            getButton("checkInbox.confirmationLink_link").click();
            log.info("Account for user \""+email+"\" is confirmed ");
    }
    
    /**
     * Performs confirmation after changing user's email
     * @param email
     * @return HomePage
     * @throws PageObjectException
     */
    public void confirmEmailChange(String email) throws PageObjectException{
            getTextField("checkInbox.email_field").typeText(email);
            getButton("checkInbox.go_button").click();
            getButton("checkInbox.confirmationMail_link").click();
            pause(3000);
            getButton("checkInbox.confirmationLink_link").click();
            log.info("Change of user's email to: \""+email+"\" is confirmed ");
    }
}
