# 🚀 Job Scraper Suite

A scalable multi-job portal scraping framework built using **Java, Playwright, Maven, and TestNG**.

The framework automates job searching across:

- LinkedIn
- Naukri
- Indeed
- Shine
- Foundit

It supports:

✅ Multi-portal scraping  
✅ Session-based login handling  
✅ Dynamic keyword search  
✅ Smart date filtering  
✅ Retry & fallback mechanisms  
✅ Infinite scroll support  
✅ Headless browser execution  

---

# 🏗️ Framework Architecture

- **POM Architecture** → Clean portal-wise structure
- **Factory Pattern** → Dynamic selector/config handling
- **Service Layer** → Reusable scraping & filtering logic
- **Config-Driven Execution** → Easy customization

---

# 📂 Project Structure

```text
job_scrapper/
│
├── output/
│   └── latest_jobs.txt
│
├── src/
│
│   ├── test/
│   │
│   ├── java/
│   │
│   │   ├── base/
│   │   │   └── Test_Base_Class.java
│   │
│   │   ├── config/
│   │   │   ├── ConfigLoader.java
│   │   │   ├── FrameworkConfig.java
│   │   │   ├── PortalConfig.java
│   │   │   └── SearchConfig.java
│   │
│   │   ├── factory/
│   │   │   └── PortalScrapeConfigFactory.java
│   │   │
│   │   ├── models/
│   │   │   ├── JobModel.java
│   │   │   └── PortalScrapeConfig.java
│   │   │
│   │   ├── pom/
│   │   │   ├── Linkedin_POM.java
│   │   │   ├── Naukri_POM.java
│   │   │   ├── Indeed_POM.java
│   │   │   ├── Shine_POM.java
│   │   │   └── Foundit_POM.java
│   │   │
│   │   ├── services/
│   │   │   ├── CommonJobScraperService.java
│   │   │   ├── JobCollectorService.java
│   │   │   ├── JobFilterService.java
│   │   │   └── JobFilterService.java
│   │   │
│   │   ├── testcases/
│   │   │   └── Job_Search_Test.java
│   │   │
│   │   └── utilities/
│   │       ├── BrowserManager.java
│   │       ├── JobPortal.java
│   │       └── ScreenshotUtils.java
│   │
│   └── resources/
│       ├── config.properties
│       ├── testng.xml
│       └── Screenshots/
│          ├── foundit_ss/
│          ├── indeed_ss/
│          ├── linkedin_ss/
│          ├── naukri_ss/
│          └── shine_ss/
│
├── target/
│
├── pom.xml
├── README.md
└── .gitignore

```

# ⚙️ Tech Stack

| Technology | Usage |
|---|---|
| Java | Core Development |
| Playwright | Browser Automation |
| Maven | Dependency Management |
| TestNG | Test Execution |
| POM | Framework Structure |

---

# 🔧 Prerequisites

- Java 17+
- Maven 3.8+
- Internet Connection

---

# 📥 Setup

## Install Dependencies

```bash
mvn clean install
```

## Install Playwright Browsers

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

---

# ▶️ Run Framework

## Execute Complete Test Suite

```bash
mvn test -Dsurefire.suiteXmlFiles=testng.xml
```

---

# ⚙️ Configuration

Update search configs inside:

```text
src/main/resources/config.properties
```

Example:

```properties
search.keywords=QA Automation,SDET,Business Analyst
search.location=Mumbai
date.filter.days=7
headless=true
```

---

# 🧠 Smart Features

- Session persistence & auto re-login
- Dynamic selector fallback handling
- Retry-based search execution
- Intelligent date filtering (7/14/21/28 days)
- Infinite scroll handling
- Portal-wise modular structure

---

# 📄 Output

Scraped jobs are stored in:

```text
output/latest_jobs.txt
```

---

# 👨‍💻 Author

### Soumik Nandi

Automation Engineer | Playwright | Selenium | AI Testing

---