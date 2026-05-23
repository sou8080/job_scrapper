package testcases;

import base.Test_Base_Class;

import com.microsoft.playwright.Browser;

import config.SearchConfig;

import factory.PortalScrapeConfigFactory;

import models.PortalScrapeConfig;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import pom.Foundit_POM;
import pom.Indeed_POM;
import pom.LinkedIn_POM;
import pom.Naukri_POM;
import pom.Shine_POM;

import services.CommonJobScraperService;
import services.JobCollectorService;
import services.JobFileStoreService;

import utilities.JobPortal;

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

                // ==========================================
                // CONTEXT
                // ==========================================

                context = browser.newContext(
                                new Browser.NewContextOptions()
                                                .setViewportSize(1440, 900));

                context.clearPermissions();

                page = context.newPage();

                // ==========================================
                // POM
                // ==========================================

                LinkedIn_POM linkedIn = new LinkedIn_POM(page);

                // ==========================================
                // CONFIG
                // ==========================================

                PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                JobPortal.LINKEDIN);

                // ==========================================
                // OPEN
                // ==========================================

                linkedIn.goToLinkedIn();

                // ==========================================
                // SEARCH
                // ==========================================

                linkedIn.searchJobs(
                                SearchConfig.OR_BASED_KEYWORDS,
                                SearchConfig.LOCATION);

                // ==========================================
                // ASSERT
                // ==========================================

                Assert.assertTrue(
                                linkedIn.hasResults(),
                                "LinkedIn jobs not found.");

                // ==========================================
                // SCRAPE
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

                System.out.println(
                                "LinkedIn jobs collected : "
                                                + linkedIn.getJobCount());
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

                context = browser.newContext(
                                new Browser.NewContextOptions()
                                                .setViewportSize(1440, 900));

                context.clearPermissions();

                page = context.newPage();

                Naukri_POM naukri = new Naukri_POM(page);

                PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                JobPortal.NAUKRI);

                naukri.goToNaukri();

                naukri.searchJobs(
                                SearchConfig.COMMA_SEPARATED_KEYWORDS,
                                SearchConfig.LOCATION);

                Assert.assertTrue(
                                naukri.hasResults(),
                                "Naukri jobs not found.");

                CommonJobScraperService.scrapeJobs(
                                page,
                                "Naukri",

                                config.getJobCardSelector(),

                                config.getCompanySelector(),

                                config.getTitleSelector(),

                                config.getLocationSelector(),

                                config.getPostedDateSelector(),

                                config.getApplyLinkSelector());

                System.out.println(
                                "Naukri jobs collected : "
                                                + naukri.getJobCount());
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

                context = browser.newContext(
                                new Browser.NewContextOptions()
                                                .setViewportSize(1440, 900));

                context.clearPermissions();

                page = context.newPage();

                Indeed_POM indeed = new Indeed_POM(page);

                PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                JobPortal.INDEED);

                indeed.goToIndeed();

                indeed.searchJobs(
                                SearchConfig.COMMA_SEPARATED_KEYWORDS,
                                SearchConfig.LOCATION);

                Assert.assertTrue(
                                indeed.hasResults(),
                                "Indeed jobs not found.");

                CommonJobScraperService.scrapeJobs(
                                page,
                                "Indeed",

                                config.getJobCardSelector(),

                                config.getCompanySelector(),

                                config.getTitleSelector(),

                                config.getLocationSelector(),

                                config.getPostedDateSelector(),

                                config.getApplyLinkSelector());

                System.out.println(
                                "Indeed jobs collected : "
                                                + indeed.getJobCount());
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

                context = browser.newContext(
                                new Browser.NewContextOptions()
                                                .setViewportSize(1440, 900));

                context.clearPermissions();

                page = context.newPage();

                Shine_POM shine = new Shine_POM(page);

                PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                JobPortal.SHINE);

                shine.goToShine();

                shine.searchJobs(
                                SearchConfig.OR_BASED_KEYWORDS,
                                SearchConfig.LOCATION);

                Assert.assertTrue(
                                shine.hasResults(),
                                "Shine jobs not found.");

                CommonJobScraperService.scrapeJobs(
                                page,
                                "Shine",

                                config.getJobCardSelector(),

                                config.getCompanySelector(),

                                config.getTitleSelector(),

                                config.getLocationSelector(),

                                config.getPostedDateSelector(),

                                config.getApplyLinkSelector());

                System.out.println(
                                "Shine jobs collected : "
                                                + shine.getJobCount());
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

                context = browser.newContext(
                                new Browser.NewContextOptions()
                                                .setViewportSize(1440, 900));

                context.clearPermissions();

                page = context.newPage();

                Foundit_POM foundit = new Foundit_POM(page);

                PortalScrapeConfig config = PortalScrapeConfigFactory.getConfig(
                                JobPortal.FOUNDIT);

                foundit.goToFoundit();

                foundit.searchJobs(
                                SearchConfig.COMMA_SEPARATED_KEYWORDS,
                                SearchConfig.LOCATION);

                Assert.assertTrue(
                                foundit.hasResults(),
                                "Foundit jobs not found.");

                CommonJobScraperService.scrapeJobs(
                                page,
                                "Foundit",

                                config.getJobCardSelector(),

                                config.getCompanySelector(),

                                config.getTitleSelector(),

                                config.getLocationSelector(),

                                config.getPostedDateSelector(),

                                config.getApplyLinkSelector());

                System.out.println(
                                "Foundit jobs collected : "
                                                + foundit.getJobCount());
        }

        // ==========================================
        // SAVE ALL JOBS
        // ==========================================

        @AfterSuite(alwaysRun = true)
        public void saveAllJobs() {

                JobFileStoreService.saveJobs(
                                JobCollectorService.getAllJobs());

                System.out.println(
                                "Total unique jobs collected : "
                                                + JobCollectorService.getTotalJobs());

                // ==========================================
                // CLOSE BROWSER
                // ==========================================

                closeBrowser();
        }
}