package kr.co.uniess.kto.batch.component;

import org.springframework.stereotype.Component;

@Component
public class CsvOutputFilenameGenerator implements OutputFilenameGenerator {

    private static final String SUFFIX = ".csv";

    @Override
    public String generateName() {
        return "no-name" + SUFFIX;
    }

    @Override
    public String generateNameBy(String input) {
        int indexOfPeriod = input.lastIndexOf('.');
        return input.substring(0, indexOfPeriod) + SUFFIX;
    }
}