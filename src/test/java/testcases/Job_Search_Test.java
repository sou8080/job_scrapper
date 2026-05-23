package testcases;

import base.Test_Base_Class;

import config.SearchConfig;

import factory.PortalScrapeConfigFactory;

import models.PortalScrapeConfig;

import org.testng.Assert;
import org.testng.annotations.Test;

import pom.Foundit_POM;
import pom.Indeed_POM;
import pom.LinkedIn_POM;
import pom.Naukri_POM;
import pom.Shine_POM;

import services.CommonJobScraperService;
import services.JobCollectorService;

import utilities.JobPortal;
import utilities.TelegramNotifier;

@Test(singleThreaded = true)
public class Job_Search_Test extends Test_Base_Class {

        // ==========================================
        // LINKEDIN JOB SEARCH
        // ==========================================

        @Test(groups = {
                        "linkedin",
                        "jobs",
                        "regression"
        })
        public void linkedInJobSearchTest() {

                try {

                        TelegramNotifier.sendMessage(
                                        "🌐 Opening LinkedIn");

                        LinkedIn_POM linkedIn = new LinkedIn_POM(page);

                        PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                        JobPortal.LINKEDIN);

                        linkedIn.goToLinkedIn();

                        TelegramNotifier.sendMessage(
                                        "🔍 Searching jobs on LinkedIn");

                        linkedIn.searchJobs(
                                        SearchConfig.OR_BASED_KEYWORDS,
                                        SearchConfig.LOCATION);

                        Assert.assertTrue(
                                        linkedIn.hasResults(),
                                        "LinkedIn jobs not found.");

                        // ==========================================
                        // BEFORE COUNT
                        // ==========================================

                        int beforeCount = JobCollectorService.getTotalJobs();

                        // ==========================================
                        // SCRAPE JOBS
                        // ==========================================

                        CommonJobScraperService.scrapeJobs(

                                        page,
                                        "LinkedIn",

                                        config.getJobCardSelector(),

                                        config.getCompanySelector(),

                                        config.getTitleSelector(),

                                        config.getLocationSelector(),

                                        config.getPostedDateSelector(),

                                        config.getApplyLinkSelector());

                        // ==========================================
                        // AFTER COUNT
                        // ==========================================

                        int afterCount = JobCollectorService.getTotalJobs();

                        // ==========================================
                        // PORTAL UNIQUE JOB COUNT
                        // ==========================================

                        int portalStoredJobs = afterCount - beforeCount;

                        TelegramNotifier.sendMessage(

                                        "✅ LinkedIn scraping completed\n"
                                                        + "📊 Unique Jobs Stored : "
                                                        + portalStoredJobs);

                } catch (Throwable e) {

                        TelegramNotifier.sendMessage(

                                        "❌ LinkedIn scraping failed\n\n"
                                                        + "Reason : "
                                                        + e.getMessage());

                        throw e;
                }
        }

        // ==========================================
        // NAUKRI JOB SEARCH
        // ==========================================

        @Test(groups = {
                        "naukri",
                        "jobs",
                        "regression"
        })
        public void naukriJobSearchTest() {

                try {

                        TelegramNotifier.sendMessage(
                                        "🌐 Opening Naukri");

                        Naukri_POM naukri = new Naukri_POM(page);

                        PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                        JobPortal.NAUKRI);

                        naukri.goToNaukri();

                        TelegramNotifier.sendMessage(
                                        "🔍 Searching jobs on Naukri");

                        naukri.searchJobs(
                                        SearchConfig.COMMA_SEPARATED_KEYWORDS,
                                        SearchConfig.LOCATION);

                        Assert.assertTrue(
                                        naukri.hasResults(),
                                        "Naukri jobs not found.");

                        int beforeCount = JobCollectorService.getTotalJobs();

                        CommonJobScraperService.scrapeJobs(

                                        page,
                                        "Naukri",

                                        config.getJobCardSelector(),

                                        config.getCompanySelector(),

                                        config.getTitleSelector(),

                                        config.getLocationSelector(),

                                        config.getPostedDateSelector(),

                                        config.getApplyLinkSelector());

                        int afterCount = JobCollectorService.getTotalJobs();

                        int portalStoredJobs = afterCount - beforeCount;

                        TelegramNotifier.sendMessage(

                                        "✅ Naukri scraping completed\n"
                                                        + "📊 Unique Jobs Stored : "
                                                        + portalStoredJobs);

                } catch (Throwable e) {

                        TelegramNotifier.sendMessage(

                                        "❌ Naukri scraping failed\n\n"
                                                        + "Reason : "
                                                        + e.getMessage());

                        throw e;
                }
        }

        // ==========================================
        // INDEED JOB SEARCH
        // ==========================================

        @Test(groups = {
                        "indeed",
                        "jobs",
                        "regression"
        })
        public void indeedJobSearchTest() {

                try {

                        TelegramNotifier.sendMessage(
                                        "🌐 Opening Indeed");

                        Indeed_POM indeed = new Indeed_POM(page);

                        PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                        JobPortal.INDEED);

                        indeed.goToIndeed();

                        TelegramNotifier.sendMessage(
                                        "🔍 Searching jobs on Indeed");

                        indeed.searchJobs(
                                        SearchConfig.COMMA_SEPARATED_KEYWORDS,
                                        SearchConfig.LOCATION);

                        Assert.assertTrue(
                                        indeed.hasResults(),
                                        "Indeed jobs not found.");

                        int beforeCount = JobCollectorService.getTotalJobs();

                        CommonJobScraperService.scrapeJobs(

                                        page,
                                        "Indeed",

                                        config.getJobCardSelector(),

                                        config.getCompanySelector(),

                                        config.getTitleSelector(),

                                        config.getLocationSelector(),

                                        config.getPostedDateSelector(),

                                        config.getApplyLinkSelector());

                        int afterCount = JobCollectorService.getTotalJobs();

                        int portalStoredJobs = afterCount - beforeCount;

                        TelegramNotifier.sendMessage(

                                        "✅ Indeed scraping completed\n"
                                                        + "📊 Unique Jobs Stored : "
                                                        + portalStoredJobs);

                } catch (Throwable e) {

                        TelegramNotifier.sendMessage(

                                        "❌ Indeed scraping failed\n\n"
                                                        + "Reason : "
                                                        + e.getMessage());

                        throw e;
                }
        }

        // ==========================================
        // SHINE JOB SEARCH
        // ==========================================

        @Test(groups = {
                        "shine",
                        "jobs",
                        "regression"
        })
        public void shineJobSearchTest() {

                try {

                        TelegramNotifier.sendMessage(
                                        "🌐 Opening Shine");

                        Shine_POM shine = new Shine_POM(page);

                        PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                        JobPortal.SHINE);

                        shine.goToShine();

                        TelegramNotifier.sendMessage(
                                        "🔍 Searching jobs on Shine");

                        shine.searchJobs(
                                        SearchConfig.OR_BASED_KEYWORDS,
                                        SearchConfig.LOCATION);

                        Assert.assertTrue(
                                        shine.hasResults(),
                                        "Shine jobs not found.");

                        int beforeCount = JobCollectorService.getTotalJobs();

                        CommonJobScraperService.scrapeJobs(

                                        page,
                                        "Shine",

                                        config.getJobCardSelector(),

                                        config.getCompanySelector(),

                                        config.getTitleSelector(),

                                        config.getLocationSelector(),

                                        config.getPostedDateSelector(),

                                        config.getApplyLinkSelector());

                        int afterCount = JobCollectorService.getTotalJobs();

                        int portalStoredJobs = afterCount - beforeCount;

                        TelegramNotifier.sendMessage(

                                        "✅ Shine scraping completed\n"
                                                        + "📊 Unique Jobs Stored : "
                                                        + portalStoredJobs);

                } catch (Throwable e) {

                        TelegramNotifier.sendMessage(

                                        "❌ Shine scraping failed\n\n"
                                                        + "Reason : "
                                                        + e.getMessage());

                        throw e;
                }
        }

        // ==========================================
        // FOUNDIT JOB SEARCH
        // ==========================================

        @Test(groups = {
                        "foundit",
                        "jobs",
                        "regression"
        })
        public void founditJobSearchTest() {

                try {

                        TelegramNotifier.sendMessage(
                                        "🌐 Opening Foundit");

                        Foundit_POM foundit = new Foundit_POM(page);

                        PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                        JobPortal.FOUNDIT);

                        foundit.goToFoundit();

                        TelegramNotifier.sendMessage(
                                        "🔍 Searching jobs on Foundit");

                        foundit.searchJobs(
                                        SearchConfig.COMMA_SEPARATED_KEYWORDS,
                                        SearchConfig.LOCATION);

                        Assert.assertTrue(
                                        foundit.hasResults(),
                                        "Foundit jobs not found.");

                        int beforeCount = JobCollectorService.getTotalJobs();

                        CommonJobScraperService.scrapeJobs(

                                        page,
                                        "Foundit",

                                        config.getJobCardSelector(),

                                        config.getCompanySelector(),

                                        config.getTitleSelector(),

                                        config.getLocationSelector(),

                                        config.getPostedDateSelector(),

                                        config.getApplyLinkSelector());

                        int afterCount = JobCollectorService.getTotalJobs();

                        int portalStoredJobs = afterCount - beforeCount;

                        TelegramNotifier.sendMessage(

                                        "✅ Foundit scraping completed\n"
                                                        + "📊 Unique Jobs Stored : "
                                                        + portalStoredJobs);

                } catch (Throwable e) {

                        TelegramNotifier.sendMessage(

                                        "❌ Foundit scraping failed\n\n"
                                                        + "Reason : "
                                                        + e.getMessage());

                        throw e;
                }
        }
}