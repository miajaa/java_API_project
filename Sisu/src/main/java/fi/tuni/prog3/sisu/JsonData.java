package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import static java.util.Collections.sort;
import java.util.HashMap;

/**
 * Class for fetching data from Sisu API and storing it.
 *
 * @author eevih
 */
public class JsonData {

    private final HashMap<String, DegreeProgramme> degreeProgrammes = new HashMap<>();
    private final ArrayList<String> listOfDegreeProgrammeNames = new ArrayList<>();

    /**
     * Constructs an initially empty JsonData.
     *
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    public JsonData() throws MalformedURLException, IOException {
    }

    /**
     * Returns JsonArray from Sisu API containing all TUNI degree programmes.
     *
     * @return JsonArray containing all TUNI degree programmes.
     * @throws ProtocolException if there is an error in underlying protocol.
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    public JsonArray getDegrees() throws ProtocolException,
        MalformedURLException, IOException {
        URL url = new URL(
            "https://sis-tuni.funidata.fi/kori/api/module-search?curriculum"
            + "PeriodId=uta-lvv-2021&universityId=tuni-university-"
            + "root-id&moduleType=DegreeProgramme&limit=1000");
        HttpURLConnection connection
            = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        JsonObject json = readJsonObjectFromUrl(url);
        var array = json.getAsJsonArray("searchResults");

        return array;
    }

    /**
     * Reads and stores information of Degrees.
     *
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    public void parseDegreeProgrammes() throws MalformedURLException,
        IOException {

        for (var degreeProgramme : getDegrees()) {
            String id = degreeProgramme.getAsJsonObject().get("id")
                .getAsString();
            String name = degreeProgramme.getAsJsonObject().get("name")
                .getAsString();

            DegreeProgramme dp = new DegreeProgramme(id, name);
            degreeProgrammes.put(name, dp);
            listOfDegreeProgrammeNames.add(name);
        }
        sort(listOfDegreeProgrammeNames);
    }

    /**
     * Returns JsonObject from Sisu API containing more information about a
     * degree programme.
     *
     * @param name the name of a degree programme.
     * @return JsonObject of more specific information about a degree
     * programme.
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    public JsonObject getId(String name) throws MalformedURLException,
        IOException {
        URL url = new URL(
            "https://sis-tuni.funidata.fi/kori/api/modules/" + degreeProgrammes
                .get(name).getId());
        HttpURLConnection connection
            = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        return readJsonObjectFromUrl(url);
    }

    /**
     * Reads and stores information of a degree programme by id number.
     *
     * @param name the name of a degree programme.
     * @throws IOException if data cannot be read.
     */
    public void parseId(String name) throws IOException {
        String moduleNameFI = "";
        String moduleNameEN = "";
        String creditUnits = "";

        JsonObject moduleName = getId(name)
            .getAsJsonObject()
            .get("name")
            .getAsJsonObject();
        if (moduleName.has("fi")) {
            moduleNameFI = moduleName
                .get("fi")
                .getAsString();
        }
        if (moduleName.has("en")) {
            moduleNameEN = moduleName
                .get("en")
                .getAsString();
        }
        JsonElement credits = getId(name)
            .getAsJsonObject()
            .get("targetCredits")
            .getAsJsonObject()
            .get("min");

        if (!credits.isJsonNull()) {
            creditUnits = credits.getAsString();
        }

        var rule = (JsonObject) getId(name).get("rule");
        var rule2 = rule;
        if (rule.has("rule")) {
            rule2 = (JsonObject) rule.get("rule");
        }
        var rules = (JsonArray) rule2.get("rules");
        JsonArray rules3 = rules;
        var rules2 = (JsonObject) rules.get(0);
        if (rules2.has("rules")) {
            rules3 = (JsonArray) rules2.get("rules");
        }
        Id id = new Id(moduleNameFI, moduleNameEN, creditUnits);
        degreeProgrammes.get(name).setId(id);

        for (var rul : rules3) {
            String r = rul.getAsJsonObject().get("moduleGroupId")
                .getAsString();
            parseStudyModule(id, r);
        }
    }

