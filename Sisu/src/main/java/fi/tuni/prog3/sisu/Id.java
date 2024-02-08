package fi.tuni.prog3.sisu;

import java.util.ArrayList;

/**
 * Class for more specific information about a degree programme.
 *
 * @author eevih
 */
public class Id {

    private final String nameFI;
    private final String nameEN;
    private final String credits;

    private final ArrayList<StudyModule> studyModules = new ArrayList<>();

    /**
     * Constructs the Id.
     *
     * @param moduleNameFI name of the id in Finnish.
     * @param moduleNameEN name of the id in English.
     * @param credits credit units of the degree programme.
     */
    public Id(String moduleNameFI, String moduleNameEN, String credits) {

        this.nameFI = moduleNameFI;
        this.nameEN = moduleNameEN;
        this.credits = credits;

    }

    /**
     * Returns the name in Finnish.
     *
     * @return the name in Finnish.
     */
    public String getNameFI() {
        if ("".equals(nameFI)) {
            return nameEN;
        }
        return nameFI;
    }

    /**
     * Returns the name in English.
     *
     * @return the name in English.
     */
    public String getNameEN() {
        if ("".equals(nameEN)) {
            return nameFI;
        }
        return nameEN;
    }

    /**
     * Returns the credit units.
     *
     * @return the credit units.
     */
    public String getCredits() {
        return credits;
    }

    /**
     * Adds the subject belonging to the degree programme.
     *
     * @param id subject to be added.
     */
    public void setStudyModule(StudyModule id) {
        studyModules.add(id);
    }

    /**
     * Returns a list of subjects of a degree programme.
     *
     * @return list of subjects of a degree programme.
     */
    public ArrayList<StudyModule> getStudyModules() {
        return studyModules;
    }

    /**
     * Returns the credit units in specific form in Finnish.
     *
     * @return credit units in specific form in Finnish.
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
     * @return credit units in specific form in English.
     */
    public String getCreditUnitsEN() {
        if (credits.equals("")) {
            return credits;
        }
        return " " + credits + "cu";
    }

}
