package com.gmail.tarkhanov.lev.reflection.dto;

import com.gmail.tarkhanov.lev.reflection.config.Settings;
import com.gmail.tarkhanov.lev.reflection.config.annotations.CustomDateFormatField;
import com.gmail.tarkhanov.lev.reflection.config.annotations.CustomJsonFieldName;
import java.time.LocalDate;

/**
 * Created by Lev Tarkhanov on 29-Jul-17.
 */

public class Person {

    private String firstName;
    private String lastName;
    @CustomJsonFieldName(jsonFieldName = "FavSport")
    private String sport;
    private String hobby;
    @CustomJsonFieldName(jsonFieldName = "Date of birth")
    @CustomDateFormatField(dateFormat = Settings.DATE_FORMAT)
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;


    public Person() {

    }


    public Person(String firstName, String lastName, String sport, String hobby, LocalDate dateOfBirth, LocalDate dateOfDeath) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.sport = sport;
        this.hobby = hobby;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;

    }


    @Override
    public String toString() {

        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", sport='" + sport + '\'' +
                ", hobby='" + hobby + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", dateOfDeath=" + dateOfDeath +
                '}';

    }

}
