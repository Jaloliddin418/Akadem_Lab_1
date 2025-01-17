import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class Student extends Person implements Serializable {
    private final int grade;
    private List<String> subjects;
    private boolean isSenior;
    private final List<Teacher> assignedTeachers;
    private Map<String, Integer> grades;

    public Student(String firstName, String lastName, LocalDate dateOfBirth, List<String> subjects) {
        super(firstName, lastName, dateOfBirth);
        this.grade = calculateGrade(dateOfBirth);
        this.subjects = subjects;
        this.assignedTeachers = new ArrayList<>();
        this.grades = new HashMap<>();

        assignRandomGrades();
    }


    private int calculateGrade(LocalDate dateOfBirth) {
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 7) {
            throw new IllegalArgumentException("Ученик слишком мал для школы!");
        }
        if (age > 18) {
            throw new IllegalArgumentException("Ученик слишком стар для школы!");
        }
        return age - 6;
    }


    private void assignRandomGrades() {
        Random random = new Random();
        for (String subject : subjects) {
            int randomGrade = random.nextInt(4) + 2; // Случайная оценка от 2 до 5
            grades.put(subject, randomGrade);
        }
    }

    public int getGrade() {
        return grade;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public Map<String, Integer> getGrades() {
        return grades;
    }



    public boolean isEligible() {
        return calculateAge() <= 18;
    }

    public List<Teacher> getAssignedTeachers() {
        return assignedTeachers;
    }

    public void addAssignedTeacher(Teacher teacher) {
        this.assignedTeachers.add(teacher);
    }

    public void addGrade(String subject, int grade) {
        grades.put(subject, grade);
    }

    @Override
    public String toString() {
        return "Student{" +
                "Фамилия = " + super.getLastName() +
                ", Имя = " + super.getFirstName() +
                ", Класс = " + grade +
                ", Предметы = " + String.join(", ", subjects) +
                ", Оценки = " + grades.toString() +
                '}';
    }
}
