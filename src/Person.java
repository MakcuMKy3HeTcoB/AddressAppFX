import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.io.*;

public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private transient StringProperty firstName;
    private transient StringProperty lastName;
    private transient StringProperty street;
    private transient StringProperty city;
    private transient StringProperty postalCode;
    private transient StringProperty birthday;

    public Person(String firstName, String lastName, String street,
                  String city, String postalCode, String birthday) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.street = new SimpleStringProperty(street);
        this.city = new SimpleStringProperty(city);
        this.postalCode = new SimpleStringProperty(postalCode);
        this.birthday = new SimpleStringProperty(birthday);
    }

    // Сериализация
    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getFirstName());
        out.writeObject(getLastName());
        out.writeObject(getStreet());
        out.writeObject(getCity());
        out.writeObject(getPostalCode());
        out.writeObject(getBirthday());
    }

    // Десериализация
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        firstName = new SimpleStringProperty((String) in.readObject());
        lastName = new SimpleStringProperty((String) in.readObject());
        street = new SimpleStringProperty((String) in.readObject());
        city = new SimpleStringProperty((String) in.readObject());
        postalCode = new SimpleStringProperty((String) in.readObject());
        birthday = new SimpleStringProperty((String) in.readObject());
    }

    // Геттеры и сеттеры
    public String getFirstName() {
        return firstNameProperty().get();
    }

    public void setFirstName(String firstName) {
        firstNameProperty().set(firstName);
    }

    public StringProperty firstNameProperty() {
        if (firstName == null) {
            firstName = new SimpleStringProperty();
        }
        return firstName;
    }

    public String getLastName() {
        return lastNameProperty().get();
    }

    public void setLastName(String lastName) {
        lastNameProperty().set(lastName);
    }

    public StringProperty lastNameProperty() {
        if (lastName == null) {
            lastName = new SimpleStringProperty();
        }
        return lastName;
    }

    public String getStreet() {
        return streetProperty().get();
    }

    public void setStreet(String street) {
        streetProperty().set(street);
    }

    public StringProperty streetProperty() {
        if (street == null) {
            street = new SimpleStringProperty();
        }
        return street;
    }

    public String getCity() {
        return cityProperty().get();
    }

    public void setCity(String city) {
        cityProperty().set(city);
    }

    public StringProperty cityProperty() {
        if (city == null) {
            city = new SimpleStringProperty();
        }
        return city;
    }

    public String getPostalCode() {
        return postalCodeProperty().get();
    }

    public void setPostalCode(String postalCode) {
        postalCodeProperty().set(postalCode);
    }

    public StringProperty postalCodeProperty() {
        if (postalCode == null) {
            postalCode = new SimpleStringProperty();
        }
        return postalCode;
    }

    public String getBirthday() {
        return birthdayProperty().get();
    }

    public void setBirthday(String birthday) {
        birthdayProperty().set(birthday);
    }

    public StringProperty birthdayProperty() {
        if (birthday == null) {
            birthday = new SimpleStringProperty();
        }
        return birthday;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;
        return getFirstName().equals(person.getFirstName()) &&
                getLastName().equals(person.getLastName()) &&
                getStreet().equals(person.getStreet()) &&
                getCity().equals(person.getCity()) &&
                getPostalCode().equals(person.getPostalCode()) &&
                getBirthday().equals(person.getBirthday());
    }

    @Override
    public int hashCode() {
        int result = getFirstName().hashCode();
        result = 31 * result + getLastName().hashCode();
        result = 31 * result + getStreet().hashCode();
        result = 31 * result + getCity().hashCode();
        result = 31 * result + getPostalCode().hashCode();
        result = 31 * result + getBirthday().hashCode();
        return result;
    }
}