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
package org.cybercat.automation.persistence;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.cybercat.automation.persistence.model.ArtifactIndex;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.persistence.model.TestRun;
import org.cybercat.automation.utils.WorkFolder;

public class TestArtifactManager {

    private JAXBContext jc;
    private MappedNamespaceConvention namespace;
    private Unmarshaller unmarshaller;
    private Marshaller marshaller;
    private TestRun thisTestRun = new TestRun();
    private ArtifactIndex index = new ArtifactIndex();    

    private static TestArtifactManager manager;
    private static Logger log = Logger.getLogger(TestArtifactManager.class);
    
    private TestArtifactManager() throws PageModelException {
        try {
            jc = JAXBContext.newInstance(ArtifactIndex.class);
            Configuration config = new Configuration();
            unmarshaller = jc.createUnmarshaller();
            marshaller = jc.createMarshaller();
            namespace = new MappedNamespaceConvention(config);
            try{
                index = load();
            }catch(Exception e){
                log.error(">>>>>>>> Index file does not exist", e);
            }
        } catch (Exception e) {
            log.error(e);
            throw new PageModelException(e);
        }

    } 
    
    public TestRun getThisTestRun() {
        return thisTestRun;
    }

    public ArtifactIndex getIndex() {
        return index;
    }

    private ArtifactIndex load() throws PageModelException {
        try {
            BufferedReader br = Files.newBufferedReader(WorkFolder.IndexFile.getPath(), Charset.defaultCharset());
            StringBuffer json = new StringBuffer();
            while (br.ready()) {
                json.append(br.readLine());
            }
            JSONObject jObject = new JSONObject(json.toString());
            XMLStreamReader xmlStreamReader = new MappedXMLStreamReader(jObject, namespace);
            ArtifactIndex index = (ArtifactIndex) unmarshaller.unmarshal(xmlStreamReader);
            return index;
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }
    
    protected void save(ArtifactIndex index) throws PageModelException {
        try {
            FileWriter fw = new FileWriter(WorkFolder.IndexFile.getPath().toFile());
            XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(namespace, fw);
            marshaller.marshal(index, xmlStreamWriter);
            this.index = index;
        } catch (Exception e) {
            log.error("Merge exception", e);
            throw new PageModelException(e);
        }
    }

    private void merge() throws PageModelException {
        try {
            List<TestRun> builds = index.getBuilds();
            if(builds.contains(thisTestRun)){
              builds.remove(thisTestRun);
            }
            index.setLastBuild(thisTestRun);
            builds.add(thisTestRun);
            FileWriter fw = new FileWriter(WorkFolder.IndexFile.getPath().toFile());
            XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(namespace, fw);
            index.setBuilds(builds);
            marshaller.marshal(index, xmlStreamWriter);
        } catch (Exception e) {
            log.error("Merge exception" , e);
            throw new PageModelException(e);
        }
    }

    private void updateInfo(TestCase test) throws PageModelException {
        for (TestCase lTest : thisTestRun.getTests()) {
            if (lTest.equals(test)) {
                lTest.merge(test);
                getInstance().merge();
                return;
            }
        }
        thisTestRun.getTests().add(test);
        // added to avoid absolute paths in testCase instances, when they are only added to the index, but not merged
        // i.e. force merge
        int testIndex = thisTestRun.getTests().indexOf(test);
        thisTestRun.getTests().get(testIndex).merge(test);
        merge();
    }

    public synchronized static void setPathToReport(String htmlReportPath) throws PageModelException {
        getInstance().thisTestRun = getInstance().index.getLastBuild();
        getInstance().thisTestRun.setHtmlReport(TestCase.getRelativePath(htmlReportPath));
        getInstance().merge();
    }
    
    
    public synchronized static void updateTestRunInfo(TestCase test) throws PageModelException {
        getInstance().updateInfo(test);
    }

    public static TestArtifactManager getInstance() throws PageModelException {
        if (manager == null)
            manager = new TestArtifactManager();
        return manager;
    }
}
