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
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

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
import org.testng.log4testng.Logger;

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
                log.error(e);
            }
        } catch (Exception e) {
            log.error(e);
            throw new PageModelException(e);
        }

    }

    private Path getIndexFile(){
        return Paths.get(WorkFolder.Report_Folder.toString(), "TestArtifactIndex.json");
    }
    
    public TestRun getThisTestRun() {
        return thisTestRun;
    }

    public ArtifactIndex getIndex() {
        return index;
    }

    private ArtifactIndex load() throws PageModelException {
        try {
            BufferedReader br = Files.newBufferedReader(getIndexFile(), Charset.defaultCharset());
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

    private void save() throws PageModelException {
        try {
            log.info("-------------------------------------> Save begin");
            if(index.getBuilds().contains(thisTestRun)){
                index.getBuilds().remove(thisTestRun);
            }
            index.setLastBuild(thisTestRun);
            index.getBuilds().add(thisTestRun);
            FileWriter fw = new FileWriter(getIndexFile().toFile());
            XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(namespace, fw);
            marshaller.marshal(index, xmlStreamWriter);
            log.info("-------------------------------------> Save end");
        } catch (Exception e) {
            log.error("-------------------------------------> Save error" , e);
            throw new PageModelException(e);
        }
    }

    private void updateInfo(TestCase test) throws PageModelException {
        for (TestCase lTest : thisTestRun.getTests()) {
            if (lTest.equals(test)) {
                lTest.merge(test);
                getInstance().save();
                return;
            }
        }
        thisTestRun.getTests().add(test);
        // added to avoid absolute paths in testCase instances, when they are only added to the index, but not merged
        // i.e. force merge
        int testIndex = thisTestRun.getTests().indexOf(test);
        thisTestRun.getTests().get(testIndex).merge(test);
        save();
    }

    public synchronized static void setPathToReport(String htmlReportPath) throws PageModelException {
        getInstance().thisTestRun = getInstance().index.getLastBuild();
        getInstance().thisTestRun.setHtmlReport(htmlReportPath);
        getInstance().save();
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
