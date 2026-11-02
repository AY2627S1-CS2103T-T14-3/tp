package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        for (String phone : List.of("", "12", "1234567890123456", "+65-9123-4567-")) {
            assertThrows(IllegalArgumentException.class, Phone.MESSAGE_CONSTRAINTS, () -> new Phone(phone));
        }
    }

    @Test
    public void isValidPhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));
    }

    @Test
    public void isValidPhone_invalidDigitCount_returnsFalse() {
        for (String phone : List.of("", "91", "+1-2", "1234567890123456",
                "+123 456 789 012 3456", "123456789012345678901234567890", "1".repeat(20000))) {
            assertFalse(Phone.isValidPhone(phone), phone);
        }
    }

    @Test
    public void isValidPhone_invalidFormatting_returnsFalse() {
        for (String phone : List.of(" ", "phone", "9011p041", "++6591234567", "65+91234567", "+ 6591234567",
                " 91234567", "91234567 ", "-91234567", "91234567-", "9123--4567", "9123  4567",
                "9123 -4567", "9123\t4567", "9123\n4567", "9123\u20034567", "(65)91234567",
                "9123.4567", "\uFF11\uFF12\uFF13\uFF14\uFF15\uFF16\uFF17")) {
            assertFalse(Phone.isValidPhone(phone), phone);
        }
    }

    @Test
    public void isValidPhone_validLocalAndInternationalFormats_returnsTrue() {
        for (String phone : List.of("123", "+1-2 3", "1234567", "93121534", "124293842033123", "+124293842033123",
                "000", "01234567", "0123-4567", "9312 1534", "9312-1534", "+65 9123 4567",
                "+1-202-555-0123", "+65 9123-4567",
                "+1-2-3-4-5-6-7-8-9-0-1-2-3-4-5")) {
            assertTrue(Phone.isValidPhone(phone), phone);
            assertEquals(phone, new Phone(phone).value);
            assertEquals(phone, new Phone(phone).toString());
        }
    }

    @Test
    public void isValidPhone_zeroImmediatelyAfterPlus_returnsFalse() {
        for (String phone : List.of("+012", "+06591234567", "+065 9123 4567", "+0-65-9123-4567")) {
            assertFalse(Phone.isValidPhone(phone), phone);
            assertThrows(IllegalArgumentException.class, Phone.MESSAGE_CONSTRAINTS, () -> new Phone(phone));
        }
    }

    @Test
    public void equals() {
        Phone phone = new Phone("999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("999")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("995")));
    }
}
