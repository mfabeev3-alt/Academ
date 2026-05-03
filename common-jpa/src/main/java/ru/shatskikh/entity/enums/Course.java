package ru.shatskikh.entity.enums;

public enum Course {

    FIRST(1, "1-й курс"),
    SECOND(2,"2-й курс"),
    THIRD(3, "3-й курс"),
    FOURTH(4, "4-й курс");

    private final int value;
    private final String description;

    Course(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {return value;}
    public String getDescription() {return description;}

    public static Course fromValue(int value){

        for (Course course: values()) {

            if(course.value == value)
                return course;
        }
        throw new IllegalArgumentException("Неверный курс: " + value);
    }
}
