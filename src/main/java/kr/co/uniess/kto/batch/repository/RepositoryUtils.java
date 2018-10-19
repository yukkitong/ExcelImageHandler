package kr.co.uniess.kto.batch.repository;

import java.util.UUID;

public class RepositoryUtils {

    private RepositoryUtils() {

    }

    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    public static String generateId(String name) {
        return UUID.fromString(name).toString();
    }
}