package kr.co.uniess.kto.batch.service;

import java.util.HashMap;

public abstract class AbstractBatchService implements BatchService {

    private HashMap<String, Object> parameters = new HashMap<>();

    @Override
    public void addParameter(String name, Object value) {
        parameters.put(name, value);
    }

    @Override
    public Object getParameter(String name) {
        return parameters.get(name);
    }
}