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
                    System.out.println(ConsoleColors.CYAN+"podaj ID zadania"+ConsoleColors.RED);
                    markAsDone(scanner.nextLong());
                    break;
                case "5":

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



    private void markAsDone(Long id){


        entityManager.getTransaction().begin();
        Task task = entityManager.find(Task.class, id);
        task.setReady(true);
        entityManager.persist(task);
        entityManager.getTransaction().commit();

    }

    private void addTestData() {
        entityManager.getTransaction().begin();
        entityManager.persist(new Task("Kup Masło", Category.HOUSEHOLD, false, LocalDate.of(2019, 8,26)));
        entityManager.persist(new Task("Idź do okulisty", Category.PRIVATE, false,  LocalDate.of(2019, 6,28)));
        entityManager.persist(new Task("Wyślij Raport", Category.WORK, false,  LocalDate.of(2019, 10,28)));
        entityManager.getTransaction().commit();
    }

    private void handleInserting() {
        System.out.println("Podaj Opis");
        String description = scanner.nextLine();
        System.out.println("Podaj Kategorię");

        for(Category value : Category.values()){
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

        if(userInput.equals("N")) {
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
