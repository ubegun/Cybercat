package org.cybercat.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.cybercat.automation.PersistenceManager;
import org.cybercat.automation.persistence.TestArtifactManager;
import org.cybercat.automation.persistence.model.ArtifactIndex;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.persistence.model.TestRun;
import org.cybercat.automation.utils.WorkFolder;
import org.cybercat.external.addon.timer.TestCaseTimers;
import org.cybercat.external.addon.timer.Timer;
import org.cybercat.report.model.ReportFile;
import org.cybercat.report.model.ReportIndex;
import org.cybercat.report.model.RowData;
import org.cybercat.report.model.TestCaseData;
import org.cybercat.report.model.TimeSeries;

public class ReportService {

    private final static Logger log = Logger.getLogger(ReportService.class);

    private ConfigProperties configProperties;
    private Path reportFolder;
    private PersistenceManager pManager;
    private TestArtifactManager tam;
    private TestRun thisBuild;

    private ArtifactIndex testArtifactIndex;

    private HashMap<String, TestCaseData> testCaseData;

    public ReportService() throws PageModelException {
        init();
    }

    public void init() throws PageModelException {
        configProperties = new ConfigProperties();
        // set basic directory to store artifacts
        String basicArtifactsDir = System.getProperty("config.basicArtifactsDir");
        WorkFolder.initWorkFolders(basicArtifactsDir);
        this.reportFolder = WorkFolder.FM_Report.getPath();
        pManager = new PersistenceManager();
        tam = TestArtifactManager.getInstance();
        thisBuild = tam.getIndex().getLastBuild();
        setTestArtifactIndex(tam.getIndex());
    }

    public Path getReportFolder() {
        return reportFolder;
    }

    public ConfigProperties getConfigProperties() {
        return configProperties;
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

    public void deleteAll() throws PageModelException {
        ReportIndex index = getReportIndex();
        if (index == null)
            log.warn("Report index is not found, the report folder sould be empty.");
        for (ReportFile rf : index.getReportFiles()) {
            delete(rf);
        }
    }

    public void delete(ReportFile rf) throws PageModelException {
        try {
            if (!Files.deleteIfExists(Paths.get(rf.getAbsilutePath())))
                log.error("File is not exist -" + rf.getAbsilutePath());
        } catch (IOException e) {
            new PageModelException(e);
        }

    }

    public TestRun getLastBuildInfo() {
        return this.thisBuild;
    }

    public void saveIndex(ReportIndex rIndex) throws PageModelException {
        pManager.save(rIndex);
    }

    /**
     * @return sorted builds history.
     */
    public List<TestRun> getBuildsHistory() {
        List<TestRun> bHistory = testArtifactIndex.getBuilds();
        Collections.sort(bHistory, new Comparator<TestRun>() {

            @Override
            public int compare(TestRun o1, TestRun o2) {
                return o2.compareTo(o1);
            }
        });
        return bHistory;
    }

    /**
     * @return the testArtifactIndex - Information about all the releases of tests.
     */
    public ArtifactIndex getTestArtifactIndex() {
        return testArtifactIndex;
    }

    /**
     * @param testArtifactIndex
     *            the testArtifactIndex to set
     */
    public void setTestArtifactIndex(ArtifactIndex testArtifactIndex) {
        // TODO:
        this.testArtifactIndex = testArtifactIndex;
    }
 
    public TestCaseData getSeriesData(String testCase){
        if(testCaseData == null){
            try {
                testCaseData = loadSeriesData();
            } catch (PageModelException e) {
                throw new RuntimeException(e);
            }
        }
        return testCaseData.get(testCase);
    }
    
    protected HashMap<String, TestCaseData> loadSeriesData() throws PageModelException {
        HashMap<TestCaseData, TimeSeries> series = new HashMap<TestCaseData, TimeSeries>();
        HashMap<String, TestCaseData> tcData = new HashMap<String, TestCaseData>(); 
        HashMap<Date, RowData> rDatas = new HashMap<Date, RowData>(); 
        
        List<TestCaseTimers> tcTimers = pManager.load(TestCaseTimers.class);

        for(TestCaseTimers tct : tcTimers){
            for(Timer t :tct.getTimers()){
                TestCaseData tCaseData = tcData.get(t.getTestGuid());
                if(tCaseData == null){
                    tCaseData = new TestCaseData(0, new ArrayList<TimeSeries>(), new ArrayList<RowData>());
                    tcData.put(t.getTestGuid(), tCaseData);
                }
                TimeSeries iSeries = series.get(tCaseData);
                if(iSeries == null){
                    tCaseData.setNumberOfSeries(tCaseData.getNumberOfSeries() + 1);
                    iSeries = new TimeSeries(tCaseData.getNumberOfSeries() - 1 , t.getTestGuid(), t.getName());
                    tCaseData.getSeries().add(iSeries);
                    series.put(tCaseData, iSeries);
                }
                RowData data = rDatas.get(t.getBuildGuid());
                if(data == null){
                    data = new RowData(t.getBuildGuid().getTime(), new long[tCaseData.getNumberOfSeries()]);
                    rDatas.put(t.getBuildGuid(), data);
                    tCaseData.getRowDatas().add(data);
                    
                }
                long[] dataSeries = data.getSeries(); 
                if(dataSeries.length <= iSeries.getIndex()){
                    long[] newDSeries = new long[iSeries.getIndex()];
                    for(int i = 0; i < dataSeries.length; i++){
                        newDSeries[i] = dataSeries[i];
                    }
                    dataSeries = newDSeries;
                    data.setSeries(newDSeries);
                }
                dataSeries[iSeries.getIndex()] = t.getDuration();
            }
        }
        for(TestCaseData tcd : tcData.values()){
        Collections.sort(tcd.getRowDatas() , new Comparator<RowData>(){

            @Override
            public int compare(RowData o1, RowData o2) {
                return o1.getTimeLabel().compareTo(o2.getTimeLabel());
            }
        });        
        Collections.sort(tcd.getSeries(), new Comparator<TimeSeries>(){

            @Override
            public int compare(TimeSeries o1, TimeSeries o2) {
                return Integer.compare(o1.getIndex(), o2.getIndex());
            }
        } );
        }
        return tcData;
    }

}
