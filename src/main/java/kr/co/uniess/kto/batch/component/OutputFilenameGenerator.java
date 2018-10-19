package kr.co.uniess.kto.batch.component;

public interface OutputFilenameGenerator {
    String generateName();

    String generateNameBy(String input);
}