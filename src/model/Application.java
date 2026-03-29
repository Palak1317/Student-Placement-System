package model;

public class Application {
    private int id;
    private int studentId;
    private int jobId;
    private double matchScore;
    private String status;

    public Application(int id, int studentId, int jobId,
                       double matchScore, String status) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.matchScore = matchScore;
        this.status = status;
    }
}