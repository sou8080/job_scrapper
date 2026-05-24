package models;

public class JobModel {

    // ==========================================
    // FIELDS
    // ==========================================

    private String company;
    private String role;
    private String portal;
    private String location;
    private String postedDate;
    private String applyLink;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public JobModel(

            String company,
            String role,
            String portal,
            String location,
            String postedDate,
            String applyLink) {

        this.company = company;
        this.role = role;
        this.portal = portal;
        this.location = location;
        this.postedDate = postedDate;
        this.applyLink = applyLink;
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public String getPortal() {
        return portal;
    }

    public String getLocation() {
        return location;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getApplyLink() {
        return applyLink;
    }

    // ==========================================
    // UNIQUE KEY
    // ==========================================

    public String getUniqueKey() {

        String companyKey = company == null ? "" : company.trim().toLowerCase();
        String roleKey = role == null ? "" : role.trim().toLowerCase();
        return companyKey + "_" + roleKey;
    }

    // ==========================================
    // FORMATTED OUTPUT
    // ==========================================

    @Override
    public String toString() {

        return "Company: " + company + "\n" + "Role: " + role + "\n" + "Portal: " + portal + "\n" + "Location: "
                + location + "\n" + "Posted: " + postedDate + "\n" + "Job URL: " + applyLink + "\n";
    }
}