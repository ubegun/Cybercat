package org.cybercat.automation.persistence;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.TestContext;
import org.cybercat.automation.core.AddonContainer;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.events.EventListener;
import org.cybercat.automation.events.EventStartTest;
import org.cybercat.automation.persistence.model.ArtifactIndex;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.persistence.model.TestRun;

public class CleanUpBuildsHistoryAddon implements AddonContainer {

  private static Logger log = Logger.getLogger(CleanUpBuildsHistoryAddon.class);
  private int historySize = 10;
  // TODO
  private boolean isTruncModel = false;

  @Override
  public String[] getSupportedFeatures() {
    return null;
  }

  private void cleanUpHistory() throws PageModelException {
    ArtifactIndex index = TestArtifactManager.getInstance().getIndex();
    int toDel = index.getBuilds().size() - historySize;
    if (toDel < 1)
      return;
    log.info("################################################");
    log.info("## start cleanup task");
    Collections.sort(index.getBuilds());
    for (int i = 0; i < toDel; i++) {
      TestRun build = index.getBuilds().get(0);
      for (TestCase tCase : build.getTests()) {
        truncateData(tCase);
      }
      deleteFile(build.getHtmlReport());
      log.info("## remove " + build.getStarted().toString() + " build");
      index.getBuilds().remove(0);
    }
    log.info("## end cleanup task");
    log.info("################################################");
    TestArtifactManager.getInstance().save(index);

  }

  private void truncateData(TestCase tCase) {
    if (tCase.getImages() != null) {
      for (String image : tCase.getImages())
        deleteFile(image);
    }
    deleteFile(tCase.getExceptionImage());
    deleteFile(tCase.getCookies());
    deleteFile(tCase.getFullLog());
    deleteFile(tCase.getShortLog());
    deleteFile(tCase.getVideo());
  }

  private void deleteFile(String file) {
    if (StringUtils.isBlank(file))
      return;
    Path path = TestCase.getAbsolutePath(file);
    try {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>()
      {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                  throws IOException
          {
              Files.delete(file);
              return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
          {
              // try to delete the file anyway, even if its attributes
              // could not be read, since delete-only access is
              // theoretically possible
              Files.delete(file);
              return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
          {
              if (exc == null)
              {
                  Files.delete(dir);
                  return FileVisitResult.CONTINUE;
              }
              else
              {
                  // directory iteration failed; propagate exception
                  throw exc;
              }
          }
      });
      
      
      
      
    } catch (Exception e) {
      log.error("Artifact file cannot be deleted by reason: " + e.getMessage(), e);
    }
  }

  private void init() {
    try {
      this.historySize = AutomationMain.getConfigProperties().getStorageHistorySize();
      this.isTruncModel = AutomationMain.getConfigProperties().isTruncateModel();
    } catch (AutomationFrameworkException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Collection<EventListener<?>> createListeners(TestContext config) {
    init();

    ArrayList<EventListener<?>> listeners = new ArrayList<EventListener<?>>();
    listeners.add(new EventListener<EventStartTest>(EventStartTest.class, 100) {

      @Override
      public void doActon(EventStartTest event) throws Exception {
        cleanUpHistory();
      }
    });
    return listeners;

  }

}
