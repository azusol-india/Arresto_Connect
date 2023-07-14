package app.com.arresto.arresto_connect.data.models;

import java.util.ArrayList;

public class EC_report_Model {
    String id;
    String project_id;
    String project_name;
    String revision_no;
    String jobcard_no;
    ArrayList<Data> line_reports;

    public boolean isChildrenVisible() {
        return childrenVisible;
    }

    public void setChildrenVisible(boolean childrenVisible) {
        this.childrenVisible = childrenVisible;
    }

    public String getRevision_no() {
        return revision_no;
    }

    public void setRevision_no(String revision_no) {
        this.revision_no = revision_no;
    }

    public String getJobcard_no() {
        return jobcard_no;
    }

    public void setJobcard_no(String jobcard_no) {
        this.jobcard_no = jobcard_no;
    }

    private boolean childrenVisible;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public ArrayList<Data> getLine_reports() {
        return line_reports;
    }

    public void setLine_reports(ArrayList<Data> line_reports) {
        this.line_reports = line_reports;
    }


    public class Data {
        String report_file, remark;

        public String getReport_file() {
            return report_file;
        }

        public void setReport_file(String report_file) {
            this.report_file = report_file;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
