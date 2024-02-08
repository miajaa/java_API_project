package fi.tuni.prog3.sisu;

import java.util.ArrayList;

/**
 * Class for grouping courses.
 *
 * @author eevih
 */
public class GroupingModule {

    private final String nameFI;
    private final String nameEN;
    private final String credits;
    private final ArrayList<Course> listOfCourses = new ArrayList<>();
    private final ArrayList<GroupingModule> subModules = new ArrayList<>();

    /**
     * Constructs the GroupinModule.
     *
     * @param nameFI name of the grouping module in Finnish.
     * @param nameEN name of the grouping module in English.
     * @param credits credit units of the grouping module.
     */
    public GroupingModule(String nameFI, String nameEN, String credits) {
        this.nameFI = nameFI;
        this.nameEN = nameEN;
        this.credits = credits;
    }

    /**
     * Returns the name of the grouping module in Finnish or if not available,
     * English.
     *
     * @return name of the grouping module in Finnish or if not available,
     * English.
     */
    public String getNameFI() {
        if (nameFI.equals("")) {
            return nameEN;
        }
        return nameFI;
    }

    /**
     * Returns the name of the grouping module in English or if not available,
     * Finnish.
     *
     * @return the name of the grouping module in English or if not available,
     * Finnish.
     */
    public String getNameEN() {
        if (nameEN.equals("")) {
            return nameFI;
        }
        return nameEN;
    }

    /**
     * Returns the credit units of a grouping module.
     *
     * @return the credit units of a grouping module.
     */
    public String getCredits() {
        return credits;
    }

    /**
     * Adds a Course object belonging to this grouping module.
     *
     * @param course Course belonging to this grouping module.
     */
    public void setCourse(Course course) {
        listOfCourses.add(course);
    }

    /**
     * Returns an ArrayList of Course objects belonging to this grouping
     * module.
     *
     * @return ArrayList of Course objects belonging to this grouping module.
     */
    public ArrayList<Course> getCourses() {
        return listOfCourses;
    }

    /**
     * Adds a Grouping Module belonging to this grouping module.
     *
     * @param subModule GroupingModule belonging to this grouping module.
     */
    public void setSubModule(GroupingModule subModule) {
        subModules.add(subModule);
    }

    /**
     * Returns an ArrayList of GroupingModule objects belonging to this
     * grouping module.
     *
     * @return ArrayList of GroupingModule objects belonging to this grouping
     * module.
     */
    public ArrayList<GroupingModule> getSubModules() {
        return subModules;
    }

    /**
     * Returns credit units in a specific form in Finnish.
     *
     * @return credit units in a specific form in Finnish.
     */
    public String getCreditUnitsFI() {
        if (credits.equals("")) {
            return credits;
        }
        return " " + credits + "op";
    }

    /**
     * Returns credit units in a specific form in English.
     *
     * @return credit units in a specific form in English.
     */
    public String getCreditUnitsEN() {
        if (credits.equals("")) {
            return credits;
        }
        return " " + credits + "cu";
    }

}
