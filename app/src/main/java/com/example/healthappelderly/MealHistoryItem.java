package com.example.healthappelderly;

public class MealHistoryItem extends Meal{
    private String time_of_report;
    private boolean ate;
    public MealHistoryItem(mealType type, String time_of_day, String date, String comment/*, Collection<allergies> allergy_list*/, String time_of_report, boolean ate) {
        super(type, time_of_day, date, comment/*, allergy_list*/);
        this.time_of_report = time_of_report;
        this.ate = ate;

    }


}
