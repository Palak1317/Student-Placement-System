package model;

public class Student {
    private int id;
    private String name;
    private String email;
    private String branch;
    private String skills;
    private int experience;
    private double cgpa;
    private String resume;

    public Student(int id, String name, String email, String branch,
                   String skills, int experience, double cgpa, String resume) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.branch = branch;
        this.skills = skills;
        this.experience = experience;
        this.cgpa = cgpa;
        this.resume = resume;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getBranch() { return branch; }
    public String getSkills() { return skills; }
    public int getExperience() { return experience; }
    public double getCgpa() { return cgpa; }
    public String getResume() { return resume; }
}