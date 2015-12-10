/**
 *
 */
package gitlet;

/**
 * @author william
 */
public class ResetCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        reset(repo, args[0]);
    }

    /**
     * Resets the repo to a given commit.
     * @param repo
     *            The repo.
     * @param commitHash
     *            The commit.
     */
    public static void reset(Repository repo, String commitHash) {
        Commit toCheck;
        if (commitHash.length() == 40) {
            toCheck = repo.objects().get(Commit.class, commitHash);
        } else {
            toCheck = repo.objects().find(Commit.class, commitHash);
        }

        if (toCheck == null) {
            throw new IllegalArgumentException(
                    "No commit with that id exists.");
        }

        repo.checkout(toCheck);
        repo.getCurrentBranch().setTarget(toCheck.sha1());
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
