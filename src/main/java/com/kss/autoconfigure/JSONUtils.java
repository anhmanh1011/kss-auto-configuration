package com.kss.autoconfigure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kss.autoconfigure.common.SpringContext;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public class JSONUtils {

    public static JSONObject toJsonObject(String str) {
        JSONObject jsonObj = new JSONObject(str);
        return jsonObj;
    }


    public static JSONArray toJsonArray(String str) {
        JSONArray jsonArr = new JSONArray(str);
        return jsonArr;
    }


    public static List toList(JSONArray jsonArray) {
        return IntStream.range(0, jsonArray.length()).mapToObj(i -> jsonArray.get(i)).collect(Collectors.toList());
    }

    public static String toJsonString(Object obj) {
        ObjectMapper mapper = getObjectMapper();

        //Object to JSON in String
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(String.valueOf(e));
        }
        return jsonString;
    }

    public static <T> T toObject(String jsonString, Class<T> clazz) {
        //JSON from String to Object
        ObjectMapper mapper = SpringContext.getBean(ObjectMapper.class);
        T obj = null;
        try {
            obj = mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
        return obj;
    }

    public static <T> List<T> toListOfObjects(String jsonString, Class<T[]> clazz) {
        //JSON from String to Object
        ObjectMapper mapper = SpringContext.getBean(ObjectMapper.class);
        T obj = null;
        List<T> listOfObjects = new ArrayList<>();
        try {
            T[] objects = mapper.readValue(jsonString, clazz);
            listOfObjects = Arrays.asList(objects);
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
        return listOfObjects;
    }

    public static boolean isValid(final String json) {
        ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        boolean valid;
        try {
            objectMapper.readTree(json);
            valid = true;
        } catch (IOException e) {
            valid = false;
            log.error(String.valueOf(e));
        }
        return valid;
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
