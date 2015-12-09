/**
 *
 */
package gitlet;

/**
 * @author william
 */
public interface Command {
    public abstract void run(Repository repo, String[] args);

    public boolean requiresRepo();

    public boolean checkOperands(String[] args);
}
