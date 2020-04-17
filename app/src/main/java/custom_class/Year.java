package custom_class;

public enum Year {
    YEAR_1("Y1"), YEAR_2("Y2"), YEAR_3("Y3"), YEAR_4("Y4");

    private Year(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    private String year;
}
