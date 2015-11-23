package org.cybercat.common;

import static org.testng.AssertJUnit.assertTrue;

import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.utils.WorkFolder;
import org.junit.Test;

public class WorkFolderTest {

    @Test
    public void testPath(){
        String path = WorkFolder.Screenshots.toString();
        System.out.println(path);
        path = TestCase.getRelativePath(path);
        System.out.println(path);
        path = TestCase.getAbsolutePath(path).toString();
        System.out.println(path);
        assertTrue(WorkFolder.Screenshots.toString().equals(path));
    }
    
}
