/**
 *
 */
package gitlet;

/**
 * @author william
 */
public class RemoveBranchCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        String branch = args[0];
        if (repo.getBranch().equals(branch)) {
            throw new IllegalArgumentException(
                    "Cannot remove the current branch.");
        }

        repo.removeBranch(branch);
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
