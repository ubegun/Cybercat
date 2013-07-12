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
package org.cybercat.automation.persistence.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.utils.WorkFolder;

@XmlType(name="TestCase")
public class TestCase {

    private String testGUID;
    private List<String> images;
    private String exceptionImage;
    private String video;
    private String fullLog;
    private String shortLog;
    private String cookies;
    private String qtName;
    private Set<JiraInfo> bugs;

    public static class JiraInfo {

        private String bugId;
        private String bugSummary;

        public JiraInfo() {
            super();
        }

        public JiraInfo(String bugId, String bugSummary) {
            this.bugId = bugId;
            this.bugSummary = bugSummary;
        }

        public String getBugId() {
            return bugId;
        }

        public void setBugId(String bugId) {
            this.bugId = bugId;
        }

        public String getBugSummary() {
            return bugSummary;
        }

        public void setBugSummary(String bugSummary) {
            this.bugSummary = bugSummary;
        }
    }

    public TestCase() {
        super();
    }
    
    public TestCase(String testGUID) {
        this.testGUID = testGUID;
    }

    public String getTestGUID() {
        return testGUID;
    }

    /**
     *  testGUID - The name of the test represented by this Class object, as a String
     */
    @XmlElement(required = true)
    public void setTestGUID(String testGUID) {
        this.testGUID = testGUID;
    }

    public List<String> getImages() {
        return images;
    }
    
    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getFullLog() {
        return fullLog;
    }

    public void setFullLog(String fullLog) {
        this.fullLog = fullLog;
    }

    public String getShortLog() {
        return shortLog;
    }

    public void setShortLog(String shortLog) {
        this.shortLog = shortLog;
    }

    public String getExceptionImage() {
        return exceptionImage;
    }

    public void setExceptionImage(String exceptionImage) {
        this.exceptionImage = exceptionImage;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public void addImage(String imagePath){
        if(images == null) 
            images = new ArrayList<String>();
        images.add(imagePath);    
    }
    
    public String getQtName() {
        return qtName;
    }

    public void setQtName(String qtName) {
        this.qtName = qtName;
    }

    public Set<JiraInfo> getBugs() {
        return bugs;
    }

    public void setBugs(Set<JiraInfo> bugs) {
        this.bugs = bugs;
    }

    /**
     * @param test
     */
    public void merge(TestCase test) {
        if (this.images == null)
            this.images = new ArrayList<String>();
        if (test.getImages() != null)
            this.images.addAll(getRelativePath(test.getImages()));
        if (test.getVideo() != null)
            this.video = getRelativePath(test.getVideo());
        if (test.getFullLog() != null)
            this.fullLog = getRelativePath(test.getFullLog());
        if (test.getShortLog() != null)
            this.shortLog = getRelativePath(test.getShortLog());
        if(test.getExceptionImage()!= null)
            this.exceptionImage=getRelativePath(test.getExceptionImage());
        if(test.getCookies()!=null)
            this.cookies = getRelativePath(test.getCookies());
        if (test.getQtName() != null)
            this.qtName = test.getQtName();
        if (test.getBugs() != null)
            this.bugs = test.getBugs();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof TestCase))
            return false;
        if (super.equals(obj))
            return true;
        return StringUtils.equals(this.testGUID, ((TestCase) obj).testGUID);
    }

    public String getRelativePath(String pathString){
        Path path = Paths.get(pathString);
        //FIXME rewrite this piece of code to avoid replacing '\' with '/'. right now it is done to make links valid in firefox
        return WorkFolder.Report_Folder.getPath().relativize(path).normalize().toString().replaceAll("\\\\","/");
    }

    public List<String> getRelativePath(List<String> pathStrings){
        List<String> paths = new ArrayList<>();
        for(String pathString : pathStrings){
            paths.add(getRelativePath(pathString));
        }

        return paths;
    }

    @Override
    public String toString() {
        return "TestCase [testGUID=" + testGUID + ", images=" + images + ", exceptionImage=" + exceptionImage
                + ", video=" + video + ", fullLog=" + fullLog + ", shortLog=" + shortLog + ", cookies=" + cookies
                + " ] ";
    }

     
}