    /**
     * Returns JsonArray from Sisu API containing information of a degree
     * programme's subject.
     *
     * @param rule identification number of a subject.
     * @return JsonArray of a degree programme's subject.
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    public JsonObject getStudyModule(String rule) throws
        MalformedURLException, IOException {
        URL url = new URL(
            "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="
            + rule + "&universityId=tuni-university-root-id");

        HttpURLConnection connection
            = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        return readJsonArrayFromUrl(url);
    }

    /**
     * Reads and stores information about a degree programme's subject
     *
     * @param id degree programme in which a subject belongs to.
     * @param rule identification number of a subject.
     * @throws IOException if data cannot be read.
     */
    public void parseStudyModule(Id id, String rule) throws IOException {
        JsonObject data = getStudyModule(rule);
        String nameFI = "";
        String nameEN = "";
        String creditUnits = "";

        JsonObject name1 = data
            .getAsJsonObject()
            .get("name")
            .getAsJsonObject();

        if (name1.has("fi")) {
            nameFI = name1.get("fi")
                .getAsString();
        }
        if (name1.has("en")) {
            nameEN = name1.get("en")
                .getAsString();
        }
        if (data.getAsJsonObject().has("targetCredits")) {

            JsonElement targetCredits = data
                .getAsJsonObject()
                .get("targetCredits")
                .getAsJsonObject()
                .get("min");

            if (!targetCredits.isJsonNull()) {
                creditUnits = targetCredits.getAsString();
            }
        }

        StudyModule studyModule = new StudyModule(nameFI, nameEN,
            creditUnits);
        id.setStudyModule(studyModule);

        setRules(data, studyModule);
        {
        }
    }

    /**
     * Returns JsonArray from Sisu API containing information of a course.
     *
     * @param rule identification number of a course.
     * @return JsonArray of a course's information.
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    private JsonObject getCourse(String rule) throws
        MalformedURLException, IOException {
        URL url = new URL(
            "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId="
            + rule + "&universityId=tuni-university-root-id");

        HttpURLConnection connection
            = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        return readJsonArrayFromUrl(url);
    }

    /**
     * Reads and stores information of a course.
     *
     * @param groupId grouping module in which a course belongs to.
     * @param rule identification number of a course.
     * @throws IOException if data cannot be read.
     */
    public void parseCourse(Object groupId, String rule) throws
        IOException {
        JsonObject data = getCourse(rule);
        String credits = "";
        ArrayList<String> curriculumPeriods = new ArrayList<>();
        String nameFI = "";
        String nameEN = "";
        String outcomesFI = "";
        String outcomesEN = "";
        String prerequisitesFI = "";
        String prerequisitesEN = "";
        String contentFI = "";
        String contentEN = "";
        String learningMaterialFI = "";
        String learningMaterialEN = "";

        JsonObject nameAsObject = data.getAsJsonObject().get("name")
            .getAsJsonObject();
        if (nameAsObject.has("fi")) {
            nameFI = nameAsObject.getAsJsonObject().get("fi").getAsString();
        }
        if (nameAsObject.has("en")) {
            nameEN = nameAsObject.getAsJsonObject().get("en").getAsString();
        }
        JsonObject creditsAsObject = data.getAsJsonObject().get("credits")
            .getAsJsonObject();
        if (!creditsAsObject.isJsonNull()) {
            if (creditsAsObject.has("min")) {
                if (!creditsAsObject.get("min").isJsonNull()) {
                    credits = creditsAsObject.get("min").getAsString();
                }
            }
        }
        if (!data.getAsJsonObject().get("curriculumPeriodIds").isJsonNull()) {
            JsonArray periods = data.get("curriculumPeriodIds")
                .getAsJsonArray();

            for (var period : periods) {
                curriculumPeriods.add(period.getAsString());
            }
        }

        if (!data.get("outcomes").isJsonNull()) {
            if (data.getAsJsonObject("outcomes").has("fi")) {
                outcomesFI = data.getAsJsonObject("outcomes")
                    .getAsJsonObject().get("fi")
                    .getAsString();
            }
            if (data.getAsJsonObject("outcomes").has("en")) {
                outcomesEN = data.getAsJsonObject("outcomes")
                    .getAsJsonObject().get("en")
                    .getAsString();
            }
        }

        if (!data.get("prerequisites").isJsonNull()) {
            if (data.getAsJsonObject("prerequisites").has("fi")) {
                prerequisitesFI = data.getAsJsonObject("prerequisites")
                    .getAsJsonObject().get(
                        "fi").getAsString();
            }
            if (data.getAsJsonObject("prerequisites").has("en")) {
                prerequisitesEN = data.getAsJsonObject("prerequisites")
                    .getAsJsonObject().get(
                        "en").getAsString();
            }
        }
        if (!data.get("content").isJsonNull()) {
            if (data.getAsJsonObject("content").has("fi")) {
                contentFI = data.getAsJsonObject("content").getAsJsonObject()
                    .get("fi")
                    .getAsString();
            }
            if (data.getAsJsonObject("content").has("en")) {
                contentEN = data.getAsJsonObject("content").getAsJsonObject()
                    .get("en")
                    .getAsString();
            }
        }
        if (!data.get("learningMaterial").isJsonNull()) {
            if (data.getAsJsonObject("learningMaterial").has("fi")) {
                learningMaterialFI = data.getAsJsonObject("learningMaterial")
                    .getAsJsonObject()
                    .get("fi").getAsString();
            } else if (data.getAsJsonObject("learningMaterial").has("en")) {
                learningMaterialEN = data.getAsJsonObject("learningMaterial")
                    .getAsJsonObject()
                    .get("en").getAsString();
            }
        }
        Course course = new Course(credits, curriculumPeriods, nameFI, nameEN,
            outcomesFI, outcomesEN, prerequisitesFI, prerequisitesEN,
            contentFI, contentEN, learningMaterialFI, learningMaterialEN);

        if (groupId instanceof GroupingModule) {
            GroupingModule groupModule = (GroupingModule) groupId;
            groupModule.setCourse(course);
        }
        if (groupId instanceof StudyModule) {
            StudyModule studyModule = (StudyModule) groupId;
            studyModule.setCourse(course);
        }
    }

