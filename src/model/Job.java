package model;

public class Job {
    private int id;
    private String jobTitle;
    private String company;
    private String skillsRequired;
    private double minCgpa;
    private int experienceRequired;
    private String deadline;

    public Job(int id, String jobTitle, String company, String skillsRequired,
               double minCgpa, int experienceRequired, String deadline) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.company = company;
        this.skillsRequired = skillsRequired;
        this.minCgpa = minCgpa;
        this.experienceRequired = experienceRequired;
        this.deadline = deadline;
    }

    public int getId() { return id; }
    public String getJobTitle() { return jobTitle; }
    public String getCompany() { return company; }
    public String getSkillsRequired() {
        return skillsRequired;
    }
}
