package com.gmail.tarkhanov.lev.reflection.service;

import com.gmail.tarkhanov.lev.reflection.config.Settings;
import com.gmail.tarkhanov.lev.reflection.config.annotations.CustomDateFormatField;
import com.gmail.tarkhanov.lev.reflection.config.annotations.CustomJsonFieldName;
import com.gmail.tarkhanov.lev.reflection.dto.Person;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Lev Tarkhanov on 29-Jul-17.
 */

public class JsonConverter {

    private static Map<String, String> fieldsWithCustomJson = new HashMap<>();
    private static Map<String, String> fieldsWithCustomDate = new HashMap<>();
    private static Map<String, String> fieldsToJson = new HashMap<>();
    private static Map<String, String> fieldsFromJson = new HashMap<>();


    public static String objectToJson(Object objectToConvert) {

        Class<?> objectClass = objectToConvert.getClass();

        for (Field field : objectClass.getDeclaredFields()) {
            extractField(objectToConvert, field);
        }

        return createJsonData();

    }


    private static void extractField(Object objectToConvert, Field field) {

        field.setAccessible(true);
        try {
            if (field.get(objectToConvert) != null) {
                String fieldName = field.getName();
                String fieldValue = "";
                if (field.isAnnotationPresent(CustomJsonFieldName.class)) {
                    fieldName = getFieldNameFromAnno(field.getAnnotations());
                }
                if (field.getType() == LocalDate.class) {
                    if (field.isAnnotationPresent(CustomDateFormatField.class)) {
                        fieldValue = getFormattedDate((LocalDate) field.get(objectToConvert), getDateFormatFromAnno(field.getAnnotations()));
                    } else {
                        fieldValue = getFormattedDate((LocalDate) field.get(objectToConvert), Settings.DEFAULT_DATE_FORMAT);
                    }
                } else {
                    fieldValue = field.get(objectToConvert).toString();
                }
                fieldsToJson.put(fieldName, fieldValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        field.setAccessible(false);

    }


    private static String getFieldNameFromAnno(Annotation[] annotations) {

        String fieldName = "";
        for (Annotation anno : annotations) {
            if (anno instanceof CustomJsonFieldName)
                fieldName = ((CustomJsonFieldName) anno).jsonFieldName();
        }
        return fieldName;

    }

    private static String getDateFormatFromAnno(Annotation[] annotations) {

        String dateFormat = "";
        for (Annotation anno : annotations) {
            if (anno instanceof CustomDateFormatField)
                dateFormat = ((CustomDateFormatField) anno).dateFormat();
        }
        return dateFormat;

    }


    private static String getFormattedDate(LocalDate date, String dateFormat) {

        String formattedDate = dateFormat;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        formattedDate = date.format(formatter);
        return formattedDate;

    }


    private static String createJsonData() {

        StringBuilder json = new StringBuilder();
        json.append("{");
        for (Map.Entry<String, String> entry : fieldsToJson.entrySet())
        {
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        json.delete(json.length() - 1, json.length());
        json.append("}");
        return json.toString();

    }


    public static Object objectFromJson(String json, Class<?> objectClass) {

        Object objectFromJson = createObjectFromClassName(objectClass);
        parseJson(json);
        extractFieldsAnnotations(objectClass);
        setPersonFields((Person) objectFromJson);

        return objectFromJson;

    }


    // It seems something wrong here
    private static Object createObjectFromClassName(Class<?> clazz) {

        Object createdObject = null;

        //Class[] parameters = new Class[] {String.class, String.class, String.class, String.class, LocalDate.class, LocalDate.class};
        Constructor constructor = null;
        try {
            //constructor = clazz.getConstructor(parameters);
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            //createdObject = constructor.newInstance("Gregory", "House", "Misanthropy", "Nuclear Physics", LocalDate.of(1950, 10, 28), null);
            if (constructor != null) {
                createdObject = constructor.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return createdObject;

    }


    private static void parseJson(String json) {

        String jsonEntries[] = json.substring(1, json.length() - 1).split(",");
        for (String entry : jsonEntries) {
            String entryData[] = entry.substring(1, entry.length() - 1).split("\":\"");
            fieldsFromJson.put(entryData[0], entryData[1]);
        }

    }


    private static void extractFieldsAnnotations(Class<?> className) {

        for (Field field : className.getDeclaredFields()) {
            String fieldName = field.getName();
            Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
            if (fieldAnnotations != null) {
                for (Annotation anno : fieldAnnotations) {
                    if (anno instanceof CustomJsonFieldName) {
                        fieldsWithCustomJson.put(fieldName, ((CustomJsonFieldName) anno).jsonFieldName());
                    }
                    if (anno instanceof CustomDateFormatField) {
                        fieldsWithCustomDate.put(fieldName, ((CustomDateFormatField) anno).dateFormat());
                    }
                }
            }
        }

    }


    private static void setPersonFields(Person person) {

        Class<?> clazz = person.getClass();

        for (Field field : clazz.getDeclaredFields()) {

            String jsonFieldIndex;
            String fieldName = field.getName();

            field.setAccessible(true);

            jsonFieldIndex = fieldsWithCustomJson.getOrDefault(fieldName, fieldName);

            if (field.getType() == String.class) {
                try {
                    field.set(person, fieldsFromJson.get(jsonFieldIndex));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.getType() == LocalDate.class) {
                String dateFormat = Settings.DEFAULT_DATE_FORMAT;
                if (fieldsWithCustomDate.containsKey(fieldName)) {
                    dateFormat = fieldsWithCustomDate.get(fieldName);
                }
                if (fieldsFromJson.containsKey(jsonFieldIndex)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
                    LocalDate dateValue = LocalDate.parse(fieldsFromJson.get(jsonFieldIndex), formatter);
                    try {
                        field.set(person, dateValue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            field.setAccessible(false);
        }

    }

}
