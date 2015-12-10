/**
 *
 */
package gitlet;

import static gitlet.ReferenceType.BRANCH;
import static gitlet.ReferenceType.HEAD;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author william
 */
public class StatusCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        String currentBranch = repo.refs().get(HEAD).target();
        System.out.println("=== Branches ===");

        repo.refs().forEach(BRANCH, (name, branch) -> {
                if (name.equals(currentBranch)) {
                    System.out.print('*');
                }
                System.out.println(name);
            });

        Index index = repo.index();
        Path workingDir = repo.getWorkingDir();

        System.out.println("\n=== Staged Files ===");
        index.getStaged().forEach((name, hash) -> System.out.println(name));

        System.out.println("\n=== Removed Files ===");
        index.getRemoved().forEach((name, hash) -> System.out.println(name));

        try {
            this.diff(index, workingDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Outputs the diff between the index and the working dir.
     * @param index The index.
     * @param workingDir The working ditrectory.
     */
    private void diff(Index index, Path workingDir) throws IOException {
        HashMap<String, String> curBlobs = new HashMap<String, String>();

        for (Path entry : Files.newDirectoryStream(workingDir)) {
            if (!Files.isDirectory(entry)) {
                String name = entry.getFileName().toString();
                Blob entryBlob = new Blob(Files.readAllBytes(entry));

                curBlobs.put(name, entryBlob.sha1());
            }
        }

        List<String> untracked = new ArrayList<>();
        List<String> notStaged = new ArrayList<>();

        curBlobs.forEach((name, hash) -> {
                if (!index.getBlobs().containsKey(name)) {
                    untracked.add(name);
                } else if (!index.getBlobs().get(name).equals(hash)) {
                    notStaged.add(name + " (modified)");
                }
            });

        index.getBlobs().forEach((name, hash) -> {
                if (!curBlobs.containsKey(name)) {
                    notStaged.add(name + " (deleted)");
                }
            });

        notStaged.sort(String::compareTo);
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        notStaged.forEach(x -> System.out.println(x));

        untracked.sort(String::compareTo);
        System.out.println("\n=== Untracked Files ===");
        untracked.forEach(x -> System.out.println(x));
        System.out.println("");
    }

    /*
     * (non-Javadoc)
     * @see gitlet.Command#requiresRepo()
     */
    @Override
    public boolean requiresRepo() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see gitlet.Command#checkOperands(java.lang.String[])
     */
    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 0;
    }

}
