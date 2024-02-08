package fi.tuni.prog3.sisu;

import java.io.IOException;
import java.net.MalformedURLException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * Junit tests for Class JsonData.
 *
 * @author eevih
 */
public class JsonDataTest {

    public JsonDataTest() {
    }

    JsonData instance;

    @Before
    public void setUp() throws IOException {
        instance = new JsonData();
    }

    /**
     * Test of getId method, of class JsonData.
     */
    @Test
    public void testGetId() {
        String name = null;
        try {
            instance.getId(name);
            fail();
        } catch (IOException e) {
        }
    }

    /**
     * Test of parseId method, of class JsonData.
     */
    @Test
    public void testParseId() {
        String name = "";
        try {
            instance.parseId(name);
            fail();
        } catch (IOException e) {
        }
    }

    /**
     * Test of getStudyModule method, of class JsonData.
     * @throws IOException if data cannot be read.
     */
    @Test
    public void testGetStudyModule() throws IOException {
        String rule = "otm-2fcd5127-e5cb-44c8-83bf-e3f92c3270a9";
        instance.parseId(rule);

        try {
            instance.getStudyModule(rule);
            fail();
        } catch (MalformedURLException e) {
        }
    }

    /**
     * Test of parseStudyModule method, of class JsonData.
     * @throws IOException if data cannot be read.
     */
    @Test
    public void testParseStudyModule() throws IOException {
        instance.parseDegreeProgrammes();
        instance.parseId("Arkkitehdin tutkinto-ohjelma");
        Id id = instance.getDegreeProgrammesByName().get(
            "Arkkitehdin tutkinto-ohjelma").getDegree();
        String rule = "otm-6ee2738c-0eec-430a-a338-dd46cd3658ba";
        instance.parseStudyModule(id, rule);
        String actual = id.getStudyModules().get(0).getNameEN();
        String expResult = "Joint Studies in Architecture, MSc";

        assertEquals(actual, expResult);
    }

    /**
     * Test of parseCourse method, of class JsonData.
     * @throws IOException if data cannot be read.
     */
    @Test
    public void testParseCourse() throws IOException {
        instance.parseDegreeProgrammes();
        instance.parseId("Arkkitehdin tutkinto-ohjelma");
        Id id = instance.getDegreeProgrammesByName().get(
            "Arkkitehdin tutkinto-ohjelma").getDegree();
        String rule = "tut-sm-g-7091";
        instance.parseStudyModule(id, rule);
        StudyModule sm = id.getStudyModules().get(0);
        Course course = sm.getCourses().get(0);
        String actual = course.getNameFI();
        String expResult = "Rakennusperinnön hoito";
        instance.parseCourse(expResult, actual);
    }

    /**
     * Test of getDegreeProgrammesByName method, of class JsonData.
     */
    @Test
    public void testGetDegreeProgrammesByName() {
        int expResult = 269;
        int result = instance
            .getDegreeProgrammesByName().size();
        assertTrue(expResult == result);
    }
}
