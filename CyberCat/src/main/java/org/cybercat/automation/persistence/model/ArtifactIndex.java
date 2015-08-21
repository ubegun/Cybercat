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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ArtifactIndex")
public class ArtifactIndex {

    private List<TestRun> builds = new CopyOnWriteArrayList<TestRun>();
    private TestRun lastBuild; 

    public List<TestRun> getBuilds() {
        return builds;
    }

    public void setBuilds(List<TestRun> builds) {
        this.builds = builds;
    }

    public TestRun getLastBuild() {
        return lastBuild;
    }

    public void setLastBuild(TestRun lastBuild) {
        this.lastBuild = lastBuild;
    }

    @Override
    public String toString() {
        return "ArtifactIndex [builds=" + builds + ", lastBuild=" + lastBuild + "]";
    }
    

}
