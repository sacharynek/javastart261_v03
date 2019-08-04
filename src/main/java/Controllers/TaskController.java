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
                    System.out.println(ConsoleColors.CYAN + "podaj ID zadania" + ConsoleColors.RESET);
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
        scanner.next();
        String newDescription = scanner.nextLine();
        newDescription = newDescription.equals("") ? null : newDescription;
        temporaryTask.setDescription(newDescription);

        System.out.println("podaj kategorię.  Wciśnij Enter aby zostawić jak jest");

        for (Category category : Category.values()) {
            System.out.println(category);
        }

        String newCategory = scanner.nextLine();
        Category category = newCategory.equals("") ? null : Category.valueOf(newCategory);
        temporaryTask.setCategory(category);

        System.out.println("Czy zadanie jest gotowe? true/false?");
        boolean isReady = scanner.nextLine().toLowerCase().equals("true");
        temporaryTask.setReady(isReady);

        System.out.println("podaj datę rozpoczęcia wykonywania zadania?. RRRR-MM-DD - Wciśnij Enter aby zostawić jak jest");
        String startDateString = scanner.nextLine();
        LocalDate startDate = startDateString.equals("") ? null : LocalDate.parse(startDateString);
        temporaryTask.setStartDate(startDate);

        System.out.println("podaj datę zakończenia wykonywania zadania?. RRRR-MM-DD - Wciśnij Enter aby zostawić jak jest");
        String finishDateString = scanner.nextLine();
        LocalDate finishDate = finishDateString.equals("") ? null : LocalDate.parse(finishDateString);
        temporaryTask.setFinishDate(finishDate);
        return temporaryTask;
    }

    public Task displayMarkAsDoneText() {
        Task temporaryTask = new Task();

        temporaryTask.setReady(true);

        return temporaryTask;
    }


    private void updateTask(Long id, Task temporaryTask) {
        entityManager.getTransaction().begin();
        Task task = entityManager.find(Task.class, id);
        if(temporaryTask.getDescription() != null)  task.setDescription(temporaryTask.getDescription());

        if(temporaryTask.getCategory() != null) task.setCategory(temporaryTask.getCategory());

        task.setReady(temporaryTask.isReady());


        if(temporaryTask.getStartDate() != null) task.setStartDate(temporaryTask.getStartDate());
        if(temporaryTask.getFinishDate() != null) task.setFinishDate(temporaryTask.getFinishDate());

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

        System.out.println("Po czym sortować? O - opis, K - kategoria, D- Deadline");
        String userInput = scanner.nextLine();

        String orderBy = "";

        if (userInput.equals("O")) {
            orderBy = "t.description";
        } else if (userInput.equals("K")) {
            orderBy = "t.category";
        }else if(userInput.equals("D")){
            orderBy = "t.deadline";
        } else {
            System.out.println("Nieprawidłowa wartość");
            return;
        }

        orderBy = " ORDER BY " + orderBy;

        handleDisplayAll(orderBy, false);
    }


    private void handleDisplayAll(String sortBy, boolean isReady) {
        TypedQuery<Task> query = entityManager.createQuery("select t FROM Task t where t.isReady = " + isReady + sortBy, Task.class);
        List<Task> resultList = query.getResultList();
        String fontColor = ConsoleColors.RESET;

        for (Task task : resultList) {
            if(task.getDeadline().isBefore(LocalDate.now())){
                fontColor = ConsoleColors.RED;
            } else{
                fontColor = ConsoleColors.GREEN;
            }


            System.out.println(fontColor+task+ConsoleColors.RESET);
        }

    }

}
