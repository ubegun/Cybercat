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
package org.cybercat.automation.components;

import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject.PathType;
import org.cybercat.automation.core.Browser;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;


public class SvgChart extends PageElement{
	
    public static class Position {
      int x, y, h ,w = 0;

      public Position(Point location, Dimension size){
        this(location.x, location.y, size.height , size.width);
      }
      
      public Position(int x, int y, int h, int w) {
        super();
        this.x = x;
        this.y = y;
        this.h = h;
        this.w = w;
      }

      public int getX() {
        return x;
      }

      public int getY() {
        return y;
      }

      public int getHeight() {
        return h;
      }

      public int getWidth() {
        return w;
      }

      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + h;
        result = prime * result + w;
        result = prime * result + x;
        result = prime * result + y;
        return result;
      }

      @Override
      public boolean equals(Object obj) {
        if (this == obj)
          return true;
        if (obj == null)
          return false;
        if (getClass() != obj.getClass())
          return false;
        Position other = (Position) obj;
        if (h != other.h)
          return false;
        if (w != other.w)
          return false;
        if (x != other.x)
          return false;
        if (y != other.y)
          return false;
        return true;
      }
      
    }
    
    private Position position;
    
    public SvgChart(String name, PathType type, String path) {
        super(name, type, path);
    }
    
    @Override
    public void initWebElement(Browser browser) throws PageObjectException {
        super.initWebElement(browser);
        position = new Position(getElement().getLocation(), getElement().getSize());
    }
    
    public Position getDescarteProperties() {
      return position;
    }

    public void dragAndDrop(Point from, Point to) throws AutomationFrameworkException{
        // since dragAndDrop in selenium works like piece of ... we should split drag and drop
        Browser.getCurrentBrowser().getActions()
    			.moveToElement(getElement(), from.getX(), from.getY())
    			.clickAndHold()
    			.moveByOffset(to.getX(), to.getY()).perform();
        Browser.getCurrentBrowser().getActions()
    			.release()
    			.perform();
    }
    
}
