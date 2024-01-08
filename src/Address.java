import java.time.LocalDate;

public class Address {

    final private String type;
    final private String name;
    final private LocalDate startDate;
    final private LocalDate endDate;

    public Address() {
        this("", "", LocalDate.of(1970, 1, 1), LocalDate.of(1970, 1, 1));
    }

    public Address(String type, String name, LocalDate startDate, LocalDate endDate) {
        this.type = type;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Проверяет, действителен ли адрес на заданную дату
     * @param checkDate - дата проверки
     * @return - true, если адрес действителен.
     */
    public boolean isUpToDate(LocalDate checkDate) {
        boolean afterOrStart = startDate.isBefore(checkDate) || startDate.isEqual(checkDate);
        boolean before = endDate.isAfter(checkDate);
        return afterOrStart && before;
    }

    /**
      * @return - true, если это пустой, т.е. незаполненный адрес.
     */
    public boolean isEmpty() {
        return this.type.isEmpty();
    }

    public boolean typeIs(String type) {
        return this.type.equals(type);
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
