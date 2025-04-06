import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty street;
    private final StringProperty city;
    private final StringProperty postalCode;
    private final StringProperty birthday;

    //Основной конструктор с параметрами
    public Person(String firstName, String lastName, String street, String city, String postalCode, String birthday) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.street = new SimpleStringProperty(street);
        this.city = new SimpleStringProperty(city);
        this.postalCode = new SimpleStringProperty(postalCode);
        this.birthday = new SimpleStringProperty(birthday);
    }

    // First Name
    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    // Last Name
    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    // Street
    public String getStreet() {
        return street.get();
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    @SuppressWarnings("unused")
    public StringProperty streetProperty() {
        return street;
    }

    // City
    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    @SuppressWarnings("unused")
    public StringProperty cityProperty() {
        return city;
    }

    // Postal Code
    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    @SuppressWarnings("unused")
    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    // Birthday
    public String getBirthday() {
        return birthday.get();
    }

    public void setBirthday(String birthday) {
        this.birthday.set(birthday);
    }

    @SuppressWarnings("unused")
    public StringProperty birthdayProperty() {
        return birthday;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }
}