package gitlet;

import static gitlet.ReferenceType.HEAD;
import static gitlet.ReferenceType.TAG;
import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ucb.junit.textui;

/**
 * The suite of all JUnit tests for the gitlet package.
 * @author
 */
public class UnitTest {
    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to the
     * arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    static TempDirectory tempDir;
    private Repository repo;

    @BeforeClass
    public static void setUpAll() {
        tempDir = new TempDirectory(Paths.get(System.getProperty("user.dir")),
                "gitletTest");
        tempDir.deleteOnExit();
    }

    @Before
    public void setUp() {
        this.repo = new Repository(tempDir.getPath().toString());
        if (!this.repo.isOpen()) {
            this.repo.init();
        }
    }

    @After
    public void takeDown() {
        this.repo.close();
    }

    @Test
    public void refsTest() {
        HashMap<String, String> firstBlobs = new HashMap<>();
        firstBlobs.put("lol.file", "a");
        firstBlobs.put("friend.file", "b");

        assertEquals(this.repo.refs().resolve(TAG, "initial"),
                this.repo.getCurrentBranch().target());
        assertEquals(this.repo.refs().resolve(TAG, "initial"),
                this.repo.refs().resolve(HEAD));

        String root = this.repo.addCommitAtHead("ROOT!", firstBlobs);
        assertEquals(root, this.repo.refs().resolve(HEAD));
    }

    @Test
    public void objTest() {

    }

    /**
     * Tests for a split point.
     */
    @Test
    public void splitpointTest() {

        HashMap<String, String> firstBlobs = new HashMap<>();
        firstBlobs.put("lol.file", "a");
        firstBlobs.put("friend.file", "b");

        String root = this.repo.addCommitAtHead("ROOT!", firstBlobs);
        String prev = root;
        String left = "";
        for (int i = 0; i < 40; i++) {
            Commit com = new Commit("LEFT" + i, LocalDateTime.now(), prev,
                    firstBlobs);
            left = prev = this.repo.objects().put(com);
        }

        prev = root;
        String right = "";
        for (int i = 0; i < 28; i++) {
            Commit com = new Commit("RIGHT" + i, LocalDateTime.now(), prev,
                    firstBlobs);
            right = prev = this.repo.objects().put(com);
        }

        assertEquals(root, MergeCommand.getSplitPoint(this.repo, left, right));

        prev = "";
        left = "";
        for (int i = 0; i < 40; i++) {
            Commit com = new Commit("LEFT" + i, LocalDateTime.now(), prev,
                    firstBlobs);
            left = prev = this.repo.objects().put(com);
        }

        prev = "";
        right = "";
        for (int i = 0; i < 27; i++) {
            Commit com = new Commit("RIGHT" + i, LocalDateTime.now(), prev,
                    firstBlobs);
            right = prev = this.repo.objects().put(com);
        }

        assertEquals("", MergeCommand.getSplitPoint(this.repo, left, right));

    }

}
