package org.cybercat.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.cybercat.automation.PersistenceManager;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.persistence.model.TestRun;
import org.cybercat.automation.utils.WorkFolder;

public class ReportService {

  private final static Logger log = Logger.getLogger(ReportService.class);
  
  private ConfigProperties configProperties;
  private Path reportFolder;
  private PersistenceManager pManager;
  private TestArtifactManager tam;
  private TestRun thisBuild;

  public ReportService() throws PageModelException {
    init();
  }

  public void init() throws PageModelException {
    configProperties = new ConfigProperties();
    // set basic directory to store artifacts
    String basicArtifactsDir = System.getProperty("config.basicArtifactsDir");
    WorkFolder.initWorkFolders(basicArtifactsDir);
    reportFolder = WorkFolder.Report_Folder.getPath();
    pManager = new PersistenceManager();
    tam = TestArtifactManager.getInstance();
    thisBuild = tam.getIndex().getLastBuild();
  }

  /**
   * Adds to index files or directory for cleanup history in case the parameter of history size has been defined.
   * 
   * @param rFile
   * @throws PageModelException
   */
  public void addFileToIndex(ReportFile rFile) throws PageModelException {
    // TODO: move to catch
    ReportIndex index = getReportIndex();
    index.getReportFiles().add(rFile);
    pManager.save(index);
  }

  public ReportIndex getReportIndex() throws PageModelException {
    return pManager.loadFirst(ReportIndex.class);
  }
  
  public void deleteAll() throws PageModelException{
    ReportIndex index = getReportIndex();
    if(index == null)
      log.warn("Report index is not found, the report folder sould be empty.");
    for(ReportFile rf: index.getReportFiles()){
      delete(rf);
    }
  }
  
  public void delete(ReportFile rf) throws PageModelException{
    try {
      if(!Files.deleteIfExists(Paths.get(rf.getAbsilutePath())))
        log.error("File is not exist -" + rf.getAbsilutePath());
    } catch (IOException e) {
      new PageModelException(e);
    }
    
  }
  
  public TestRun getLastBuildInfo(){
    return this.thisBuild;
  }

  public void saveIndex(ReportIndex rIndex) throws PageModelException {
    pManager.save(rIndex);
  }
  
}
