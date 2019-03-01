package tests;

import io.github.cdimascio.dotenv.DotEnvException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class JavaTests {
    private Map<String, String> envVars;

    @Before
    public void setUp() {
        envVars = new HashMap<String, String>();
        envVars.put("MY_TEST_EV1", "my test ev 1");
        envVars.put("MY_TEST_EV2", "my test ev 2");
        envVars.put("ABSENT_ENV_VARIABLE", null);
        envVars.put("WITHOUT_VALUE", "");
    }

    @Test(expected = DotEnvException.class)
    public void throwIfMalconfigured() {
        Dotenv.configure().load();
    }

    @Test(expected = DotEnvException.class)
    public void load() {
        Dotenv dotenv = Dotenv.load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }

        String envName = "ABSENT_ENV_VARIABLE";
        String defValue = "This is the default value";
        assertEquals(defValue, dotenv.get(envName, defValue));
        assertNull(dotenv.get(envName, defValue));
    }

    @Test
    public void iteratorOverDotenv() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();
        Iterator<Map.Entry<String,String>> iter = dotenv.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            assertEquals(dotenv.get(entry.getKey()), entry.getValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failToSetValueFromIterator() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();
        Iterator<Map.Entry<String,String>> iter = dotenv.iterator();
        while (iter.hasNext()) {
            Map.Entry<String,String> entry = iter.next();
            entry.setValue("This operation must fail");
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failToRemoveFromIterator() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();
        while (dotenv.iterator().hasNext()) {
            dotenv.iterator().remove();
        }
    }

    @Test
    public void configureWithIgnoreMalformed() {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMalformed()
            .load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void configureWithIgnoreMissingAndMalformed() {
        Dotenv dotenv = Dotenv.configure()
            .directory("/missing/dir")
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();
        assertNotNull(dotenv.get("PATH"));
    }
}
