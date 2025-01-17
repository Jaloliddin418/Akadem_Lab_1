import java.util.List;

public class ClassLevel {
    private int gradeLevel;
    private List<String> subjects;

    public ClassLevel(int gradeLevel, List<String> subjects) {
        this.gradeLevel = gradeLevel;
        this.subjects = subjects;
    }

    public int getGradeLevel() {
        return gradeLevel;
    }

    public List<String> getSubjects() {
        return subjects;
    }
}
