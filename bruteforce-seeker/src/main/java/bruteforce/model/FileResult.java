package bruteforce.model;

public class FileResult {
    private String path;
    private double score;

    public FileResult() {}

    public FileResult(String path, double score) {
        this.path = path;
        this.score = score;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}