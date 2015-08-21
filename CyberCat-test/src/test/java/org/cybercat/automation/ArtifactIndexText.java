package org.cybercat.automation;

import static org.junit.Assert.assertNotNull;

import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.ArtifactIndex;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.utils.WorkFolder;
import org.junit.Test;

public class ArtifactIndexText {

    //@Test
    public void testIndex() throws PageModelException{
        WorkFolder.initWorkFolders("/home/ubegun/cyberTest");
        ArtifactIndex index = TestArtifactManager.getInstance().getIndex();
        assertNotNull(index.getLastBuild());
    }
    
}

