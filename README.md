
# 🎓 Student Placement Management System

A complete **Java-based Placement Management System** with GUI, database integration, AI-powered resume generation, and advanced analytics.

This system simulates a real-world college placement portal where students can apply for jobs and admins can manage the entire recruitment process.

---

# 🚀 PROJECT OBJECTIVE

Manual placement management is inefficient and hard to scale.

This project solves that by:
- Digitizing student and job data
- Automating job matching
- Providing AI-powered resume generation
- Enabling analytics and reporting

---

# 🏗 SYSTEM ARCHITECTURE

User (GUI - JavaFX)
        ↓
Java Application Logic
        ↓
JDBC (Database Connection)
        ↓
MySQL Database
        ↓
Matching + AI + Email Services
        ↓
Output (UI / PDF / Email)

---

# 🛠 TECHNOLOGIES USED

| Technology | Purpose |
|----------|--------|
| Java (JDK 21) | Core development |
| JavaFX | GUI |
| MySQL | Database |
| JDBC | Database connectivity |
| OpenAI API | AI Resume generation |
| Jakarta Mail API | Email notifications |
| iText PDF | PDF export |
| Git & GitHub | Version control |

---

# ✨ FEATURES

---

## 👨‍🎓 STUDENT FEATURES

### 🔍 1. View Jobs
- View all available job postings
- Search by:
  - Job title
  - Company
  - Skills

---

### 📝 2. Apply for Jobs
- Select job from table
- Automatically calculates match score
- Stores application in database

---

### 🎯 3. Job Recommendations
- Displays jobs sorted by match score
- Shows:
  - Match %
  - Skill gap

---

### 💾 4. Bookmark Jobs
- Save jobs for later
- View saved jobs in separate section

---

### 📄 5. Resume Upload & Preview
- Upload resume (PDF only)
- Stored in database
- Can open anytime

---

### 🤖 6. AI Resume Builder
- Generates resume based on:
  - Student profile
  - Selected job
- Auto-fill from database
- Download as PDF

---

### 📊 7. My Applications
- View:
  - Applied jobs
  - Match score
  - Status (Applied / Selected / Rejected)

---

### 📥 8. Export Data
- Download:
  - PDF
  - CSV

---

---

## 🧑‍💼 ADMIN FEATURES

---

### ➕ 1. Add Jobs
- Add job roles with:
  - Skills
  - Company
  - CGPA requirement
  - Experience
  - Deadline

---

### 📋 2. Manage Applications
- View all applications
- Update status:
  - Selected
  - Rejected

---

### 📧 3. Bulk Email System
- Send emails automatically:
  - Selected → Congratulations email
  - Rejected → Update email

---

### 📊 4. Placement Analytics (Advanced)

Displays:

- Total Students
- Total Jobs
- Total Applications
- Placement Rate %
- Top Companies
- Top Skills in Demand

---

---

## 🧠 ADVANCED FEATURES

- AI Resume Generation (OpenAI API)
- Smart Matching Algorithm
- Skill Gap Analysis
- Bulk Email Automation
- Export (PDF & CSV)
- Navigation system (Back + Home)
- Role-based system (Admin / Student)

---

# 🗄 DATABASE DESIGN

---

## 📌 Students Table

- id
- name
- skills
- branch
- experience
- email
- resume

---

## 📌 Jobs Table

- id
- job_title
- company
- skills_required
- min_cgpa
- experience_required
- deadline

---

## 📌 Applications Table

- student_id
- job_id
- match_score
- status

---

---

# ⚙️ SETUP INSTRUCTIONS

---

## 🔹 STEP 1: Clone Repository

```bash
git clone https://github.com/Palak1317/Student-Placement-System.git
````

---

## 🔹 STEP 2: Setup Database

1. Open MySQL
2. Create database:

```sql
CREATE DATABASE placement_db;
```

3. Create tables (students, jobs, applications)

---

## 🔹 STEP 3: Configure Database in Code

Update:

```
DBConnection.java
```

```java
jdbc:mysql://localhost:3306/placement_db
```

---

## 🔹 STEP 4: Setup Environment Variables

### Windows:

```bash
setx EMAIL_APP_PASSWORD "your_password"
setx OPENAI_API_KEY "your_api_key"
```

Restart IntelliJ after this.

---

## 🔹 STEP 5: Add Required Libraries

Add these JAR files:

* MySQL Connector
* JavaFX SDK
* Jakarta Mail
* iText PDF

---

## 🔹 STEP 6: Run Project

Run:

```
MainApp.java
```

---

# 🧭 HOW TO USE (STEP-BY-STEP)

---

## 👨‍🎓 STUDENT FLOW

1. Register/Login
2. Go to Dashboard
3. View Jobs
4. Apply for jobs
5. Check Recommendations
6. Upload Resume
7. Generate AI Resume
8. Track Applications
9. Download reports

---

## 🧑‍💼 ADMIN FLOW

1. Login as Admin
2. Add Job Listings
3. View Applications
4. Update status
5. Send bulk emails
6. Check analytics dashboard

---

# 📸 OUTPUT

Example:

Student: Rahul
Skills: Java, SQL

Recommended Jobs:

* Backend Developer – 90%
* Java Developer – 85%

---

# 🔒 SECURITY PRACTICES

* No API keys stored in code
* Uses environment variables
* Sensitive data protected

---

# 🚀 FUTURE ENHANCEMENTS

* Chat system (Student ↔ Recruiter)
* Resume parser
* Online deployment
* Mobile app version

---

# 👨‍💻 AUTHOR

**Palak Patel**