    /**
     * Returns JsonObject from Sisu API containing information of a grouping
     * module.
     *
     * @param rule identification number of a grouping module.
     * @return JsonObject containing information of a grouping module.
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    private JsonObject getGroupingModule(String rule) throws
        MalformedURLException, IOException {
        URL url = new URL(
            "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId="
            + rule + "&universityId=tuni-university-root-id");

        HttpURLConnection connection
            = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        return readJsonArrayFromUrl(url);
    }

    /**
     * Reads and stores information of a group module.
     *
     * @param module grouping module or study module the grouping module
     * belongs to.
     * @param rule identification number of a group module.
     * @throws IOException if data cannot be read.
     */
    public void parseGroupingModule(Object module, String rule)
        throws
        IOException {
        JsonObject data = getGroupingModule(rule);
        String nameFI = "";
        String nameEN = "";
        String credits = "";

        JsonObject nameAsJson = data
            .getAsJsonObject()
            .get("name")
            .getAsJsonObject();

        if (nameAsJson.has("fi")) {
            nameFI = nameAsJson.get("fi")
                .getAsString();
        }
        if (nameAsJson.has("en")) {
            nameEN = nameAsJson.get("en")
                .getAsString();
        }

        if (data.getAsJsonObject().has("targetCredits")) {
            JsonObject targetCredits = data.getAsJsonObject().get(
                "targetCredits")
                .getAsJsonObject();
            if (!targetCredits.get("min").isJsonNull()) {
                credits = targetCredits.get("min").getAsString();
            }
        }

        GroupingModule groupingModule = new GroupingModule(nameFI, nameEN,
            credits);

        if (module instanceof StudyModule) {
            StudyModule studyModule = (StudyModule) module;
            studyModule.setGroupingModules(groupingModule);
        }
        if (module instanceof GroupingModule) {
            GroupingModule groupModule = (GroupingModule) module;
            groupModule.setSubModule(groupingModule);
        }
        setRules(data, groupingModule);

    }

