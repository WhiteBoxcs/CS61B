package gitlet;

import static gitlet.ReferenceType.BRANCH;
import static gitlet.ReferenceType.HEAD;
import static gitlet.ReferenceType.TAG;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Represents a Gitlet repository.
 * @author william
 */
public class Repository extends LazySerialManager<Serializable> {
    private static final String GITLET_DIR = ".gitlet";
    private static final String INDEX = "index";
    private static final String OBJ_DIR = "objects/";
    private static final String REFS_DIR = "refs/";

    /**
     * The working path.
     */
    private Path workingDir;

    /**
     * The gitlet directory.
     */
    private Path gitletDir;

    /**
     * Manages all objects in the repository.
     */
    private GitletObjectManager objectMan;

    /**
     * Manages all of the references.
     */
    private ReferenceManager refMan;

    /**
     * Declares a repository at the workingDIR.
     * @param workingDir
     *            The working dir.
     * @param name
     *            the name.
     */
    public Repository(String workingDir) {
        super(Paths.get(workingDir).resolve(GITLET_DIR));
        this.workingDir = this.getBaseDirectory().getParent();
        this.gitletDir = this.getBaseDirectory();

        this.objectMan =
                new GitletObjectManager(this.gitletDir.resolve(OBJ_DIR));
        this.refMan = new ReferenceManager(this.gitletDir.resolve(REFS_DIR));

        if (Files.exists(this.gitletDir)) {
            this.open();
        }

    }

    /**
     * Gets the index.
     * @return The index.
     */
    public Index index() {
        return this.get(Index.class, INDEX);
    }

    /**
     * Gets the objects in the repository.
     * @return The manager which holds the objects.
     */
    public GitletObjectManager objects() {
        return this.objectMan;
    }

    /**
     * Gets the reference ma nager.
     */
    public ReferenceManager refs() {
        return this.refMan;
    }

    /**
     * Initializes a repository if one does not already exist there.
     */
    public void init() {
        if (this.isOpen()) {
            throw new IllegalStateException(
                    "A gitlet version-control system already exists in the current directory.");
        }

        super.open();
        this.objectMan.open();
        this.refMan.open();

        String initialCommit = this.objects()
                .put(new Commit("initial commit", LocalDateTime.now()));

        this.refs().add(BRANCH, "master", new Reference(initialCommit));
        this.refs().add(HEAD, new Reference(BRANCH, "master"));
        this.refs().add(TAG, "initial", new Reference(initialCommit));

        this.add(INDEX, new Index());

    }

    /**
     * Checks out a given commit.
     * @param commit
     *            The commit to checkout.
     */
    public void checkout(Commit commit) {

        Index index = this.index();

        try {
            for (Path entry : Files.newDirectoryStream(this.getWorkingDir(),
                    x -> !Files.isDirectory(x))) {
                String fileName = entry.getFileName().toString();

                if (commit.containsKey(fileName)
                        && (!index.getBlobs().containsKey(fileName)
                                || index.getStaged().containsKey(fileName))) {
                    throw new IllegalStateException("There is an untracked "
                            + "file in the way; delete it or add it first.");
                }
            }

            for (Path entry : Files.newDirectoryStream(this.getWorkingDir(),
                    x -> !Files.isDirectory(x))) {
                String name = entry.getFileName().toString();

                if (index.getBlobs().containsKey(name)) {
                    Files.delete(entry);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        commit.getBlobs().forEach((file, hash) -> {
                Blob blob = this.objects().get(Blob.class, hash);
                Path filePath = this.getWorkingDir().resolve(file);
                try {
                    Files.write(filePath, blob.getContents());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        index.checkout(commit);

    }

    /**
     * Checks out a given file of a commit.
     * @param commit
     * @param filename
     */
    public void checkout(Commit commit, String filename, boolean stage) {
        String blobHash = commit.get(filename);
        if (blobHash == null) {
            throw new IllegalArgumentException(
                    "File does not exist in that commit.");
        }

        Blob blob = this.objects().get(Blob.class, blobHash);
        Path filePath = this.getWorkingDir().resolve(filename);
        try {
            Files.write(filePath, blob.getContents());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Index index = this.index();
        index.checkout(filename, blobHash, stage);
    }

    /**
     * Adds a commit to the head.
     * @param commit
     *            The commit to add to the head.
     * @return The Sha-1 of the commit.
     */
    public String addCommitAtHead(String message,
            HashMap<String, String> blobs) {
        String headHash = this.refs().resolve(HEAD);
        LocalDateTime now = LocalDateTime.now();
        String commitHash =
                this.objects().put(new Commit(message, now, headHash, blobs));

        this.getCurrentBranch().setTarget(commitHash);
        return commitHash;
    }

    /**
     * Gets the head commit.
     * @return The hash for the head commit.
     */
    public Reference getCurrentBranch() {
        Reference head = this.refs().get(HEAD);
        return this.refs().get(BRANCH, head.target());
    }

    /**
     * Sets the current branch in the head.`
     * @param branch
     */
    public void setCurrentBranch(String branch) {
        this.refs().get(BRANCH, branch);
        this.refs().get(HEAD).setTarget(branch);
    }

    /**
     * Opens a repository if the repository failed to open in the first place.
     */
    @Override
    public void open() {
        super.open();
        this.refMan.open();
        this.objectMan.open();

    }

    /**
     * Closes a repository and serializes every loaded object.
     */
    @Override
    public void close() {
        super.close();
        this.refMan.close();
        this.objectMan.close();
    }

    /** Gets the working directory */
    public Path getWorkingDir() {
        return this.workingDir;
    }

    @Override
    protected boolean niceSerialization() {
        return false;
    }

}
