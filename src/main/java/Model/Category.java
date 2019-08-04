package Model;

public enum Category {

    WORK("WORK"), HOUSEHOLD("HOUSEHOLD"), PRIVATE("PRIVATE");

    private String categoryName;

    Category(String categoryName){
        this.categoryName = categoryName;
    }


}
