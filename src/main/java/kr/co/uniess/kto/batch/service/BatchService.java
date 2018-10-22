package kr.co.uniess.kto.batch.service;

public interface BatchService {
    void addParameter(String name, Object value);
    Object getParameter(String name);
    void execute();
}