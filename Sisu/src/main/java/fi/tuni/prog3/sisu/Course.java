package fi.tuni.prog3.sisu;

import java.util.ArrayList;

/**
 * Class which represents a course.
 *
 * @author eevih
 */
public class Course {

    private final String creditsMin;
    private final ArrayList<String> curriculumPeriods;
    private final String nameFI;
    private final String nameEN;
    private final String outcomesFI;
    private final String outcomesEN;
    private final String prerequisitesFI;
    private final String prerequisitesEN;
    private final String contentFI;
    private final String contentEN;
    private final String learningMaterialFI;
    private final String learningMaterialEN;

    /**
     * Construct a Course.
     *
     * @param creditUnits credit units of the course.
     * @param curriculumPeriods periods when the course is ongoing.
     * @param nameFI name of the course in Finnish.
     * @param nameEN name of the course in English.
     * @param outcomesFI outcomes of the course in Finnish.
     * @param outcomesEN outcomes of the course in English.
     * @param prerequisitesFI prerequisites of the course in Finnish.
     * @param prerequisitesEN prerequisites of the course in English.
     * @param contentFI contents of the course in Finnish.
     * @param contentEN contents of the course in English.
     * @param learningMaterialFI learning material of the course in Finnish.
     * @param learningMaterialEN learning material of the course in English.
     */
    public Course(String creditUnits,
        ArrayList<String> curriculumPeriods, String nameFI, String nameEN,
        String outcomesFI, String outcomesEN, String prerequisitesFI,
        String prerequisitesEN, String contentFI, String contentEN,
        String learningMaterialFI, String learningMaterialEN) {

        this.creditsMin = creditUnits;
        this.curriculumPeriods = curriculumPeriods;
        this.nameFI = nameFI;
        this.nameEN = nameEN;
        this.outcomesFI = outcomesFI;
        this.outcomesEN = outcomesEN;
        this.prerequisitesFI = prerequisitesFI;
        this.prerequisitesEN = prerequisitesEN;
        this.contentFI = contentFI;
        this.contentEN = contentEN;
        this.learningMaterialFI = learningMaterialFI;
        this.learningMaterialEN = learningMaterialEN;
    }

    /**
     * Returns the credit units of the course.
     *
     * @return credit units of the course.
     */
    public String getCredits() {
        return creditsMin;
    }

    /**
     * Returns an ArrayLsit of the periods of the course.
     *
     * @return ArrayLsit of the periods of the course.
     */
    public ArrayList<String> getCurriculumPeriods() {
        return curriculumPeriods;
    }

    /**
     * Returns the name of the course in Finnish or if not available, English.
     *
     * @return name of the course in Finnish or if not available, English.
     */
    public String getNameFI() {
        if ("".equals(nameFI)) {
            return nameEN;
        }
        return nameFI;
    }

    /**
     * Returns the name of the course in English or if not available, Finnish.
     *
     * @return name of the course in English or if not available, Finnish.
     */
    public String getNameEN() {
        if ("".equals(nameEN)) {
            return nameFI;
        }
        return nameEN;
    }

    /**
     * Returns outcomes of the course in Finnish.
     *
     * @return outcomes of the course in Finnish.
     */
    public String getOutcomesFI() {
        return outcomesFI;
    }

    /**
     * Returns outcomes of the course in English.
     *
     * @return outcomes of the course in English.
     */
    public String getOutcomesEN() {
        return outcomesEN;
    }

    /**
     * Returns prerequisites in Finnish.
     *
     * @return prerequisites in Finnish.
     */
    public String getPrerequisitesFI() {
        return prerequisitesFI;
    }

    /**
     * Returns prerequisites in English.
     *
     * @return prerequisites in English.
     */
    public String getPrerequisitesEN() {
        return prerequisitesEN;
    }

    /**
     * Returns course content in Finnish.
     *
     * @return String course content in Finnish.
     */
    public String getContentFI() {
        return contentFI;
    }

    /**
     * Returns course content in English.
     *
     * @return course content in English.
     */
    public String getContentEN() {
        return contentEN;
    }

    /**
     * Returns learning material of the course in Finnish.
     *
     * @return learning material of the course in Finnish.
     */
    public String getLearningMaterialFI() {
        return learningMaterialFI;
    }

    /**
     * Returns learning material of the course in English.
     *
     * @return learning material of the course in English.
     */
    public String getLearningMaterialEN() {
        return learningMaterialEN;
    }

    /**
     * Returns credit units of the course in specific form in Finnish.
     *
     * @return credit units of the course in specific form in Finnish.
     */
    public String getCreditUnitsFI() {
        if (creditsMin.equals("")) {
            return creditsMin;
        }
        return " " + creditsMin + "op";
    }

    /**
     * Returns credit units of the course in specific form in English.
     *
     * @return credit units of the course in specific form in English.
     */
    public String getCreditUnitsEN() {
        if (creditsMin.equals("")) {
            return creditsMin;
        }
        return " " + creditsMin + "cu";
    }

}
