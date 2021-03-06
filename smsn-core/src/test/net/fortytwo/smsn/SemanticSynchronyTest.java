package net.fortytwo.smsn;

import net.fortytwo.smsn.config.Configuration;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SemanticSynchronyTest {
    private static final Pattern NEW_ID_PATTERN = Pattern.compile("[a-zA-Z0-9]{16}");

    @Test
    public void testConfiguration() throws Exception {
        Configuration conf = SemanticSynchrony.getConfiguration();

        assertEquals("http://example.org/things/", conf.getThingNamespace());

        assertEquals("private", conf.getSources().get(0).getName());
        assertEquals("personal", conf.getSources().get(1).getName());
        assertEquals("public", conf.getSources().get(2).getName());
        assertEquals("universal", conf.getSources().get(3).getName());

        assertEquals(0xff0000, (int) conf.getSources().get(0).getColor());
        assertEquals(0xffc000, (int) conf.getSources().get(1).getColor());
        assertEquals(0x00e000, (int) conf.getSources().get(2).getColor());
        assertEquals(0x0000ff, (int) conf.getSources().get(3).getColor());
    }
    
    @Test
    public void testVersionInfo() throws Exception {
        String version = SemanticSynchrony.getConfiguration().getVersion();
        int i = version.indexOf('.');
        int majorVersion = Integer.valueOf(version.substring(0, i));
        assertTrue(majorVersion >= 1);
    }

    @Test
    public void testUnicodeEscape() throws Exception {
        StringBuilder sb;

        sb = new StringBuilder();
        sb.append((char) 0);
        assertEquals("\\u0000", SemanticSynchrony.unicodeEscape(sb.toString()));

        sb = new StringBuilder();
        sb.append("the number ");
        sb.append((char) 4);
        sb.append((char) 2);
        sb.append("...");
        sb.append((char) 127);
        sb.append((char) 1008);
        assertEquals("the number \\u0004\\u0002...\\u007F\\u03F0", SemanticSynchrony.unicodeEscape(sb.toString()));
    }

    @Test
    public void randomIdsHaveExpectedFormat() {
        Set<String> ids = new HashSet<>();
        int total = 100;
        for (int i = 0; i < total; i++) {
            String id = SemanticSynchrony.createRandomId();
            assertTrue(NEW_ID_PATTERN.matcher(id).matches());
            ids.add(id);
            //System.out.println(id);
        }

        // no duplicates
        assertEquals(total, ids.size());
    }

    @Test
    public void migratedIdsAreConsistent() {
        for (int i = 0; i < 3; i++) {
            assertMigratedIdEquals("eodxY8InPB5UYLqO", "9zVbkI_");
            assertMigratedIdEquals("LYkiOeeBZiDq718D", "ZGVYTCA");
            assertMigratedIdEquals("OhOt7yg71kBYkI22", "1HHHl9e");
        }
    }

    private void assertMigratedIdEquals(final String expected, final String original) {
        String actual = SemanticSynchrony.migrateId(original);
        assertEquals(expected, actual);
    }
}
