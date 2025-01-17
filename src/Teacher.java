import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Teacher extends Person implements Serializable {
    private List<String> subjects;
    private boolean isSenior;
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);
    private final int id;

    public Teacher(String firstName, String lastName, LocalDate dateOfBirth, List<String> subjects, boolean isSenior) {
        super(firstName, lastName, dateOfBirth);
        this.id = ID_GENERATOR.getAndIncrement();
        this.subjects = subjects;
        this.isSenior = isSenior;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public int getId() {
        return id;
    }



    public boolean isSenior() {
        return isSenior;
    }

    public boolean isEligible() {
        int age = calculateAge();
        return age >= 27 && age <= 70;
    }


    public boolean isEligibleFor(Student student) {
        List<String> requiredSubjects = getSubjectsByGrade(student.getGrade());
        return this.subjects.containsAll(requiredSubjects) && this.isSenior == (student.getGrade() >= 7);
    }


    private List<String> getSubjectsByGrade(int grade) {
        List<String> subjects = new ArrayList<>();
        subjects.add("Математика");
        subjects.add("Русский язык");
        subjects.add("Науки о природе");
        if (grade >= 7) {
            subjects.add("Физика");
            subjects.add("Химия");
            subjects.add("Информатика");
            subjects.add("Биология");
        }
        return subjects;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "Фамилия = " + super.getLastName() +
                " Имя = " + super.getFirstName() +
                " subjects = " + subjects +
                '}';
    }
}
