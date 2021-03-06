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
package org.cybercat.automation.addons.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.TestContext;
import org.cybercat.automation.addons.common.MakeScreenshotEvent.ImageFormat;
import org.cybercat.automation.addons.media.FrameSet;
import org.cybercat.automation.addons.media.events.TakeScreenshotEvent;
import org.cybercat.automation.addons.media.events.TakeScreenshotEvent.EffectType;
import org.cybercat.automation.core.AddonContainer;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.events.EventHighlightElement;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventManager;
import org.cybercat.automation.events.EventPageObjectCall;
import org.cybercat.automation.events.EventStartTestStep;
import org.cybercat.automation.events.EventStopTestStep;
import org.cybercat.automation.events.EventTestFail;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.utils.CommonUtils;
import org.cybercat.automation.utils.WorkFolder;


public class ScreenshotManager implements AddonContainer {

    public static final String STEPS_SCREENSHOT = "On test step BEGIN screenshot";
    public static final String ON_BEGIN_STEP_SCREENSHOT = "On test step BEGIN screenshot";
    public static final String ON_END_STEP_SCREENSHOT = "On test step END screenshot";
    public static final String PAGE_OBJECT_SCREENSHOT = "Page object screenshot";
    public static final String PAGE_EVENT_SCREENSHOT = "Page event screenshot";
    public static final String EXCEPTION_SCREENSHOT = "Exception screenshot";
    public static final String SCREENSHOT_MANAGER = "Screenshot manager";
    

    
    private static Logger log = Logger.getLogger(ScreenshotManager.class);
    private int bottomOffset;
    private Font font;
    private int lineOffset; // px
    private Color fontColor;
    private ScreenshotProvider provider;
    private EventManager eventManager;
    private String thestGuid;

    public ScreenshotManager() throws AutomationFrameworkException {
        eventManager = AutomationMain.getEventManager();
        bottomOffset = 200;
        lineOffset = 10;
        font = new Font("Arial", Font.PLAIN, 32);
        fontColor = Color.RED;
    }

    public void setScreenshotProvider(ScreenshotProvider provider) {
        this.provider = provider;
    }

    private Path saveScreen(ImageFormat format, String subtitles) throws Exception {
      Path img = makeSreenshot(WorkFolder.Screenshots.getPath(), CommonUtils.getCurrentDate(), format, subtitles); 
      if(img == null)
        return null;
      TestCase test = new TestCase(thestGuid);
      test.addImage(img.toString());
      TestArtifactManager.updateTestRunInfo(test);      
      return img;
    }
    
  private Path makeSreenshot(Path path, String fileName, ImageFormat format, String subtitles) throws Exception {
    if (StringUtils.isBlank(fileName)) {
      log.error("File name is not be null");
      return null;
    }
    Path screen;
    Files.createDirectories(path);
    String name = CommonUtils.validateFileName(fileName);
    ByteArrayInputStream stream = null;
    try {
      stream = new ByteArrayInputStream(provider.getScreen());
    } catch (Exception e) {
      log.error("Impossible to create screenshot by reason the current browser does not exist.");
      return null;
    }
    BufferedImage image = FrameSet.toBufferedImage(ImageIO.read(stream));
    screen = Paths.get(path.toString(), name + "." + format.getName());
    File file = screen.toFile();
    if (StringUtils.isNotBlank(subtitles)) {
      image = applySubs(image, subtitles);
    }
    ImageIO.write(image, format.getName(), file);
    return screen;
  }

    public BufferedImage applySubs(BufferedImage image, String text) {
        Graphics2D g2 = image.createGraphics();
        g2.setFont(font);

        int height = image.getHeight();
        int width = image.getWidth();

        g2.setColor(fontColor);

        String[] subs = text.split("\n");
        FontMetrics fMetrics = g2.getFontMetrics();
        int lineHeight = fMetrics.getHeight();
        int lineWidth;
        for (int i = subs.length; i > 0; i--) {
            lineWidth = fMetrics.stringWidth(subs[i - 1]);
            int y = height - bottomOffset - ((subs.length - i) * (lineHeight + lineOffset));
            int x = (width / 2) - (lineWidth / 2);
            g2.drawString(subs[i - 1], x, y);
        }
        g2.dispose();
        return image;
    }

