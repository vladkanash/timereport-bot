package org.vladkanash.jira.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Issue {

    @SerializedName("key")
    private String code;

    private Fields fields;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public static class Fields {

        private Worklog worklog;


        public Worklog getWorklog() {
            return worklog;
        }

        public void setWorklog(Worklog worklog) {
            this.worklog = worklog;
        }

        public static class Worklog {

            private List<org.vladkanash.jira.entity.Worklog> worklogs;

            private int maxResults;

            public List<org.vladkanash.jira.entity.Worklog> getWorklogs() {
                return worklogs;
            }

            public void setWorklogs(List<org.vladkanash.jira.entity.Worklog> worklogs) {
                this.worklogs = worklogs;
            }

            public int getMaxResults() {
                return maxResults;
            }

            public void setMaxResults(int maxResults) {
                this.maxResults = maxResults;
            }

            @Override
            public String toString() {
                return "Worklog{" +
                        "worklogs=" + worklogs +
                        ", maxResults=" + maxResults +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Field{" +
                    "worklog=" + worklog +
                    '}';
        }
    }
}
