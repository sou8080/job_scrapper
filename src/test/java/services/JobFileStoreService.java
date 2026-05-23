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
                        // NULL CHECK
                        // ==========================================

                        if (jobs == null) {

                                System.out.println(
                                                "Job list is null.");

                                return;
                        }

                        // ==========================================
                        // TOTAL JOBS RECEIVED
                        // ==========================================

                        System.out.println(

                                        "Jobs received for saving : "
                                                        + jobs.size());

                        // ==========================================
                        // CREATE OUTPUT DIRECTORY
                        // ==========================================

                        File outputDir = new File("output");

                        if (!outputDir.exists()) {

                                boolean folderCreated = outputDir.mkdirs();

                                System.out.println(

                                                "Output folder created : "
                                                                + folderCreated);
                        }

                        // ==========================================
                        // OUTPUT FILE
                        // ==========================================

                        File outputFile =

                                        new File(
                                                        outputDir,
                                                        "latest_jobs.txt");

                        // ==========================================
                        // DELETE OLD FILE
                        // ==========================================

                        if (outputFile.exists()) {

                                boolean deleted = outputFile.delete();

                                System.out.println(

                                                "Old report deleted : "
                                                                + deleted);
                        }

                        // ==========================================
                        // CREATE NEW FILE
                        // ==========================================

                        boolean created = outputFile.createNewFile();

                        System.out.println(

                                        "New report file created : "
                                                        + created);

                        // ==========================================
                        // FILE WRITER
                        // ==========================================

                        FileWriter writer = new FileWriter(outputFile);

                        // ==========================================
                        // TOTAL UNIQUE JOBS
                        // ==========================================

                        writer.write(

                                        "====================================\n"

                                                        + "TOTAL UNIQUE JOBS : "

                                                        + jobs.size()

                                                        + "\n"

                                                        + "====================================\n\n");

                        // ==========================================
                        // WRITE JOBS
                        // ==========================================

                        int count = 1;

                        for (JobModel job : jobs) {

                                writer.write(

                                                "JOB : "
                                                                + count++
                                                                + "\n\n");

                                writer.write(
                                                job.toString());

                                writer.write(

                                                "\n"
                                                                +
                                                                "===================================="
                                                                +
                                                                "\n\n");
                        }

                        // ==========================================
                        // FLUSH
                        // ==========================================

                        writer.flush();

                        // ==========================================
                        // CLOSE WRITER
                        // ==========================================

                        writer.close();

                        // ==========================================
                        // SUCCESS LOG
                        // ==========================================

                        System.out.println(

                                        "Jobs saved successfully.");

                        System.out.println(

                                        "Saved file path : "

                                                        + outputFile.getAbsolutePath());

                        System.out.println(

                                        "Total unique jobs saved : "

                                                        + jobs.size());

                } catch (Exception e) {

                        System.out.println(
                                        "Failed to save jobs.");

                        e.printStackTrace();
                }
        }
}