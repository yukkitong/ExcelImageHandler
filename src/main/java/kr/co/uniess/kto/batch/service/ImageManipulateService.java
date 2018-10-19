package kr.co.uniess.kto.batch.service;

import java.util.List;

public interface ImageManipulateService<T> {
  void execute(List<T> list);
}