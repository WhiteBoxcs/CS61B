/**
 *
 */
package gitlet;

import static gitlet.ReferenceType.HEAD;

/**
 * @author william
 */
public class LsCommitCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        Commit commit;
        if (args.length == 0) {
            commit = repo.objects().get(Commit.class,
                    repo.refs().resolve(HEAD));
        } else {
            commit = repo.objects().find(Commit.class, args[0]);
        }
        if (commit == null) {
            throw new IllegalArgumentException("No such commit exists.");
        }

        System.out.println(commit.toString());
        commit.forEach((name, hash) -> {
            System.out.println(name + "\t" + hash);
        });

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
        return args.length == 1 || args.length == 0;
    }

}
