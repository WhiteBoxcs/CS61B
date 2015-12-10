/**
 *
 */
package gitlet;

import static gitlet.ReferenceType.HEAD;

/**
 * @author william Represents a log command which logs all of the commits
 *         starting from the head.
 */
public class LogCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        String commitHash = repo.refs().resolve(HEAD);
        while (commitHash != null && !commitHash.equals("")) {
            Commit commit = repo.objects().get(Commit.class, commitHash);
            System.out.println(commit.toString());
            commitHash = commit.getParent();
        }
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
     * @see gitlet.Command#checkOperands()
     */
    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 0;
    }

}
