package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import static gitlet.ReferenceType.*;

/**
 * Represents a Gitlet repository.
 * @author william
 */
public class Repository extends LazySerialManager<Serializable>{
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
        
        this.objectMan = new GitletObjectManager(this.gitletDir.resolve(OBJ_DIR));
        this.refMan = new ReferenceManager(this.gitletDir.resolve(REFS_DIR));

        if (Files.exists(this.gitletDir)) {
            this.open();
        }

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
                .add(new Commit("initial commit", LocalDateTime.now()));
        this.addBranch("master", initialCommit);
        this.setBranch("master");
        this.setInitialCommit(initialCommit);

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
                this.objects().add(new Commit(message, now, headHash, blobs));
        
        this.getCurrentBranch().setTarget(commitHash);
        return commitHash;
    }

    /**
     * Gets the index.
     * @return The index.
     */
    public Index index() {
        return this.get(Index.class, INDEX);
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
        this.refs().get(HEAD).setTarget(branch);
    }


    /**
     * Adds a branch to the reference store at the current head commit.
     * @param name
     *            The name of the branch
     * @param commit
     *            the commit.
     */
    public void addBranch(String name, String commit) {
        try {
            Path branchPath = this.gitletDir.resolve(REFHEAD_DIR + name);
            if (!Files.exists(branchPath.getParent())) {
                Files.createDirectories(branchPath.getParent());
            }

            if (Files.exists(branchPath)) {
                throw new IllegalArgumentException(
                        "A branch with that name already exists.");
            }

            Files.write(branchPath, commit.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a branch at the head.
     * @param name
     *            The name of the branch.
     */
    public void addBranch(String name) {
        this.addBranch(name, this.getHead());
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
    public ReferenceManager refs(){
        return this.refMan;
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

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    public String initialCommit() {
        try {
            Path branchPath = this.gitletDir.resolve(REFS_DIR + "init");
            return Files.readAllLines(branchPath).get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setInitialCommit(String hash) {
        try {
            Path branchPath = this.gitletDir.resolve(REFS_DIR + "init");

            Files.write(branchPath, hash.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
