/**
 * 
 */
package org.cybercat.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cybercat.automation.persistence.model.TestCase;
import org.cybercat.automation.persistence.model.TestRun;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author ubegun
 *
 */
public class FreeMarkerAdapter implements IReporter {

    private final static Logger log = Logger.getLogger(FreeMarkerAdapter.class);

    private static final String INDEX_TEMPLATE = "index.html.fmt";
    private static final String INDEX_HTML = "index.html";

    private static final String BUILDS_LIST = "buildsList";

    private static final String TEST_CASE = "testCases";
    private static final String SUITE_HTML = "suite_%s.html";
    private static final String SUITE_TEMPLATE = "suite.html.fmt";

    private ReportService sReport;
    private TestRun thisTestRun;
    private ReportIndex thisIndex;
    private Map<String, Object> root = new HashMap<String, Object>();

    private Configuration cfg;

    private URL templatesFolder;

    private void init(){
        try {
            sReport = new ReportService();

            templatesFolder = FreeMarkerAdapter.class.getResource("/FMReport/");
            log.info("FreeMarkerAdapter has been initialized by " + templatesFolder.toString() + " teplates path.");

            // Create your Configuration instance, and specify if up to what FreeMarker
            // version (here 2.3.22) do you want to apply the fixes that are not 100%
            // backward-compatible. See the Configuration JavaDoc for details.
            cfg = new Configuration(Configuration.VERSION_2_3_22);

            // Specify the source where the template files come from. Here I set a
            // plain directory for it, but non-file-system sources are possible too:
            cfg.setClassForTemplateLoading(FreeMarkerAdapter.class, "/FMReport/");

            // Set the preferred charset template files are stored in. UTF-8 is
            // a good choice in most applications:
            cfg.setDefaultEncoding("UTF-8");

            // Sets how errors will appear.
            // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

            thisTestRun = sReport.getLastBuildInfo();
            thisIndex = new ReportIndex();
            thisIndex.setId(thisTestRun.getStarted().getTime());

        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    protected void createSuites() {
        try {
            List<TCReport> testCases = new ArrayList<TCReport>();
            for (TestCase tc : sReport.getLastBuildInfo().getTests()) {
                testCases.add(new TCReport(tc));
            }
            root.put(TEST_CASE, testCases);
            Template tSuite = cfg.getTemplate(SUITE_TEMPLATE);
            String suiteFile = String.format(SUITE_HTML, sReport.getLastBuildInfo().getStartedAsFileName());
            Path pSuite = sReport.getReportFolder().resolve(suiteFile);
            generateFile(pSuite.toFile(), tSuite);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    protected void createIndexPage() {
        try {
            root.put(BUILDS_LIST, sReport.getBuildsHistory());
            Template tIndex = cfg.getTemplate(INDEX_TEMPLATE);
            Path pIndex = sReport.getReportFolder().resolve(INDEX_HTML);
            generateFile(pIndex.toFile(), tIndex);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    protected void copyNonTeplateFiles(final Path source, final Path destination) {
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        if (!file.getFileName().toString().endsWith(".fmt")) {

                            Path newFile = Files.copy(file,
                                    destination.resolve(destination.toString() + trimSorcePath(file)));
                            thisIndex.getReportFiles().add(new ReportFile(newFile.toString(), "file"));

                        }
                    } catch (FileAlreadyExistsException e1) {
                        // nothing
                    } catch (Exception e) {
                        log.error(file.toString() + "\n" + e.toString());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    try {
                        Path newDir = Files
                                .createDirectories(destination.resolve(destination.toString() + trimSorcePath(dir)));
                        thisIndex.getReportFiles().add(new ReportFile(newDir.toString(), "dir"));
                    } catch (FileAlreadyExistsException e1) {
                        // nothing
                    } catch (Exception e) {
                        log.error(e);
                    }
                    return FileVisitResult.CONTINUE;
                }

                private String trimSorcePath(Path path) {
                    String ret = path.toString();
                    return ret.substring(ret.indexOf("FMReport") + 8);
                }

            });
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    protected Map<String, Object> getRoot() {
        return root;
    }

    /**
     * Generate the specified output file by merging the specified FreeMarker template with the supplied context.
     */
    protected void generateFile(File file, Template tmp) throws Exception {
        Writer writer = new BufferedWriter(new FileWriter(file));
        try {
            tmp.process(root, writer);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        init();
        Path from = null;
        FileSystem fs;
        if (templatesFolder.toString().contains("!")) {
            Map<String, String> env = new HashMap<>();
            String[] array = templatesFolder.toString().split("!");
            try {
                fs = FileSystems.newFileSystem(URI.create(array[0]), env);
                from = fs.getPath(array[1]);
            } catch (IOException e) {
                new RuntimeException(e);
            }
        } else {
            from = (new File(templatesFolder.getFile())).toPath();
        }
        Path to = sReport.getReportFolder();
        copyNonTeplateFiles(from, to);
        createSuites();
        createIndexPage();
    }

}
