package seedu.address.model.applicant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmail("-peterjack@example.com")); // local part starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack-@example.com")); // local part ends with a hyphen
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@bc")); // minimal
        assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void isValidEmail_adjacentLocalPunctuation_returnsTrue() {
        for (String punctuation : List.of("__", "++", "--", "_+", "+-", "._", "_.", "-.-", "_+.-")) {
            assertTrue(Email.isValidEmail("alice" + punctuation + "lee@example.com"));
        }
    }

    @Test
    public void isValidEmail_invalidLocalPunctuation_returnsFalse() {
        for (String localPart : List.of("alice..lee", "alice+.._lee", "alice...lee", "+alice", "alice+",
                "_alice", "alice_", ".alice", "alice.", "-alice", "alice-", "++", "alice!lee")) {
            assertFalse(Email.isValidEmail(localPart + "@example.com"));
        }
    }

    @Test
    public void isValidEmail_adjacentPunctuationLengthBoundary_expectedResult() {
        String email = "a" + "+".repeat(240) + "b@example.com";
        assertTrue(Email.isValidEmail(email)); // 254 characters
        assertFalse(Email.isValidEmail("a" + email));
    }

    @Test
    public void isValidEmail_consecutiveDomainHyphens_success() {
        assertTrue(Email.isValidEmail("alice@xn--bcher-kva.de"));
        assertTrue(Email.isValidEmail("alice@example.xn--p1ai"));
        assertTrue(Email.isValidEmail("alice@a---b.com"));
        assertFalse(Email.isValidEmail("alice@--example.com"));
        assertFalse(Email.isValidEmail("alice@example--.com"));
        assertFalse(Email.isValidEmail("alice@example.c"));
    }

    @Test
    public void isValidEmail_lengthBoundary_expectedResult() {
        String email = "a".repeat(242) + "@example.com";
        assertTrue(Email.isValidEmail(email)); // 254 characters
        assertFalse(Email.isValidEmail("a" + email));
    }

    @Test
    public void isValidEmail_longInput_returnsFalse() {
        assertFalse(Email.isValidEmail("a.".repeat(2000) + "a@example.com"));
        assertFalse(Email.isValidEmail("a".repeat(20000) + "!"));
        assertFalse(Email.isValidEmail("a@" + "a".repeat(240) + "!"));
        assertFalse(Email.isValidEmail("a@" + "a-".repeat(2000) + "a.com"));
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }
}
