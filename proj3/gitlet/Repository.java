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

    private static final String COMMIT_DIR = "objects/commits/";
    private static final String BLOB_DIR = "objects/blobs/";
    private static final String REFHEAD_DIR = "refs/heads/";
    private static final String INDEX = "index";
    private static final String HEAD = "HEAD";
    private static final String REFS_DIR = "refs/";

    /**
     * If the Repository is open and "on."
     */
    private boolean open = false;

    /**
     * All loaded objects.
     */
    private HashMap<String, Serializable> loadedObjects;

    /**
     * The working path.
     */
    private Path workingDir;

    /**
     * The gitlet directory.
     */
    private Path gitletDir;
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
        this.name = name;

        if (Files.exists(gitletDir))
            open();

        loadedObjects = new HashMap<>();
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
        if (isOpen())
            throw new IllegalStateException(
                    "A gitlet version-control system already exists in the current directory.");

        try {
            Files.createDirectory(gitletDir);
            String initialCommit = this.addCommit(
                    new Commit("initial commit", LocalDateTime.now()));
            this.addBranch("master", initialCommit);
            this.setBranch("master");
            this.setInitialCommit(initialCommit);

            this.loadedObjects.put(INDEX, new Index());

            open = true;
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

        Index index = getIndex();

        try {
            for (Path entry : Files.newDirectoryStream(getWorkingDir(),
                    x -> !Files.isDirectory(x))) {
                String name = entry.getFileName().toString();
                if (commit.getBlobs().containsKey(name)
                        && (!index.getBlobs().containsKey(name)
                                || index.getStaged().containsKey(name)))
                    throw new IllegalStateException("There is an untracked "
                            + "file in the way; delete it or add it first.");
            }

            for (Path entry : Files.newDirectoryStream(getWorkingDir(),
                    x -> !Files.isDirectory(x))) {
                String name = entry.getFileName().toString();

                if (index.getBlobs().containsKey(name))
                    Files.delete(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        commit.getBlobs().forEach((file, hash) -> {
            Blob blob = this.getBlob(hash);
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
        if (blobHash == null)
            throw new IllegalArgumentException(
                    "File does not exist in that commit.");

        Blob blob = this.getBlob(blobHash);
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
     * Adds a commit to the repository.
     * @param commit
     *            The commit.
     * @return
     */
    public String addCommit(Commit commit) {
        String hash = commit.sha1();
        this.loadedObjects.put(COMMIT_DIR + hash, commit);
        return hash;
    }

    /**
     * Adds a commit to the head.
     * @param commit
     *            The commit to add to the head.
     * @return The Sha-1 of the commit.
     */
    public String addCommitAtHead(String message,
            HashMap<String, String> blobs) {
        String headHash = getHead();
        LocalDateTime now = LocalDateTime.now();
        String commitHash =
                this.addCommit(new Commit(message, now, headHash, blobs));
        this.setHead(commitHash);
        return commitHash;
    }

    /**
     * Gets a commit with a specific hash.
     * @param hash
     *            Ther hash valie.
     * @return The commit.
     */
    public Commit getCommit(String hash) {
        return (Commit) this.load(COMMIT_DIR + hash);
    }

    /**
     * Applies a function to all commits.
     * @param func
     *            The function to apply/
     */
    public void applyToCommits(BiConsumer<String, Commit> func) {
        Path commitPath = gitletDir.resolve(COMMIT_DIR);
        try {
            for (Path entry : Files.newDirectoryStream(commitPath)) {
                String hash = entry.getFileName().toString();
                Commit commit = this.getCommit(hash);

                func.accept(hash, commit);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the first commit which satisfies the condition.
     * @param func
     * @return
     */
    public Commit firstCommitWhere(Predicate<String> func) {
        Path commitPath = gitletDir.resolve(COMMIT_DIR);
        try {
            for (Path entry : Files.newDirectoryStream(commitPath)) {
                String hash = entry.getFileName().toString();

                if (func.test(hash)) {
                    return this.getCommit(hash);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Adds a blob to the store.
     * @param blob
     *            The blob to add.
     * @return The hash of the blob.
     */
    public String addBlob(Blob blob) {
        String hash = blob.sha1();
        if (!this.loadedObjects.containsKey(BLOB_DIR + hash))
            this.loadedObjects.put(BLOB_DIR + hash, blob);
        return hash;
    }

    /** Gets a blob with a specific hash */
    public Blob getBlob(String hash) {
        return (Blob) this.load(BLOB_DIR + hash);
    }

    /**
     * Applies a function to all blobs.
     * @param func
     *            The function to apply/
     */
    public void applyToBlobs(BiConsumer<String, Blob> func) {
        Path commitPath = gitletDir.resolve(BLOB_DIR);
        try {
            for (Path entry : Files.newDirectoryStream(commitPath)) {
                String hash = entry.getFileName().toString();
                Blob commit = this.getBlob(hash);

                func.accept(hash, commit);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the first blob which satisfies the condition.
     * @param func
     * @return
     */
    public Blob firstBlobWhere(Predicate<String> func) {
        Path commitPath = gitletDir.resolve(BLOB_DIR);
        try {
            for (Path entry : Files.newDirectoryStream(commitPath)) {
                String hash = entry.getFileName().toString();

                if (func.test(hash))
                    return this.getBlob(hash);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the index.
     * @return The index.
     */
    public Index getIndex() {
        return (Index) this.load(INDEX);
    }

    /**
     * Gets the head commit.
     * @return The hash for the head commit.
     */
    public String getHead() {
        String curBranch = getBranch();
        try {
            Path branchPath = gitletDir.resolve(REFHEAD_DIR + curBranch);
            return Files.readAllLines(branchPath).get(0);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Sets the head commit. */
    public void setHead(String commitHash) {
        String curBranch = getBranch();
        try {
            Path branchPath = gitletDir.resolve(REFHEAD_DIR + curBranch);
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
            Path headPath = gitletDir.resolve(HEAD);
            String branchUri = Files.readAllLines(headPath).get(0);
            Path branchPath = gitletDir.resolve(branchUri);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Sets the current branch in the head.`
     * @param branch
     */
    public void setBranch(String branch) {
        try {
            Path headPath = gitletDir.resolve(HEAD);
            Path branchPath = gitletDir.resolve(REFHEAD_DIR + branch);
            if (!Files.exists(branchPath))
                throw new IllegalStateException("No such branch exists.");

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
            Path branchPath = gitletDir.resolve(REFHEAD_DIR + branch);
            if (!Files.exists(branchPath))
                throw new IllegalStateException("No such branch exists.");

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
            Path branchPath = gitletDir.resolve(REFHEAD_DIR + name);
            if (!Files.exists(branchPath.getParent()))
                Files.createDirectories(branchPath.getParent());

            if (Files.exists(branchPath))
                throw new IllegalArgumentException(
                        "A branch with that name already exists.");

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
            Path branchPath = gitletDir.resolve(REFHEAD_DIR + branch);
            if (!Files.exists(branchPath.getParent()))
                Files.createDirectories(branchPath.getParent());

            if (!Files.exists(branchPath))
                throw new IllegalArgumentException(
                        "A branch with that name does not exist.");

            Files.delete(branchPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an object into the lazy cache.
     * @param file
     * @return
     */
    private Serializable load(String file) {
        if (loadedObjects.containsKey(file))
            return loadedObjects.get(file);

        Path filePath = gitletDir.resolve(file);
        try {
            InputStream fin = Files.newInputStream(filePath);
            ObjectInputStream oin = new ObjectInputStream(fin);
            Serializable loaded = (Serializable) oin.readObject();
            oin.close();
            fin.close();

            this.loadedObjects.put(file, loaded);
            return loaded;
        } catch (IOException i) {
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Saves a gitlit object.
     * @param file
     *            The file name/relative path.
     * @param object
     *            The object to save.
     */
    private void save(String file, Serializable object) {
        Path filePath = gitletDir.resolve(file);
        try {
            if (!Files.exists(filePath.getParent()))
                Files.createDirectories(filePath.getParent());
            OutputStream fin = Files.newOutputStream(filePath);
            ObjectOutputStream oin = new ObjectOutputStream(fin);
            oin.writeObject(object);
            oin.close();
            fin.close();

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Opens a repository if the repository failed to open in the first place.
     */
    public void open() {
        if (isOpen())
            throw new IllegalStateException(
                    "Close repository before opening a new instance.");
        this.open = true;
    }

    /**
     * Closes a repository and serializes every loaded object.
     */
    public void close() {
        if (this.isOpen()) {
            this.open = false;

            for (Map.Entry<String, Serializable> loaded : loadedObjects
                    .entrySet()) {
                this.save(loaded.getKey(), loaded.getValue());
            }
        }
    }

    /** Returns if the repository has been opened. */
    public boolean isOpen() {
        return open;
    }

    /** Gets the working directory */
    public Path getWorkingDir() {
        return workingDir;
    }

    /** Gets the gitlet dir. */
    public Path getGitletDir() {
        return gitletDir;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public String initialCommit() {
        try {
            Path branchPath = gitletDir.resolve(REFS_DIR + "init");
            return Files.readAllLines(branchPath).get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void setInitialCommit(String hash){
        try {
            Path branchPath = gitletDir.resolve(REFS_DIR + "init");

             Files.write(branchPath, hash.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
