package services;

import models.JobModel;

import java.io.File;
import java.io.FileWriter;

import java.util.List;

public class JobFileStoreService {

        public static void saveJobs(
                        List<JobModel> jobs) {

                try {

                        // ==========================================
                        // CREATE OUTPUT FOLDER
                        // ==========================================

                        File outputDir = new File("output");

                        if (!outputDir.exists()) {

                                outputDir.mkdirs();
                        }

                        // ==========================================
                        // SINGLE FIXED FILE
                        // ==========================================

                        File outputFile = new File(
                                        outputDir,
                                        "latest_jobs.txt");

                        // ==========================================
                        // DELETE OLD FILE
                        // ==========================================

                        if (outputFile.exists()) {

                                outputFile.delete();

                                System.out.println(
                                                "Old file deleted.");
                        }

                        // ==========================================
                        // CREATE NEW FILE WRITER
                        // ==========================================

                        FileWriter writer = new FileWriter(outputFile);

                        // ==========================================
                        // TOTAL COUNT
                        // ==========================================

                        writer.write(

                                        "TOTAL JOBS : "

                                                        + jobs.size()

                                                        + "\n\n");

                        // ==========================================
                        // WRITE JOBS
                        // ==========================================

                        for (JobModel job : jobs) {

                                writer.write(
                                                job.toString());

                                writer.write(
                                                "\n----------------------\n\n");
                        }

                        // ==========================================
                        // CLOSE WRITER
                        // ==========================================

                        writer.close();

                        System.out.println(

                                        "Jobs saved successfully : "

                                                        + outputFile.getAbsolutePath());

                } catch (Exception e) {

                        e.printStackTrace();
                }
        }
}