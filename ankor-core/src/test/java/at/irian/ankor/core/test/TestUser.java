package at.irian.ankor.core.test;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class TestUser {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestUser.class);

    private final String firstName;
    private final String lastName;

    public TestUser(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "TestUser{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               '}';
    }
}
