package models;

public class PortalScrapeConfig {

    private final String jobCardSelector;
    private final String companySelector;
    private final String titleSelector;
    private final String locationSelector;
    private final String postedDateSelector;
    private final String applyLinkSelector;

    public PortalScrapeConfig(
            String jobCardSelector,
            String companySelector,
            String titleSelector,
            String locationSelector,
            String postedDateSelector,
            String applyLinkSelector) {

        this.jobCardSelector = jobCardSelector;
        this.companySelector = companySelector;
        this.titleSelector = titleSelector;
        this.locationSelector = locationSelector;
        this.postedDateSelector = postedDateSelector;
        this.applyLinkSelector = applyLinkSelector;
    }

    public String getJobCardSelector() {
        return jobCardSelector;
    }

    public String getCompanySelector() {
        return companySelector;
    }

    public String getTitleSelector() {
        return titleSelector;
    }

    public String getLocationSelector() {
        return locationSelector;
    }

    public String getPostedDateSelector() {
        return postedDateSelector;
    }

    public String getApplyLinkSelector() {
        return applyLinkSelector;
    }
}