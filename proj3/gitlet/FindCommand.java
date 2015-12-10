/**
 *
 */
package gitlet;

/**
 * @author william
 */
public class FindCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(final Repository repo, String[] args) {
        final int[] iter = new int[] { 0 };
        repo.objects().forEach(Commit.class, (hash, com) -> {
                if (com.getMessage().equals(args[0])) {
                    iter[0]++;
                    System.out.println(hash);
                }
            });
        if (iter[0] == 0) {
            throw new IllegalArgumentException(
                    "Found no commit with that message.");
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
     * @see gitlet.Command#checkOperands(java.lang.String[])
     */
    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 1;
    }

}
