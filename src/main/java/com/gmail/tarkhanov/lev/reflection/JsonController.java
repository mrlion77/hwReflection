package com.gmail.tarkhanov.lev.reflection;

import com.gmail.tarkhanov.lev.reflection.dto.Person;
import com.gmail.tarkhanov.lev.reflection.service.JsonConverter;
import java.time.LocalDate;

/**
 * Created by Lev Tarkhanov on 29-Jul-17.
 */

public class JsonController {

    public static void main(String[] args) {

        Person personToJson = new Person("Gregory", "House", "Misanthropy", "Nuclear Physics", LocalDate.of(1950, 10, 28), null);
        String jsonResult = JsonConverter.objectToJson(personToJson);
        System.out.println("toJSON: " + jsonResult);

        Person personFromJson = (Person) JsonConverter.objectFromJson(jsonResult, Person.class);
        System.out.println("fromJSON: " + personFromJson);

    }

}
