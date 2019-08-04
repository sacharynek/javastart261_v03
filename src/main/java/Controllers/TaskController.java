package Controllers;

import Model.Category;
import Model.Task;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class TaskController {


    private Scanner scanner;
    private EntityManager entityManager;

    public void run() {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("db");
        entityManager = entityManagerFactory.createEntityManager();

        addTestData();

        while (true) {
            System.out.println("Co chcesz zrobić?");
            System.out.println("1. Dodać produkt"); //done
            System.out.println("2. Wyświetlić wszystkie zadania do zrobienia");//done
            System.out.println("3. Wyświelić posortowane");//done
            System.out.println("4. Oznacz jako zrobione");//done
            System.out.println("5. Edytuj zadanie");//
            System.out.println("6. Wyświetl Archiwum");//done
            System.out.println("0. Koniec");//done

            scanner = new Scanner(System.in);

            String userInput = scanner.nextLine();

            switch (userInput) {
                case "1":
                    handleInserting();
                    break;
                case "2":
                    handleDisplayAll("", false);
                    break;
                case "3":
                    handleDisplayAllSorted();
                    break;
                case "4":
                    System.out.println(ConsoleColors.CYAN + "podaj ID zadania" + ConsoleColors.Reset);
                    updateTask(scanner.nextLong(), displayMarkAsDoneText());
                    break;
                case "5":
                    System.out.println(ConsoleColors.CYAN + "podaj ID zadania" + ConsoleColors.RESET);
                    updateTask(scanner.nextLong(), displayUpdateText());
                    break;
                case "6":
                    handleDisplayAll("", true);
                    break;
                case "0":
                    entityManager.close();
                    return;
                default:
                    System.out.println("Nieprawidłowy wybór!");
            }
        }
    }


    public Task displayUpdateText() {
        Task temporaryTask = new Task();

        System.out.println("podaj nowy opis. Wciśnij Enter aby zostawić jak jest");
        String newDescription = scanner.next();
        newDescription = newDescription.equals("") ? temporaryTask.getDescription() : scanner.nextLine();
        temporaryTask.setDescription(newDescription);

        System.out.println("podaj kategorię.  Wciśnij Enter aby zostawić jak jest");
        String newCategory = scanner.next();
        Category category = newCategory.equals("") ? temporaryTask.getCategory() : Category.valueOf(scanner.nextLine());
        temporaryTask.setCategory(category);

        System.out.println("Czy zadanie jest gotowe? true/false?");
        boolean isReady = scanner.nextBoolean();
        temporaryTask.setReady(isReady);

        System.out.println("podaj datę rozpoczęcia wykonywania zadania?. Wciśnij Enter aby zostawić jak jest");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        temporaryTask.setStartDate(startDate);

        System.out.println("podaj datę zakończenia wykonywania zadania?. Wciśnij Enter aby zostawić jak jest");
        LocalDate finishDate = LocalDate.parse(scanner.nextLine());
        temporaryTask.setFinishDate(finishDate);

        return temporaryTask;
    }

    public Task displayMarkAsDoneText() {
        Task temporaryTask = new Task();

        System.out.println("Czy zadanie jest gotowe? true/false?");
        boolean isReady = scanner.nextBoolean();
        temporaryTask.setReady(isReady);

        return temporaryTask;
    }


    private void updateTask(Long id, Task temporaryTask) {
        entityManager.getTransaction().begin();
        Task task = entityManager.find(Task.class, id);

        task.setDescription(temporaryTask.getDescription());
        task.setCategory(temporaryTask.getCategory());
        task.setReady(temporaryTask.isReady());
        task.setStartDate(temporaryTask.getStartDate());
        task.setFinishDate(temporaryTask.getFinishDate());

        entityManager.persist(task);
        entityManager.getTransaction().commit();
    }


    private void addTestData() {
        entityManager.getTransaction().begin();
        entityManager.persist(new Task("Kup Masło", Category.HOUSEHOLD, false, LocalDate.of(2019, 8, 26)));
        Task task = new Task("Idź do okulisty", Category.PRIVATE, true, LocalDate.of(2019, 6, 28));
        task.setStartDate(LocalDate.of(2019, 6, 5));
        task.setFinishDate(LocalDate.of(2019, 6, 25));
        entityManager.persist(task);
        entityManager.persist(new Task("Wyślij Raport", Category.WORK, false, LocalDate.of(2019, 10, 28)));
        entityManager.getTransaction().commit();
    }

    private void handleInserting() {
        System.out.println("Podaj Opis");
        String description = scanner.nextLine();
        System.out.println("Podaj Kategorię");

        for (Category value : Category.values()) {
            System.out.println(value);
        }
        System.out.println(Category.values());
        String category = scanner.nextLine();

        System.out.println("podaj deadline w formacie RRRR-MM-DD");
        LocalDate ld = LocalDate.parse(scanner.nextLine());

        Task task = new Task();
        task.setDescription(description);
        task.setCategory(Category.valueOf(category));
        task.setDeadline(ld);

        entityManager.getTransaction().begin();
        entityManager.persist(task);
        entityManager.getTransaction().commit();
    }

    private void handleDisplayAllSorted() {

        System.out.println("Po czym sortować? N - opis, C - kategoria");
        String userInput = scanner.nextLine();

        String orderBy = "";

        if (userInput.equals("N")) {
            orderBy = "t.description";
        } else if (userInput.equals("C")) {
            orderBy = "t.category";
        } else {
            System.out.println("Nieprawidłowa wartość");
            return;
        }

        orderBy = " ORDER BY " + orderBy;

        handleDisplayAll(orderBy, false);
    }


    private void handleDisplayAll(String sortBy, boolean isReady) {
        TypedQuery<Task> query = entityManager.createQuery("select t FROM Task t where t.isReady = false " + sortBy, Task.class);
        List<Task> resultList = query.getResultList();

        for (Task task : resultList) {
            System.out.println(task);
        }

    }

}
