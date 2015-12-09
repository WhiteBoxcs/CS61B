package gitlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a Gitlet repository.
 * @author william
 */
public class Repository {

    private static final String REFHEAD_DIR = "refs/heads/";
    private static final String INDEX = "index";
    private static final String HEAD = "HEAD";
    private static final String REFS_DIR = "refs/";

    /**
     * If the Repository is open and "on."
     */
    private boolean open = false;

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
    
    
    private String name;

    /**
     * Declares a repository at the workingDIR.
     * @param workingDir
     *            The working dir.
     * @param name
     *            the name.
     */
    public Repository(String name, String workingDir) {
        this.workingDir = Paths.get(workingDir);
        this.gitletDir = this.workingDir.resolve(".gitlet");
        objectMan = new GitletObjectManager(gitletDir);
        this.name = name;

        if (Files.exists(this.gitletDir)) {
            this.open();
        }

    }

    /**
     * Declares a repository at the workingDIR.
     * @param workingDir
     *            The working dir.
     */
    public Repository(String workingDir) {
        this("local", workingDir);
    }

    /**
     * Initializes a repository if one does not already exist there.
     */
    public void init() {
        if (this.isOpen()) {
            throw new IllegalStateException(
                    "A gitlet version-control system already exists in the current directory.");
        }

        try {
            Files.createDirectory(this.gitletDir);
            objectMan.open();
            
            String initialCommit = this.objects().add(
                    new Commit("initial commit", LocalDateTime.now()));
            this.addBranch("master", initialCommit);
            this.setBranch("master");
            this.setInitialCommit(initialCommit);

            this.objects().add(INDEX, new Index());

            this.open = true;
        } catch (IOException e) {
            System.out.println(
                    "Something went wrong while initializing the repository!");
            e.printStackTrace();
        }

    }

    /**
     * Checks out a given commit.
     * @param commit
     *            The commit to checkout.
     */
    public void checkout(Commit commit) {

        Index index = this.getIndex();

        try {
            for (Path entry : Files.newDirectoryStream(this.getWorkingDir(),
                    x -> !Files.isDirectory(x))) {
                String name = entry.getFileName().toString();

                if (commit.getBlobs().containsKey(name)
                        && (!index.getBlobs().containsKey(name)
                                || index.getStaged().containsKey(name))) {
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
        String blobHash = commit.getBlobs().get(filename);
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
        Index index = this.getIndex();
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
        String headHash = this.getHead();
        LocalDateTime now = LocalDateTime.now();
        String commitHash =
                this.objects().add(new Commit(message, now, headHash, blobs));
        this.setHead(commitHash);
        return commitHash;
    }


    /**
     * Gets the index.
     * @return The index.
     */
    public Index getIndex() {
        return (Index) this.objects().getDirect(Index.class, INDEX);
    }

    /**
     * Gets the head commit.
     * @return The hash for the head commit.
     */
    public String getHead() {
        String curBranch = this.getBranch();
        try {
            Path branchPath = this.gitletDir.resolve(REFHEAD_DIR + curBranch);
            return Files.readAllLines(branchPath).get(0);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Sets the head commit. */
    public void setHead(String commitHash) {
        String curBranch = this.getBranch();
        try {
            Path branchPath = this.gitletDir.resolve(REFHEAD_DIR + curBranch);
            Files.write(branchPath, commitHash.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current branch if it exists.
     * @return The name of the current branch.
     */
    public String getBranch() {
        try {
            Path headPath = this.gitletDir.resolve(HEAD);
            String branchUri = Files.readAllLines(headPath).get(0);
            Path branchPath = this.gitletDir.resolve(branchUri);
            if (!Files.exists(branchPath)) {
                throw new IllegalStateException(
                        "Current branch does not exist.");
            }
            return branchPath.getFileName().toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Applies a functional to all of the branches.
     * @param func
     */
    public void applyToBranches(Consumer<String> func) {
        Path branchDir = this.gitletDir.resolve(REFHEAD_DIR);
        try {
            DirectoryStream<Path> contents =
                    Files.newDirectoryStream(branchDir);

            for (Path entry : contents) {
                func.accept(entry.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the current branch in the head.`
     * @param branch
     */
    public void setBranch(String branch) {
        try {
            Path headPath = this.gitletDir.resolve(HEAD);
            Path branchPath = this.gitletDir.resolve(REFHEAD_DIR + branch);
            if (!Files.exists(branchPath)) {
                throw new IllegalStateException("No such branch exists.");
            }

            Files.write(headPath, (REFHEAD_DIR + branch).getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the branch head.
     * @param branch
     *            The branch.
     * @return The branch head.
     */
    public String getBranchHead(String branch) {
        try {
            Path branchPath = this.gitletDir.resolve(REFHEAD_DIR + branch);
            if (!Files.exists(branchPath)) {
                throw new IllegalStateException("No such branch exists.");
            }

            return Files.readAllLines(branchPath).get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public void removeBranch(String branch) {
        try {
            Path branchPath = this.gitletDir.resolve(REFHEAD_DIR + branch);
            if (!Files.exists(branchPath.getParent())) {
                Files.createDirectories(branchPath.getParent());
            }

            if (!Files.exists(branchPath)) {
                throw new IllegalArgumentException(
                        "A branch with that name does not exist.");
            }

            Files.delete(branchPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a repository if the repository failed to open in the first place.
     */
    public void open() {
        if (this.isOpen()) {
            throw new IllegalStateException(
                    "Close repository before opening a new instance.");
        }
        this.open = true;
        objectMan.open();
        
    }

    /**
     * Closes a repository and serializes every loaded object.
     */
    public void close() {
        if (this.isOpen()) {
            this.open = false;
            objectMan.close();
        }
    }

    /** Returns if the repository has been opened. */
    public boolean isOpen() {
        return this.open;
    }
    
    /** Gets the working directory */
    public Path getWorkingDir() {
        return this.workingDir;
    }

    /** Gets the gitlet dir. */
    public Path getGitletDir() {
        return this.gitletDir;
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
    
    /**
     * Gets the objects in the repository.
     * @return The manager which holds the objects.
     */
    public GitletObjectManager objects(){
        return objectMan;
    }

}
