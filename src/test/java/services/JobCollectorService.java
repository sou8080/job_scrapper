package services;

import models.JobModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JobCollectorService {

    // ==========================================
    // UNIQUE JOB STORAGE
    // ==========================================

    private static final Map<String, JobModel> JOBS_MAP = new LinkedHashMap<>();

    // ==========================================
    // ADD JOB
    // ==========================================

    public static void addJob(JobModel job) {

        String uniqueKey = job.getUniqueKey();

        // ==========================================
        // DUPLICATE CHECK
        // ==========================================

        if (!JOBS_MAP.containsKey(uniqueKey)) {
            JOBS_MAP.put(uniqueKey, job);
            System.out.println("Added new job : " + job.getCompany() + " - " + job.getRole());
        } else {
            System.out.println("Duplicate skipped : " + job.getCompany() + " - " + job.getRole());
        }
    }

    // ==========================================
    // GET ALL JOBS
    // ==========================================

    public static List<JobModel> getAllJobs() {

        return new ArrayList<>(JOBS_MAP.values());
    }

    // ==========================================
    // TOTAL COUNT
    // ==========================================

    public static int getTotalJobs() {

        return JOBS_MAP.size();
    }

    // ==========================================
    // CLEAR JOBS
    // ==========================================

    public static void clearJobs() {

        JOBS_MAP.clear();
    }
}