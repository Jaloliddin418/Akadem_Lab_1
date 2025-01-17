import java.io.*;
import java.util.*;

public class SchoolSystem implements Serializable {
    private List<Student> students = new ArrayList<>();
    private List<Teacher> teachers = new ArrayList<>();
    private Map<Student, List<Teacher>> assignments = new HashMap<>();


    public void addStudent(Student student) {
        if (student.isEligible()) {
            students.add(student);
            System.out.println("Ученик добавлен: " + student.getFullName());
        } else {
            System.out.println("Ученик старше 18 лет и не может быть добавлен.");
        }
    }


    public void addTeacher(Teacher teacher) {
        if (teacher.isEligible()) {
            teachers.add(teacher);
            System.out.println("Учитель добавлен: " + teacher.getFullName());
        } else {
            System.out.println("Учитель не подходит по возрасту.");
        }
    }


    public void showTeacher() {
        if (teachers.isEmpty()) {
            System.out.println("Учителей пока нет.");
        } else {
            teachers.forEach(System.out::println);
        }
    }


    public void ensureTeachersHaveStudents() {
        for (Teacher teacher : teachers) {
            boolean hasStudents = assignments.values().stream()
                    .anyMatch(studentList -> studentList.contains(teacher));

            if (!hasStudents) {
                for (Student student : students) {
                    if (teacher.isEligibleFor(student)) {
                        assignTeacherToStudent(teacher, student);
                        break;
                    }
                }
            }
        }
    }


    private void assignTeacherToStudent(Teacher teacher, Student student) {
        List<Teacher> assignedTeachers = assignments.getOrDefault(student, new ArrayList<>());
        if (!assignedTeachers.contains(teacher)) {
            assignedTeachers.add(teacher);
            assignments.put(student, assignedTeachers);
        }
    }


    public void removeStudentsAboveAgeLimit() {
        students.removeIf(student -> {
            if (!student.isEligible()) {
                System.out.println("Ученик исключен из школы: " + student.getFullName());
                assignments.remove(student); // Удаляем связанные данные
                return true;
            }
            return false;
        });
    }


    public void exportClassGradesToFile(int grade, String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            // Заголовок файла
            writer.println("Фамилия,Имя,Класс,Предметы,Оценки");

            for (Student student : students) {
                if (student.getGrade() == grade) {
                    // Преобразование оценок в строку формата "Предмет=Оценка"
                    String gradesAsString = student.getGrades().entrySet()
                            .stream()
                            .map(entry -> entry.getKey() + "=" + entry.getValue())
                            .reduce((a, b) -> a + "; " + b)
                            .orElse("");

                    // Запись данных студента в файл
                    writer.printf("%s, %s, %d, %s, %s%n",
                            student.getLastName(),
                            student.getFirstName(),
                            student.getGrade(),
                            String.join(", ", student.getSubjects()),
                            gradesAsString);
                }
            }

            System.out.println("Данные успешно экспортированы в файл: " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }



    public void assignTeachersToNewStudent(Student student) {
        List<Teacher> assignedTeachers = new ArrayList<>();

        for (String subject : student.getSubjects()) {
            for (Teacher teacher : teachers) {
                if (teacher.getSubjects().contains(subject) && teacher.isEligibleFor(student)) {
                    if (!assignedTeachers.contains(teacher)) {
                        assignedTeachers.add(teacher);
                    }
                }
            }
        }

        assignments.put(student, assignedTeachers);

        System.out.println("Ученику " + student.getFullName() + " назначены учителя: ");
        for (Teacher teacher : assignedTeachers) {
            System.out.println(" - " + teacher.getFullName());
        }
    }


    public void showStudent() {
        if (students.isEmpty()) {
            System.out.println("Ученики пока не добавлены.");
        } else {
            students.forEach(System.out::println);
        }
    }


    public Map<Student, List<Teacher>> getAssignments() {
        return assignments;
    }


    public void setAssignments(Map<Student, List<Teacher>> assignments) {
        this.assignments = assignments;
    }


    public void showAssignTeacherToStudent(Student student) {
        List<Teacher> assignedTeachers = assignments.get(student);

        if (assignedTeachers == null || assignedTeachers.isEmpty()) {
            System.out.println("Учителя не назначены для ученика: " + student.getFullName());
        } else {
            System.out.println("Назначенные учителя для ученика " + student.getFullName() + ":");
            for (Teacher teacher : assignedTeachers) {
                System.out.println(" - " + teacher.getLastName() + " " + teacher.getFirstName() +
                        " (Предметы: " + String.join(", ", teacher.getSubjects()) + ")");
            }
        }
    }



    public void assignTeachersToStudent(Student student) {
        List<Teacher> assignedTeachers = assignments.getOrDefault(student, new ArrayList<>());

        for (String subject : student.getSubjects()) {
            for (Teacher teacher : teachers) {
                if (!assignedTeachers.contains(teacher) &&
                        teacher.getSubjects().contains(subject) &&
                        teacher.isEligibleFor(student)) {
                    assignedTeachers.add(teacher);
                    break;
                }
            }
        }

        assignments.put(student, assignedTeachers);

        if (assignedTeachers.isEmpty()) {
            System.out.println("Нет подходящих учителей для ученика: " + student.getFullName());
        } else {
            System.out.println("Учителя успешно назначены для ученика: " + student.getFullName());
        }
    }



    public List<Student> getStudents() {
        return students;
    }


    public void setStudents(List<Student> students) {
        this.students = students;
    }


    public List<Teacher> getTeachers() {
        return teachers;
    }


    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }


    public void saveData(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
            System.out.println("Данные успешно сохранены в файл: " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }


    public static SchoolSystem loadData(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (SchoolSystem) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Файл данных не найден. Создана новая система.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке данных: " + e.getMessage());
        }
        return new SchoolSystem();
    }
}
