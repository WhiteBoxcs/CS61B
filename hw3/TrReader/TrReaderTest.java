import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;

import ucb.junit.textui;

/** Cursory test of the TrReader class.
 *  @author Josh Hug
 *  @author P. N. Hilfinger
 */
public class TrReaderTest {

    /* This test reads in the source code for itself.
     * Then feeds it into TrReader. If it works, you get the
     * source for this test, but scrambled. */
    @Test
    public void testSource() throws IOException {
        Reader r = makeStringReader(new FileReader("TrReaderTest.java"), 4096);

        TrReader trR = new TrReader(r, "import jav.", "josh hug___");
        char[] cbuf = new char[250];

        assertEquals(250, trR.read(cbuf));
        String result = new String(cbuf);
        assertEquals(TRANSLATION.substring(0, 250), result);
    }

    /** Return a StringReader that contains the contents delivered by R,
     *  up to MAXSIZE characters.  All end-of-line sequences in the
     *  characters read are canonicalized to '\n' (this has an effect only
     *  on Windows). */
    private Reader makeStringReader(Reader r, int maxSize) throws IOException {
        char[] buf = new char[maxSize];
        r.read(buf);
        String result = new String(buf);
        return new StringReader(result.replace("\r\n", "\n"));
    }

    public static void main(String[] args) {
        System.exit(textui.runClasses(TrReaderTest.class));
    }

    static final String TRANSLATION =
        "import java.io.FileReader;\n"
        + "import java.io.Reader;\n"
        + "import java.io.IOException;\n"
        + "import java.util.Arrays;\n"
        + "\n"
        + "import org.junit.Test;\n"
        + "import static org.junit.Assert.*;\n"
        + "\n"
        + "import ucb.junit.textui;\n"
        + "\n"
        + "/** Cursory test of the TrReader class.\n"
        + " *  @author Josh Hug\n"
        + " *  @author P. N. Hilfinger\n";
}
