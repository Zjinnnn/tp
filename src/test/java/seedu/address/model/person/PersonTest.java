package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_G01;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_T09;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.week.Week;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice =
                new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withId(VALID_ID_BOB)
                .withTags(VALID_TAG_G01).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    /**
     * Tests if an empty Optional is returned when there's no attendance record for the current week.
     */
    @Test
    public void getAttendanceForCurrentWeek_noAttendance_emptyOptional() {
        Person emptyBob = new PersonBuilder(BOB).build();
        Optional<Attendance> result = emptyBob.getAttendanceForSpecifiedWeek(new Week(1));
        assertFalse(result.isPresent());
    }

    /**
     * Tests if an attendance record for the current week is correctly retrieved when it exists.
     */
    @Test
    public void getAttendanceForCurrentWeek_attendanceExists_optionalWithAttendance() {
        Week testWeek = new Week(1);
        Attendance attendance = new Attendance(testWeek, true, null);
        Person emptyAlice = new PersonBuilder(ALICE).build();
        emptyAlice.addAttendance(attendance);
        Optional<Attendance> result = emptyAlice.getAttendanceForSpecifiedWeek(testWeek);
        assertTrue(result.isPresent());
        assertTrue(result.get().getWeek().equals(testWeek));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_G01).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", id=" + ALICE.getId() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void mergeAttendanceRecordsMethod_noDuplicatedAttendance() {
        Week testWeek1 = new Week(1);
        Attendance attendance1 = new Attendance(testWeek1, true, null);
        List<Attendance> attendanceRecords1 = new ArrayList<>();
        attendanceRecords1.add(attendance1);

        Week testWeek2 = new Week(2);
        Attendance attendance2 = new Attendance(testWeek2, true, null);
        List<Attendance> attendanceRecords2 = new ArrayList<>();
        attendanceRecords2.add(attendance2);

        Person emptyBob = new PersonBuilder(BOB).build();
        emptyBob.mergeAttendanceRecords(attendanceRecords1, attendanceRecords2, emptyBob);

        List<Attendance> expectedRecords = new ArrayList<>();
        expectedRecords.add(attendance1);
        expectedRecords.add(attendance2);

        assertEquals(expectedRecords, emptyBob.getAttendanceRecords());
    }

    @Test
    public void mergeAttendanceRecordsMethod_withDuplicatedAttendance() {
        Week testWeek1 = new Week(1);
        Attendance attendance1 = new Attendance(testWeek1, true, null);
        List<Attendance> attendanceRecords1 = new ArrayList<>();
        attendanceRecords1.add(attendance1);

        Week testWeek2 = new Week(1);
        Attendance attendance2 = new Attendance(testWeek2, true, null);
        List<Attendance> attendanceRecords2 = new ArrayList<>();
        attendanceRecords2.add(attendance2);

        Person emptyFiona = new PersonBuilder(FIONA).build();
        emptyFiona.mergeAttendanceRecords(attendanceRecords1, attendanceRecords2, emptyFiona);

        List<Attendance> expectedRecords = new ArrayList<>();
        expectedRecords.add(attendance1);

        assertEquals(expectedRecords, emptyFiona.getAttendanceRecords());
    }

    @Test
    public void mergePersonsMethod() {
        Person primaryStudent = new PersonBuilder().withTags(VALID_TAG_G01).build();
        Person secondaryStudent = new PersonBuilder().withTags(VALID_TAG_T09).build();
        Person mergedStudent = primaryStudent.mergePersons(secondaryStudent);

        Person expectedMergedStudent = new PersonBuilder().withTags(VALID_TAG_G01, VALID_TAG_T09).build();

        assertEquals(expectedMergedStudent, mergedStudent);
    }

}
