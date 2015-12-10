/**
 *
 */
package gitlet;

import static gitlet.ReferenceType.HEAD;

import java.io.IOException;
import java.nio.file.Files;

/**
 * @author william
 */
public class RemoveCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        remove(repo, args[0],
                repo.objects().get(Commit.class, repo.refs().resolve(HEAD)));
    }

    public static void remove(Repository repo, String file, Commit head) {
        Index index = repo.index();

        if (head.containsKey(file)) {
            try {
                index.remove(file, true);
                if (Files.exists(repo.getWorkingDir().resolve(file))) {
                    Files.delete(repo.getWorkingDir().resolve(file));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            index.remove(file, false);
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
