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

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;

public abstract class ScreenshotProvider {

    protected RemoteWebDriver driver;

    public ScreenshotProvider(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public byte[] getScreen() {
        return (byte[]) getScreenshotAs(OutputType.BYTES);
    }

    private <T> Object getScreenshotAs(OutputType<T> outputType) {
        if (driver.getClass() == RemoteWebDriver.class) {
            Augmenter augmenter = new Augmenter();
            TakesScreenshot ts = (TakesScreenshot) augmenter.augment(driver);
            return ts.getScreenshotAs(outputType);
        } else {
            return ((TakesScreenshot) driver).getScreenshotAs(outputType);
        }
    }

}
