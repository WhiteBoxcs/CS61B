/**
 *
 */
package gitlet;

import static gitlet.ReferenceType.BRANCH;
import static gitlet.ReferenceType.HEAD;
import static gitlet.ReferenceType.TAG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author william
 */
public class MergeCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        merge(repo, args[0]);
    }

    /**
     * Merges two commits from different repositories.
     * @param The
     *            repo.
     * @param commitB
     *            The commit from which the content will be merged into TO.
     */
    public static void merge(Repository repo, String branch) {
        if (branch.equals(repo.getCurrentBranch())) {
            throw new IllegalArgumentException(
                    "Cannot merge a branch with itself.");
        }
        if (repo.index().isChanged()) {
            throw new IllegalStateException("You have uncommitted changes.");
        }

        String otherHash = repo.refs().resolve(BRANCH, branch);
        String headHash = repo.refs().resolve(HEAD);

        String splitHash = getSplitPoint(repo, headHash, otherHash);

        if (splitHash.equals(otherHash)) {
            throw new IllegalStateException(
                    "Given branch is an ancestor of the current branch.");
        }
        if (splitHash.equals(headHash)) {
            ResetCommand.reset(repo, otherHash);
            System.out.println("Current branch fast-forwarded.");
        }
        if (splitHash.isEmpty()) {
            splitHash = repo.refs().resolve(TAG, "initial");
        }

        Commit split = repo.objects().get(Commit.class, splitHash);
        Commit head = repo.objects().get(Commit.class, headHash);
        Commit other = repo.objects().get(Commit.class, otherHash);

        boolean conflicts = mergeCompare(repo, head, other, split);
        if (!conflicts) {
            repo.addCommitAtHead("Merged " + repo.refs().get(HEAD).target()
                    + " with " + branch + ".", repo.index().blobsFromStage());
        } else {
            throw new IllegalStateException("Encountered a merge conflict.");
        }
    }

    /**
     * Handles the actual checkout of the merge.
     * @param repo
     *            The repository.
     * @param head
     *            The head.
     * @param other
     *            The other.
     * @param split
     *            The split.
     * @return if there was a conflict.
     */
    private static boolean mergeCompare(Repository repo, Commit head,
            Commit other, Commit split) {

        List<String> toCheckout = new ArrayList<String>();
        List<String> toRemove = new ArrayList<String>();
        List<String> inConflict = new ArrayList<String>();

        other.forEach((file, otherHash) -> {
                String splitHash = split.get(file);
                String headHash = head.get(file);
    
                if (splitHash == null) {
                    if (headHash == null) {
                        toCheckout.add(file);
                    } else if (!headHash.equals(otherHash)) {
                        inConflict.add(file);
                    }
                } else if (!otherHash.equals(headHash)) {
                    if (headHash == null) {
                        if (!otherHash.equals(splitHash)) {
                            inConflict.add(file);
                        }
                    } else if (headHash.equals(splitHash)) {
                        toCheckout.add(file);
                    } else if (!otherHash.equals(headHash)) {
                        inConflict.add(file);
                    }
                }
            });

        head.forEach((file, headHash) -> {
                String splitHash = split.get(file);
                String otherHash = other.get(file);
                if (splitHash != null && otherHash == null) {
                    if (headHash.equals(splitHash)) {
                        toRemove.add(file);
                    } else {
                        inConflict.add(file);
                    }
                }
            });

        mergeCheckout(repo, other, toCheckout);
        mergeRemove(repo, head, toRemove);
        mergeConflict(repo, head, other, inConflict);

        return !inConflict.isEmpty();
    }

    /**
     * Checks out all files that mergeCompare deems mergable.
     * @param repo
     *            The repository.
     * @param other
     *            The commit from which to checkout.
     * @param toCheckout
     *            The files to checkout.
     */
    private static void mergeCheckout(Repository repo, Commit other,
            Collection<String> toCheckout) {
        Path workingDir = repo.getWorkingDir();
        Index index = repo.index();

        for (String file : toCheckout) {
            if (Files.exists(workingDir.resolve(file))
                    && !index.getBlobs().containsKey(file)) {
                throw new IllegalStateException("There is an untracked "
                        + "file in the way; delete it or add it first.");
            }
        }

        toCheckout.forEach(x -> repo.checkout(other, x, true));
    }

    /**
     * Removes all files from the repository which the mergeCompare deems
     * removable.
     * @param repo
     * @param head
     * @param other
     * @param split
     * @param toRemove
     */
    private static void mergeRemove(Repository repo, Commit head,
            Collection<String> toRemove) {
        toRemove.forEach(x -> RemoveCommand.remove(repo, x, head));
    }

    /**
     * Merges the conflicts by displaying their differences.
     * @param repo
     *            The repository.
     * @param head
     *            The head.
     * @param other
     *            The other.
     * @param inConflict
     *            The files in conflict.
     */
    private static void mergeConflict(Repository repo, Commit head,
            Commit other, Collection<String> inConflict) {
        Index index = repo.index();
        for (String file : inConflict) {
            Path filePath = repo.getWorkingDir().resolve(file);

            try {
                Files.write(filePath, "<<<<<<< HEAD\n".getBytes());

                if (head.containsKey(file)) {
                    Blob headVersion = repo.objects().get(Blob.class,
                            head.getBlobs().get(file));
                    Files.write(filePath, headVersion.getContents(),
                            StandardOpenOption.APPEND);
                }

                Files.write(filePath, "=======\n".getBytes(),
                        StandardOpenOption.APPEND);

                if (other.getBlobs().containsKey(file)) {
                    Blob otherVersion = repo.objects().get(Blob.class,
                            other.getBlobs().get(file));
                    Files.write(filePath, otherVersion.getContents(),
                            StandardOpenOption.APPEND);
                }

                Files.write(filePath, ">>>>>>>\n".getBytes(),
                        StandardOpenOption.APPEND);

                // unstage the file.
                if (index.getBlobs().containsKey(file)) {
                    index.unstage(file);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Gets the split point.
     * @param repo
     *            The repository.
     * @param a
     *            The first commit.
     * @param b
     *            The second commmit.
     * @return The splitpoint commit.
     */
    public static String getSplitPoint(Repository repoA, String a, String b) {
        if (b.equals(a)) {
            return a;
        }

        List<String> aHistory =
                (List<String>) getHistory(repoA, a, new ArrayList<String>());
        Collection<String> bHistory =
                getHistory(repoA, b, new HashSet<String>());

        aHistory.retainAll(bHistory);
        if (aHistory.isEmpty()) {
            return "";
        } else {
            return aHistory.get(0);
        }
    }

    /**
     * Gets the commit history from a starting point.
     * @param repo
     * @param start
     * @param history
     * @return
     */
    public static Collection<String> getHistory(Repository repo, String start,
            Collection<String> history) {
        String cur = start;
        while (!cur.isEmpty()) {
            history.add(cur);
            cur = repo.objects().get(Commit.class, cur).getParent();
        }
        return history;
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
        return args.length == 1;
    }

}
