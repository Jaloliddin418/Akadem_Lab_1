import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SchoolSystemCSV {


    public void saveStudentsToCSV(List<Student> students, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Student student : students) {
                writer.write(student.getLastName() + "," +
                        student.getFirstName() + "," +
                        student.getDateOfBirth() + "," +
                        student.getGrade() + "," +
                        String.join(";", student.getSubjects()) + "," +
                        student.getGrades().entrySet().stream()
                                .map(entry -> entry.getKey() + "=" + entry.getValue())
                                .collect(Collectors.joining(";")));
                writer.newLine();
            }
            System.out.println("Данные учеников успешно сохранены в файл: " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении учеников: " + e.getMessage());
        }
    }

    public void saveTeachersToCSV(List<Teacher> teachers, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("First Name,Last Name,Date of Birth,Subjects,Senior\n");
            for (Teacher teacher : teachers) {
                writer.write(teacher.getFirstName() + "," +
                        teacher.getLastName() + "," +
                        teacher.getDateOfBirth() + "," +
                        String.join(";", teacher.getSubjects()) + "," +
                        teacher.isSenior() + "\n");
            }
            System.out.println("Teacher data saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving teacher data to CSV: " + e.getMessage());
        }
    }

    public void saveAssignmentsToCSV(Map<Student, List<Teacher>> assignments, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Student Full Name,Assigned Teachers\n");
            for (Map.Entry<Student, List<Teacher>> entry : assignments.entrySet()) {
                String studentName = entry.getKey().getFullName();
                List<Teacher> assignedTeachers = entry.getValue();
                String teacherNames = assignedTeachers.stream()
                        .map(Teacher::getFullName)
                        .collect(Collectors.joining(";"));
                writer.write(studentName + "," + teacherNames + "\n");
            }
            System.out.println("Assignments data saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving assignment data to CSV: " + e.getMessage());
        }
    }

    public List<Student> loadStudentsFromCSV(String filePath) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Read the first line (header row)
            if (line != null && line.contains("Date of Birth")) {
                // If the line contains headers, skip it
                line = reader.readLine();
            }

            while (line != null) {
                String[] parts = line.split(",");
                String lastName = parts[0];
                String firstName = parts[1];
                LocalDate dateOfBirth = LocalDate.parse(parts[2]);
                List<String> subjects = Arrays.asList(parts[4].split(";"));

                // Parse grades
                Map<String, Integer> grades = new HashMap<>();
                if (parts.length > 5) {
                    String[] gradeEntries = parts[5].split(";");
                    for (String gradeEntry : gradeEntries) {
                        String[] gradeParts = gradeEntry.split("=");
                        grades.put(gradeParts[0], Integer.parseInt(gradeParts[1]));
                    }
                }

                Student student = new Student(firstName, lastName, dateOfBirth, subjects);
                for (Map.Entry<String, Integer> entry : grades.entrySet()) {
                    student.addGrade(entry.getKey(), entry.getValue());
                }

                students.add(student);
                line = reader.readLine(); // Read the next line
            }
            System.out.println("Данные учеников успешно загружены из файла: " + filePath);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке учеников: " + e.getMessage());
        }
        return students;
    }




    public List<Teacher> loadTeachersFromCSV(String filePath) {
        List<Teacher> teachers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String firstName = parts[0];
                String lastName = parts[1];
                LocalDate dateOfBirth = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                List<String> subjects = Arrays.asList(parts[3].split(";"));
                boolean isSenior = Boolean.parseBoolean(parts[4]);
                teachers.add(new Teacher(firstName, lastName, dateOfBirth, subjects, isSenior));
            }
            System.out.println("Teacher data loaded from CSV.");
        } catch (IOException e) {
            System.out.println("Error loading teacher data from CSV: " + e.getMessage());
        }
        return teachers;
    }

    public Map<Student, List<Teacher>> loadAssignmentsFromCSV(String filePath) {
        Map<Student, List<Teacher>> assignments = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 2) {
                    System.out.println("Skipping invalid row: " + line);
                    continue; // Skip rows with missing data
                }

                String studentName = parts[0];
                List<Teacher> assignedTeachers = new ArrayList<>();
                String[] teacherNames = parts[1].split(";");
                for (String teacherName : teacherNames) {
                    Teacher teacher = findTeacherByName(teacherName);
                    if (teacher != null) {
                        assignedTeachers.add(teacher);
                    }
                }
                Student student = findStudentByName(studentName);
                if (student != null) {
                    assignments.put(student, assignedTeachers);
                }
            }
            System.out.println("Assignment data loaded from CSV.");
        } catch (IOException e) {
            System.out.println("Error loading assignment data from CSV: " + e.getMessage());
        }
        return assignments;
    }


    private List<Student> students = new ArrayList<>();
    private List<Teacher> teachers = new ArrayList<>();


    private Teacher findTeacherByName(String teacherName) {
        for (Teacher teacher : teachers) {
            if (teacher.getFullName().equalsIgnoreCase(teacherName.trim())) {
                return teacher;
            }
        }
        return null; // Return null if teacher is not found
    }


    private Student findStudentByName(String studentName) {
        for (Student student : students) {
            if (student.getFullName().equalsIgnoreCase(studentName.trim())) {
                return student;
            }
        }
        return null;
    }

}
