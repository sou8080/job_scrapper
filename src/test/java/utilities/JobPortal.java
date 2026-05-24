package utilities;

public enum JobPortal {

    LINKEDIN("linkedin"),
    INDEED("indeed"),
    NAUKRI("naukri"),
    SHINE("shine"),
    FOUNDIT("foundit"),
    TIMESJOBS("timesjobs");

    private final String portalName;

    JobPortal(String portalName) {
        this.portalName = portalName;
    }

    public String getPortalName() {
        return portalName;
    }
}