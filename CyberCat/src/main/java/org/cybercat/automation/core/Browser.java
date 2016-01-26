/**Copyright 2013 The Cybercat project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cybercat.automation.core;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.TestContext;
import org.cybercat.automation.addons.common.ScreenshotProvider;
import org.cybercat.automation.addons.media.events.TakeScreenshotEvent.EffectType;
import org.cybercat.automation.events.EventHighlightElement;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventManager;
import org.cybercat.automation.events.EventStopTest;
import org.cybercat.automation.events.EventTestFail;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.utils.CommonUtils;
import org.cybercat.automation.utils.WorkFolder;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sun.istack.Nullable;

public class Browser extends ScreenshotProvider implements AddonContainer {

    private static final Logger log = Logger.getLogger(Browser.class);

    private Browsers browserType;
    private boolean isRemote;
    private EventManager eventManager;
    private boolean isClosed = false;
    private boolean waitForEndOfPage = true;
    private ArrayList<EventListener<?>> listeners;

    public static final String BROWSER_CHANNEL = "Browser chammel";

    /**
     * The list of browsers supported by our system.
     */
    public static enum Browsers {
        firefox, chrome, internetExplorer, safari, firefoxWithExtensions
    }

    public Browser(RemoteWebDriver driver, Browsers browserType) throws PageObjectException {
        this(driver, browserType, false);
    }

    public Browser(RemoteWebDriver driver, Browsers browserType, boolean isRemote) throws PageObjectException {
        super(driver);
        this.browserType = browserType;
        this.isRemote = isRemote;
        listeners = new ArrayList<EventListener<?>>();
    }

    public static Browser getCurrentBrowser() throws AutomationFrameworkException {
        return ConfigurationManager.getInstance().getBrowser();
    }

    /**
     * Returns
     */
    public Browsers getBrowserType() {
        return browserType;
    }

    public void callMaximize() {
        driver.manage().window().maximize();
    }

    public String getCurrentUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            isClosed = true;
            log.error(e);
            return null;
        }
    }

    public void get(String finalUrl) {
        try {
            driver.get(finalUrl);
            eventManager.notify(new EventHighlightElement("highlightElement", this, EffectType.RESIZ_BY_WIDTH));
        } catch (Exception e) {
            log.error(e);
        }
    }

    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Sets driver implicit wait
     * 
     * @param implicitTimeout
     */
    public void callImplicitlyWait(long implicitTimeout) {
        driver.manage().timeouts().implicitlyWait(implicitTimeout, TimeUnit.SECONDS);
    }

    public WebDriverWait createWaitDriver(long explicitTimeout) {
        return new WebDriverWait(driver, explicitTimeout);
    }

    public void quit() {
        driver.quit();
        isClosed = true;
    }

    public void switchToFrame(String name) {
        driver.switchTo().frame(name);
    }

    public void switchToFrame(int index) {
        super.driver = (RemoteWebDriver) super.driver.switchTo().frame(index);
    }

    public Browser getAnotherWindow(String name) throws PageObjectException {
        return new Browser((RemoteWebDriver) driver.switchTo().window(name), this.browserType, this.isRemote);
    }

    /**
     * @return window preview handler id
     * @throws AutomationFrameworkException
     */
    public String switchToNewWindow(String url) throws AutomationFrameworkException {
        String previewHandler = getWindowHandle();
        ((JavascriptExecutor) driver).executeScript("window.open('" + url + "','_blank');");
        Set<String> wHandler = getWindowHandles();
        for (String name : wHandler) {
            if (!previewHandler.equals(name)) {
                switchToWindow(name);
                if (getCurrentUrl().contains(url))
                    return previewHandler;
            }
        }
        log.warn("Switching into new window was failed. No one from windows contains target url:" + url);
        return previewHandler;
    }

    /**
     * @param name
     *            - window string id
     * @throws PageObjectException
     */
    public void switchToWindow(String name) throws PageObjectException {
        driver = (RemoteWebDriver) driver.switchTo().window(name);
    }

    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    public void navigateBack() {
        driver.navigate().back();
    }

    public Alert switchToAlert() {
        return driver.switchTo().alert();
    }

    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    public void close() {
      try {
        driver.close();
        driver.quit();
      } catch (Exception e) {
        e.printStackTrace();
      }
      isClosed = true;
      EventManager evm;
      try{
        evm = AutomationMain.getEventManager();
        for (EventListener<?> listener : listeners) {
          evm.unsubscribe(listener);
        }
      }catch(Exception e ){
        log.error(e);
      }

   }

    public void removeAllCookies() {
        driver.manage().deleteAllCookies();
    }

    public void removeCookie(String cookieName) {
        driver.manage().deleteCookieNamed(cookieName);
    }

    public void removeCookie(Cookie cookie) {
        driver.manage().deleteCookie(cookie);
    }

    public Set<Cookie> getCookies() {
        return driver.manage().getCookies();
    }

    public void setCookies(Cookie cookie) {
        driver.manage().addCookie(cookie);
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    /**
     * highlight active element with red color
     */
    public void highlightElement(WebElement element) {
        String bg = element.getCssValue("backgroundColor");
        try {
            this.executeScript("arguments[0].style.backgroundColor = '" + "red" + "'", element);
            Point point = element.getLocation();
            Dimension demention = new Dimension(point.x, point.y);// .getSize();
            eventManager.notify(new EventHighlightElement("highlightElement", this, EffectType.RESIZ_BY_WIDTH, point.getX()
                    + demention.getWidth(), point.getY() + demention.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.executeScript("arguments[0].style.backgroundColor = '" + bg + "'", element);
    }

    /**
     * Waits until all ajax requests on page will end
     * 
     * @param timeToWait
     *            - maximum time to wait
     */
    public void waitForAjaxRequestsEnding(long timeToWaitInSeconds) {

        WebDriverWait wait = createWaitDriver(timeToWaitInSeconds);
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            @Nullable
            public Boolean apply(@Nullable WebDriver input) {
                return (Boolean) executeScript("if(window.jQuery.active >0) return false; else return true;");
            }
        });
    }

    public Actions getActions() {
        return new Actions(driver);
    }

    public Mouse getMouse() {
        return driver.getMouse();
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public boolean isRemote() {
        return driver.getClass() == RemoteWebDriver.class;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String getSessionId() {
        SessionId sessionId = driver.getSessionId();
        return sessionId == null ? "-1" : sessionId.toString();
    }

    public boolean isWaitForEndOfPage() {
        return waitForEndOfPage;
    }

    public void setWaitForEndOfPage(boolean waitForEndOfPage) {
        this.waitForEndOfPage = waitForEndOfPage;
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cybercat.automation.events.AddonContainer#getSupportedFeatures()
     */
    @Override
    public String[] getSupportedFeatures() {
        return new String[] { BROWSER_CHANNEL };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.cybercat.automation.events.AddonContainer#createListeners(org.cybercat.automation.Configuration)
     */
    @Override
    public Collection<EventListener<?>> createListeners(TestContext config) {
        listeners.add(new EventListener<EventTestFail>(EventTestFail.class, 1000) {

            @Override
            public void doActon(EventTestFail event) throws Exception {
                log.error("URL: " + getCurrentUrl());
                saveCookies(event);
            }

        });
        listeners.add(new EventListener<EventStopTest>(EventStopTest.class, 1001) {

            @Override
            public void doActon(EventStopTest event) throws Exception {
                close();
            }

        });
        // TODO Auto-generated method stub
        return listeners;
    }

    private void saveCookies(EventTestFail event) {
        Path cookiePath = Paths.get(WorkFolder.Screenshots.getPath().toString(),  CommonUtils.getCurrentDate() + "_cookies.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(cookiePath, Charset.defaultCharset())) {
            for (Cookie cookie : getCookies()) {
                writer.write("Domain: " + cookie.getDomain() + "; Name: " + cookie.getName() + "; Value: " + cookie.getValue() + "; Path: " + cookie.getPath()
                        + ";\n");
            }
            TestCase test = new TestCase(event.getTestClass().getName());
            test.setCookies(cookiePath.toString());
            TestArtifactManager.updateTestRunInfo(test);
        } catch (Exception e) {
            log.error("Exception occurred while saving cookies.", e);
            return;
        }
        log.info("Cookies are saved to file " + cookiePath);
    }

}