    /**
     * Returns JsonArray of identification number information.
     *
     * @param object JsonObject of identification number information.
     * @return JsonArray of identification number information.
     * @throws IOException if data cannot be read.
     */
    private void setRules(JsonObject object, Object module) throws IOException {
        JsonArray rules = null;
        if (object.has("rule")) {
            JsonElement rule1 = object
                .getAsJsonObject()
                .get("rule");
            if (rule1.getAsJsonObject().has("rule")) {
                JsonObject rule2 = rule1.getAsJsonObject().get("rule")
                    .getAsJsonObject();
                if (!rule2.get("rules").getAsJsonArray().get(0)
                    .getAsJsonObject()
                    .has("rules")) {
                    rules = rule2.get("rules").getAsJsonArray();
                } else {
                    JsonObject rules1 = rule2.get("rules").getAsJsonArray()
                        .get(0)
                        .getAsJsonObject();
                    rules = rules1.getAsJsonObject().get("rules")
                        .getAsJsonArray();
                }

            } else {
                rules = rule1.getAsJsonObject().get("rules")
                    .getAsJsonArray();
            }
        }
        for (var rule : rules) {

            JsonArray rules2 = null;
            if (rule.getAsJsonObject().has("courseUnitGroupId")) {
                parseCourse(module, rule.getAsJsonObject().get(
                    "courseUnitGroupId").getAsString());
            } else if (rule.getAsJsonObject().has("moduleGroupId")) {
                parseGroupingModule(module, rule.getAsJsonObject().get(
                    "moduleGroupId").getAsString());
            }
            if (rule.getAsJsonObject().has("rule")) {
                rules2 = rule.getAsJsonObject().get("rule")
                    .getAsJsonObject().get("rules").getAsJsonArray();
            } else if (rule.getAsJsonObject().has("rules")) {
                rules2 = rule.getAsJsonObject().get("rules")
                    .getAsJsonArray();
            }
            if (rules2 != null) {
                for (var cr2 : rules2) {
                    if (cr2.getAsJsonObject().has("courseUnitGroupId")) {
                        parseCourse(module, cr2.getAsJsonObject().get(
                            "courseUnitGroupId").getAsString());
                    }
                    if (cr2.getAsJsonObject().has("moduleGroupId")) {
                        parseGroupingModule(module, cr2
                            .getAsJsonObject()
                            .get("moduleGroupId").getAsString());

                    }
                }
            }
        }
    }

    /**
     * Returns JsonObject containing information from Sisu API.
     *
     * @param url Url of Sisu API.
     * @return JsonObject containing information from Sisu API.
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    private JsonObject readJsonObjectFromUrl(URL url) throws
        MalformedURLException,
        IOException {

        InputStream inputStream = url.openStream();
        Gson gson = new Gson();
        InputStreamReader streamReader = new InputStreamReader(inputStream,
            Charset.forName("UTF-8"));
        JsonObject obj = gson.fromJson(streamReader, JsonObject.class
        );
        return obj;
    }

    /**
     * Returns JsonArray containing information from Sisu API.
     *
     * @param url Url of Sisu API.
     * @return JsonArray containing information from Sisu API.
     * @throws MalformedURLException if the URL is missing or invalid.
     * @throws IOException if data cannot be read.
     */
    private JsonObject readJsonArrayFromUrl(URL url) throws
        MalformedURLException,
        IOException {

        InputStream inputStream = url.openStream();
        Gson gson = new Gson();
        InputStreamReader streamReader = new InputStreamReader(inputStream,
            Charset.forName("UTF-8"));
        JsonArray array = gson.fromJson(streamReader, JsonArray.class
        );
        JsonObject obj = array.get(0).getAsJsonObject();

        return obj;
    }

    /**
     * Returns ArrayList of all degree programme names.
     *
     * @return ArrayList of all degree programme names.
     */
    public ArrayList<String> getDegreeProgrammeNames() {
        return listOfDegreeProgrammeNames;
    }

    /**
     * Returns HashMap of all DegreeProgramme objects by name.
     *
     * @return HashMap of all DegreeProgramme objects by name.
     */
    public HashMap<String, DegreeProgramme> getDegreeProgrammesByName() {
        return degreeProgrammes;
    }
}
