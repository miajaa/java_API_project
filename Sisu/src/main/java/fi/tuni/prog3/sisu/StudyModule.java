package fi.tuni.prog3.sisu;

import java.util.ArrayList;

/**
 * Class for a subject belonging to a degree programme.
 *
 * @author eevih
 */
public class StudyModule {

    private final String nameFI;
    private final String nameEN;
    private final String credits;

    private final ArrayList<Course> courses = new ArrayList<>();
    private final ArrayList<GroupingModule> groupingModules = new ArrayList<>();

    /**
     * Constructs the StudyModule (subject).
     *
     * @param nameFI name of the subject in Finnish.
     * @param nameEN name of the subject in English.
     * @param creditUnits subject's credit units.
     */
    public StudyModule(String nameFI, String nameEN, String creditUnits) {
        this.nameFI = nameFI;
        this.nameEN = nameEN;
        this.credits = creditUnits;
    }

    /**
     * Returns the name of the subject in Finnish or if not available, English.
     *
     * @return the name of the subject in Finnish or if not available, English.
     */
    String getNameFI() {
        if ("".equals(nameFI)) {
            return nameEN;
        }
        return nameFI;
    }

    /**
     * Returns the name of the subject in English or if not available, Finnish.
     *
     * @return the name of the subject in English or if not available, Finnish.
     */
    String getNameEN() {
        if ("".equals(nameEN)) {
            return nameFI;
        }
        return nameEN;
    }

    /**
     * Returns the credit units of the subject.
     *
     * @return credit units of the subject.
     */
    String getCredits() {
        return credits;
    }

    /**
     * Adds the Course object that belongs to this subject.
     *
     * @param course Course object that belongs to the subject.
     */
    public void setCourse(Course course) {
        courses.add(course);
    }

    /**
     * Returns an ArrayList of Courses that belong to the subject.
     *
     * @return ArrayList of Courses that belong to the subject.
     */
    public ArrayList<Course> getCourses() {
        return courses;
    }

    /**
     * Adds a GroupinModule object that belongs to the subject.
     *
     * @param mod GroupinModule object that belongs to the subject.
     */
    public void setGroupingModules(GroupingModule mod) {
        groupingModules.add(mod);
    }

    /**
     * Returns an ArrayList of GroupingModules belonging to the subject.
     *
     * @return ArrayList of GroupingModules belonging to the subject.
     */
    public ArrayList<GroupingModule> getGroupingModules() {
        return groupingModules;
    }

    /**
     * Returns the credit units in specific form in Finnish.
     *
     * @return the credit units in specific form in Finnish.
     */
    public String getCreditUnitsFI() {
        if (credits.equals("")) {
            return credits;
        }
        return " " + credits + "op";
    }

    /**
     * Returns the credit units in specific form in English.
     *
     * @return the credit units in specific form in English.
     */
    public String getCreditUnitsEN() {
        if (credits.equals("")) {
            return credits;
        }
        return " " + credits + "cu";
    }

}
