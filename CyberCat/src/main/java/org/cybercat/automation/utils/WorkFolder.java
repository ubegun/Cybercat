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
package org.cybercat.automation.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WorkFolder {

    public static final Path Home = Paths.get("Cybercat");

    public static final WorkFolder Log = new WorkFolder(Home, Paths.get("LOG"));
    public static final WorkFolder Har = new WorkFolder(Home, Paths.get("LOG", "Har"));
    public static final WorkFolder Model = new WorkFolder(Home, Paths.get("Model"));
    public static final WorkFolder Screenshots = new WorkFolder(Home, Paths.get("WebDriverScreenshots"));
    public static final WorkFolder DownloadedFiles = new WorkFolder(Home, Paths.get("DownloadedFiles"));
    public static final WorkFolder MediaFolder = new WorkFolder(Home, Paths.get("Video"));
    public static final WorkFolder Report = new WorkFolder(Home, Paths.get("Report"));
    public static final WorkFolder Report_Folder = new WorkFolder(Home, Paths.get("Report", "html"));
    public static final WorkFolder Extensions_Relative = new WorkFolder(Home, Paths.get("extensions"));

    private static Path BASIC_FOLDER = Paths.get(System.getProperty("user.home"));

    private Path path;

    /**
     * Initialize basic directory to store artifacts
     * 
     * @param basicFolderPath
     */
    public static String initWorkFolders(String basicFolderPath) {
        if (basicFolderPath != null)
            BASIC_FOLDER = Paths.get(basicFolderPath);
        return BASIC_FOLDER.toString();
    }

    private WorkFolder(Path... paths) {
        StringBuffer result = new StringBuffer("");
        for (Path path : paths) {
            result.append("/").append(path.toString());
        }
        this.path = Paths.get(result.toString());
    }

    private WorkFolder(Path path) {
        this.path = path;
    }

    private WorkFolder() {
    }

    public boolean exists() {
        return Files.exists(getPath());
    }

    public void createDirectory() {
        try {
            Files.createDirectories(getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void md() throws IOException {
        Files.createDirectories(getPath());
    }

    /**
     * Returns absolute path of this folder (BaseDir path + relative folder path)
     * 
     * @return
     */
    public Path getPath() {
        return Paths.get(BASIC_FOLDER.toString(), this.path.toString());
    }

    /**
     * Returns relative path of this folder without Basic dir
     * 
     * @return
     */
    public Path getRelativePath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String toString() {
        return getPath().toString();
    }

    public static WorkFolder[] values() {

        List<WorkFolder> retList = new ArrayList<>();

        try {

            Field[] fields = WorkFolder.class.getDeclaredFields();
            for (Field field : fields)
                if (field.getType().equals(WorkFolder.class))
                    retList.add((WorkFolder) field.get(new Object()));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return retList.toArray(new WorkFolder[retList.size()]);
    }

    public static WorkFolder valueOf(String name) {
        try {
            Field[] fields = WorkFolder.class.getDeclaredFields();
            for (Field field : fields)
                if (field.getType().equals(WorkFolder.class) && field.getName().equals(name))
                    return (WorkFolder) field.get(new WorkFolder());

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
