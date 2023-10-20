package com.example.healthappelderly;
/*enum allergies {
    NUTS,
    GLUTEN,
    LACTOS
}*/



enum mealType {
    breakfast,
    lunch,
    dinner,
    snack
}

public class Meal {
    private mealType type;
    private String time_of_day;
    private String date;
    private String comment;
    //private ArrayList<allergies> allergy_list;
    public Meal() {}
    public Meal(mealType type, String time_of_day, String date, String comment /*, Collection<allergies> allergy_list*/) {
        this.type = type;
        this.time_of_day = time_of_day;
        this.date = date;
        this.comment = comment;

        //this.allergy_list = new ArrayList<allergies>();
        //allergy_list.addAll(allergy_list);

    }

    public mealType getType() {
        return type;
    }
    public void setType(mealType type) {
        this.type = type;
    }

    public String getTime_of_day() {
        return time_of_day;
    }

    public void setTime_of_day(String time_of_day) {
        this.time_of_day = time_of_day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
