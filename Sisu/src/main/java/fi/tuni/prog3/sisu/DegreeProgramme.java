package fi.tuni.prog3.sisu;

/**
 * Class for a degree programme.
 *
 * @author eevih
 */
public class DegreeProgramme {

    private final String id;
    private final String name;
    private Id degree;

    /**
     * Constructs a DegreeProgramme.
     *
     * @param id Id that belongs to the degree programme.
     * @param name name of the degree programme.
     */
    public DegreeProgramme(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the identification number.
     *
     * @return identification number.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the degree programme.
     *
     * @return the name of the degree programme.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Id belonging to the degree programme.
     *
     * @param id Id belonging to the degree programme.
     */
    public void setId(Id id) {
        this.degree = id;
    }

    /**
     *
     * @return the Id object which belongs to this degree programme.
     */
    public Id getDegree() {
        return degree;
    }

}
