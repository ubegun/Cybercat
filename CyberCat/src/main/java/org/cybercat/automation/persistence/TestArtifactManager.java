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
import org.cybercat.automation.utils.WorkFolder;


public class TestArtifactManager {

    private Path indexFile;
    private JAXBContext jc;
    private MappedNamespaceConvention namespace;
    private Unmarshaller unmarshaller;
    private Marshaller marshaller;
    
    private static TestArtifactManager manager;

    private TestArtifactManager() throws PageModelException {
        try {
            jc = JAXBContext.newInstance(ArtifactIndex.class);
            indexFile = Paths.get(WorkFolder.Report_Folder.toString(), "TestArtifactIndex.json");
            Configuration config = new Configuration();
            unmarshaller = jc.createUnmarshaller();
            marshaller = jc.createMarshaller();
            namespace = new MappedNamespaceConvention(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PageModelException(e);
        }

    }

    private ArtifactIndex load() throws PageModelException {
        try {
            BufferedReader br = Files.newBufferedReader(indexFile, Charset.defaultCharset());
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

    private void save(ArtifactIndex index) throws PageModelException {
        try {
            FileWriter fw = new FileWriter(indexFile.toFile());
            XMLStreamWriter xmlStreamWriter = new MappedXMLStreamWriter(namespace, fw);
            marshaller.marshal(index, xmlStreamWriter);
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }
    
    public static synchronized ArtifactIndex getIndex() throws PageModelException{ 
        ArtifactIndex index = new ArtifactIndex();
        if( Files.exists(getInstance().indexFile))
            index = getInstance().load();
        return index; 
    }

    public static synchronized void updateTestInfo(TestCase test) throws PageModelException{  
        ArtifactIndex index = getIndex();
        for(TestCase lTest : index.getTests()){
            if(lTest.equals(test)){
                lTest.merge(test);
                getInstance().save(index);
                return;
            }
        }
        index.getTests().add(test);

        //added to avoid absolute paths in testCase instances, when they are only added to the index, but not merged
        // i.e. force merge
        int testIndex = index.getTests().indexOf(test);
        index.getTests().get(testIndex).merge(test);


        getInstance().save(index);
    }
    
    private static TestArtifactManager getInstance() throws PageModelException{  
        if(manager == null)
            manager = new TestArtifactManager();
        return manager;
    }
}
