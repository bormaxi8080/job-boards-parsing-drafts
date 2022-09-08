package ru.airlabs.ego.model;

public class Experience {

    private String companyName;
    private String experiencePosition;
    private String experienceDescription;
    private String addressLocality;
    private String experienceTimeinterval;
    private String dates;

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getExperienceTimeinterval() {
        return experienceTimeinterval;
    }

    public void setExperienceTimeinterval(String experienceTimeinterval) {
        this.experienceTimeinterval = experienceTimeinterval;
    }

    public String getAddressLocality() {
        return addressLocality;
    }

    public void setAddressLocality(String addressLocality) {
        this.addressLocality = addressLocality;
    }

    public String getExperienceDescription() {
        return experienceDescription;
    }

    public void setExperienceDescription(String experienceDescription) {
        this.experienceDescription = experienceDescription;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getExperiencePosition() {
        return experiencePosition;
    }

    public void setExperiencePosition(String experiencePosition) {
        this.experiencePosition = experiencePosition;
    }
}
