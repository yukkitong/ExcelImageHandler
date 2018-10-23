package kr.co.uniess.kto.batch.controller;

public interface IController<T> {
    void run(T data) throws Exception;
}