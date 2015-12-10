/**
 *
 */
package gitlet;

import static gitlet.ReferenceType.REMOTE;

/**
 * @author william
 */
public class AddRemoteCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        String remoteName = args[0];
        String targetDir = args[1];

        repo.refs().add(REMOTE, remoteName, new Reference(targetDir));
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
        return args.length == 2;
    }

}
