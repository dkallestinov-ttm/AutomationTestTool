package Requests;

import java.util.Date;

public class SubmitBatchPayload {

    private String submittedBy;
    private Date scheduledDate;
    private String name;
    private String[] dsns;
    private Profile profile;

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String[] getDsns() {
        return dsns;
    }

    public void setDsns(String[] dsns) {
        this.dsns = dsns;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }


    public SubmitBatchPayload(String submittedBy, Date scheduledDate, String[] dsns, int profileId, String name) {
        this.submittedBy = submittedBy;
        this.scheduledDate = scheduledDate;
        this.dsns = dsns;
        this.profile = new Profile(profileId);
        this.name = name;
    }

    public SubmitBatchPayload(String submittedBy, Date scheduledDate, String[] dsns, Profile profile, String name) {
        this.submittedBy = submittedBy;
        this.scheduledDate = scheduledDate;
        this.dsns = dsns;
        this.profile = profile;
        this.name = name;
    }

}