    @Override
    public Collection<EventListener<?>> createListeners(TestContext config) {
        thestGuid = config.getTestGuid();
        ArrayList<EventListener<?>> listeners = new ArrayList<EventListener<?>>();
        if (config.hasFeature(ScreenshotManager.SCREENSHOT_MANAGER))
        listeners.add(new EventListener<MakeScreenshotEvent>(MakeScreenshotEvent.class, 100) {

            @Override
            public void doActon(MakeScreenshotEvent event) throws Exception {
                Path screen = makeSreenshot(event.getPath(), event.getFileName(), event.getFormat(), event.getSubtitles());
                TestCase test = new TestCase(thestGuid);
                test.addImage(screen.toString());
                TestArtifactManager.updateTestRunInfo(test);
            }

        });
        if (config.hasFeature(ScreenshotManager.STEPS_SCREENSHOT) || config.hasFeature(ON_BEGIN_STEP_SCREENSHOT))
            listeners.add(new EventListener<EventStartTestStep>(EventStartTestStep.class, 100) {

                @Override
                public void doActon(EventStartTestStep event) throws Exception {
                    if (provider == null) {
                        log.warn("You were trying to make a screenshot before having the browser initialized.");
                        return;
                    }
                    saveScreen(ImageFormat.PNG, null);
                }

            });

        if (config.hasFeature(ScreenshotManager.ON_END_STEP_SCREENSHOT))
            listeners.add(new EventListener<EventStopTestStep>(EventStopTestStep.class, 100) {

                @Override
                public void doActon(EventStopTestStep event) throws Exception {
                    if (provider == null) {
                        log.warn("You were trying to make a screenshot before having the browser initialized.");
                        return;
                    }
                    saveScreen(ImageFormat.PNG, null);
                }

            });
        
        if (config.hasFeature(ScreenshotManager.EXCEPTION_SCREENSHOT))
            listeners.add(new EventListener<EventTestFail>(EventTestFail.class, 100) {

                @Override
                public void doActon(EventTestFail event) throws Exception {
                    if (provider == null) {
                        log.warn("You were trying to make a screenshot before having the browser initialized.");
                        return;
                    }
                    Path img = saveScreen(ImageFormat.PNG, null);
                    TestCase test = new TestCase(thestGuid);
                    test.setExceptionImage(img.toString());
                    TestArtifactManager.updateTestRunInfo(test); 
                    eventManager.notify(new TakeScreenshotEvent(provider, EffectType.RESIZ_BY_WIDTH));
                }
            });
        
        if (config.hasFeature(ScreenshotManager.PAGE_OBJECT_SCREENSHOT))
            listeners.add(new EventListener<EventPageObjectCall>(EventPageObjectCall.class, 100) {

                @Override
                public void doActon(EventPageObjectCall event) throws Exception {
                    saveScreen(ImageFormat.PNG, null);
                }
                
            });
        if ( config.hasFeature(ScreenshotManager.PAGE_EVENT_SCREENSHOT))  
            listeners.add(new EventListener<EventHighlightElement>(EventHighlightElement.class, 100) {

                @Override
                public void doActon(EventHighlightElement event) throws Exception {
                    saveScreen(ImageFormat.PNG, null);
                    eventManager.notify(new TakeScreenshotEvent(provider, EffectType.RESIZ_BY_WIDTH));
                }
                
            });
        return listeners;
    }

    @Override
    public String[] getSupportedFeatures() {
        return new String[]{SCREENSHOT_MANAGER, ON_BEGIN_STEP_SCREENSHOT, ON_END_STEP_SCREENSHOT, STEPS_SCREENSHOT, EXCEPTION_SCREENSHOT, PAGE_OBJECT_SCREENSHOT, PAGE_EVENT_SCREENSHOT}; 
    }
    
    

}
