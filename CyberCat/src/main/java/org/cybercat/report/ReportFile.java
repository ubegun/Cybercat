package org.cybercat.report;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ReportFile")
public class ReportFile {

    private String absilutePath;
    private String groupLabel;

    public ReportFile(String absilutePath, String groupLabel) {
        super();
        this.absilutePath = absilutePath;
        this.groupLabel = groupLabel;
    }

    public String getAbsilutePath() {
        return absilutePath;
    }

    public void setAbsilutePath(String absilutePath) {
        this.absilutePath = absilutePath;
    }

    public String getGroupLabel() {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    @Override
    public String toString() {
        return "ReportFile [absilutePath=" + absilutePath + ", groupLabel=" + groupLabel + "]";
    }
}
