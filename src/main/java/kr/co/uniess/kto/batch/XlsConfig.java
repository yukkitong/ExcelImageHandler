package kr.co.uniess.kto.batch;

public class XlsConfig {

    private String[] sheetNameArray;
    private int[] sheetIndexArray;

    private final FindMainStrategy defaultFindMainStrategy = new FindMainStrategy() {
        @Override
        public boolean isMain(String value) {
            return "O".equalsIgnoreCase(value);
        }
    };

    private FindMainStrategy strategy = defaultFindMainStrategy;

    private XlsConfig(Builder builder) {
        this.sheetNameArray = builder.sheetNameArray;
        this.sheetIndexArray = builder.sheetIndexArray;
    }

    public FindMainStrategy getFindMainStrategy() {
        return strategy;
    }

    public void setFindMainStrategy(FindMainStrategy strategy) {
        this.strategy = strategy;
    }

    public String[] getSheetNames() {
        return sheetNameArray;
    }

    public int[] getSheetIndexes() {
        return sheetIndexArray;
    }

    public interface FindMainStrategy {
        boolean isMain(String value);
    }

    public static class Builder {
        String[] sheetNameArray;
        int[] sheetIndexArray;

        public Builder sheetNames(String... name) {
            sheetNameArray = name;
            return this;
        }

        public Builder sheetIndexes(int... index) {
            sheetIndexArray = index;
            return this;
        }

        public XlsConfig build() {
            return new XlsConfig(this);
        }

        public static Builder getNullBuilder() {
            return new Builder();
        }
    }
}