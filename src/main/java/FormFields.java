public enum FormFields {

    NUMBER(0),
    YEAR(1),
    SELECTION(2),
    NAME(3),
    WEEK_OF_YEAR(4),
    DATE_FROM(5),
    DATE_TO(6),
    MONDAY_DESCRIPTION(7),
    MONDAY_HOURS(8),
    TUESDAY_DESCRIPTION(9),
    TUESDAY_HOURS(10),
    WEDNESDAY_DESCRIPTION(11),
    WEDNESDAY_HOURS(12),
    THURSDAY_DESCRIPTION(13),
    THURSDAY_HOURS(14),
    FRIDAY_DESCRIPTION(15),
    FRIDAY_HOURS(16),
    WEEK_TOTAL(17),
    SIGNATURE_DATE(18),
    SIGNATURE_LOCATION(19);

    private int value;

    public int getValue() {
        return this.value;
    }

    private FormFields(int value) {
        this.value = value;
    }
}
