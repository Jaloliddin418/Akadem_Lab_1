import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SchoolSystemConsoleApp {


    private static final Scanner scanner = new Scanner(System.in);
    private static SchoolSystem schoolSystem;
    private static final String STUDENT_FILE_PATH = "student_data.csv";
    private static final String TEACHER_FILE_PATH = "teacher_data.csv";
  //  private static final String ASSIGN_FILE_PATH = "assign_data.csv";


    public static void main(String[] args) {

        schoolSystem = new SchoolSystem();
        loadData();
        removeIneligibleStudents();
        ensureTeachersHaveStudents();


        boolean running = true;
        while (running) {
            System.out.println("\nСистема учета школы");
            System.out.println("1. Добавить ученика");
            System.out.println("2. Добавить учителя");
            System.out.println("3. Назначить учителей ученикам");
            System.out.println("4. Показать учителей");
            System.out.println("5. Показать учеников");
            System.out.println("6. Показать назначенных учителей");
            System.out.println("7. Экспорт оценок класса");
            System.out.println("8. Выход");
            System.out.print("Выберите пункт меню: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addStudent();
                    break;
                case "2":
                    addTeacher();
                    break;
                case "3":
                    assignTeachersToStudent();
                    break;
                case "4":
                    showTeacher();
                    break;
                case "5":
                    showStudent();
                    break;
                case "6":
                    showAssignTeacherToStudent();
                    break;
                case "7":
                    exportClassGrades();
                    break;
                case "8":
                    saveData();
                    running = false;
                    break;
                default:
                    System.out.println("Неверный ввод. Попробуйте снова.");
            }
        }
    }


    private static void addStudent() {
        System.out.print("Введите имя ученика: ");
        String firstName = scanner.nextLine();
        System.out.print("Введите фамилию ученика: ");
        String lastName = scanner.nextLine();
        LocalDate dateOfBirth = getDateInput("Введите дату рождения ученика (День-месяц-год): ");

        try {
            List<String> subjects = getSubjectsForGrade(new Student(firstName, lastName, dateOfBirth, new ArrayList<>()).getGrade());
            Student student = new Student(firstName, lastName, dateOfBirth, subjects);
            schoolSystem.addStudent(student);
            schoolSystem.assignTeachersToNewStudent(student);
            System.out.println("Ученик успешно добавлен: " + student);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }



    private static void addTeacher() {
        System.out.print("Введите имя учителя: ");
        String firstName = scanner.nextLine();
        System.out.print("Введите фамилию учителя: ");
        String lastName = scanner.nextLine();
        LocalDate dateOfBirth = getDateInput("Введите дату рождения учителя (День-Месяц-Год): ");
        System.out.print("Учитель преподает для старших классов? (да/нет): ");
        String level = scanner.nextLine().trim().toLowerCase();

        List<String> subjects;
        boolean isSenior;
        if (level.equals("да")) {
            subjects = Arrays.asList("Математика", "Русский язык", "Науки о природе", "Физика", "Химия", "Информатика", "Биология");
            isSenior = true;
        } else if (level.equals("нет")) {
            subjects = Arrays.asList("Математика", "Русский язык", "Науки о природе");
            isSenior = false;
        } else {
            System.out.println("Неверный ввод. Учитель не добавлен.");
            return;
        }

        Teacher teacher = new Teacher(firstName, lastName, dateOfBirth, subjects, isSenior);
        schoolSystem.addTeacher(teacher);

    }

    private static void assignTeachersToStudent() {
        System.out.print("Введите Фамилию и Имя ученика для назначения учителей: ");
        String fullName = scanner.nextLine();
        for (Student student : schoolSystem.getStudents()) {
            if (student.getFullName().equals(fullName)) {
                schoolSystem.assignTeachersToStudent(student);
                return;
            }
        }
        System.out.println("Ученик не найден.");
    }

    private static void showTeacher() {
        schoolSystem.showTeacher();
    }

    private static void showStudent() {
        schoolSystem.showStudent();
    }

    private static void showAssignTeacherToStudent() {
        System.out.print("Введите Фамилию и Имя ученика для просмотра назначенных учителей: ");
        String fullName = scanner.nextLine();
        for (Student student : schoolSystem.getStudents()) {
            if (student.getFullName().equals(fullName)) {
                schoolSystem.showAssignTeacherToStudent(student);
                return;
            }
        }
        System.out.println("Ученик не найден.");
    }



    private static void saveData() {
        SchoolSystemCSV csvHandler = new SchoolSystemCSV();
        csvHandler.saveStudentsToCSV(schoolSystem.getStudents(), STUDENT_FILE_PATH);
        csvHandler.saveTeachersToCSV(schoolSystem.getTeachers(), TEACHER_FILE_PATH);
       // csvHandler.saveAssignmentsToCSV(schoolSystem.getAssignments(), ASSIGN_FILE_PATH);
    }

    private static void loadData() {
        SchoolSystemCSV csvHandler = new SchoolSystemCSV();
        List<Student> students = csvHandler.loadStudentsFromCSV(STUDENT_FILE_PATH);
        if (students != null) {
            schoolSystem.setStudents(students);
        }

        List<Teacher> teachers = csvHandler.loadTeachersFromCSV(TEACHER_FILE_PATH);
        if (teachers != null) {
            schoolSystem.setTeachers(teachers);
        }
    }


    private static LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (Exception e) {
                System.out.println("Пожалуйста, введите дату в формате День-Месяц-Год (например, 01-01-2020).");
            }
        }
    }


    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите правильное число.");
            }
        }
    }


    private static void removeIneligibleStudents() {
        schoolSystem.removeStudentsAboveAgeLimit();
        System.out.println("Удалены ученики старше 18 лет.");
    }


    private static void exportClassGrades() {
        int grade = getIntInput("Введите класс для экспорта оценок: ");
        String filePath = "grades_class_" + grade + ".csv";
        schoolSystem.exportClassGradesToFile(grade, filePath);
    }


    private static void ensureTeachersHaveStudents() {
        schoolSystem.ensureTeachersHaveStudents();
        System.out.println("Обновление назначений: каждый учитель имеет хотя бы одного ученика.");
    }


    private static List<String> getSubjectsForGrade(int grade) {
        if (grade < 7) {
            return Arrays.asList("Математика", "Русский язык", "Науки о природе");
        } else if (grade < 12) {
            return Arrays.asList("Математика", "Русский язык", "Науки о природе", "Физика", "Химия", "Информатика", "Биология");
        }
        return null;
    }
}
