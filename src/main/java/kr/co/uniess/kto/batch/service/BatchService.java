package kr.co.uniess.kto.batch.service;

public interface BatchService<T> {
    void execute(T data);
}