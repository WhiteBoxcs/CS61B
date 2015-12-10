/**
 *
 */
package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author william
 */
public class AddCommand implements Command {

    /*
     * (non-Javadoc)
     * @see gitlet.Command#run(gitlet.Repository, java.lang.String[])
     */
    @Override
    public void run(Repository repo, String[] args) {
        String file = args[0];
        Path workingDir = repo.getWorkingDir();
        Path filePath = workingDir.resolve(file);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File does not exist.");
        }
        if (Files.isDirectory(filePath)) {
            throw new IllegalStateException("Cannot add a directory.");
        }

        try {
            Blob fileBlob = new Blob(Files.readAllBytes(filePath));

            String blobHash = repo.objects().put(fileBlob);
            Index index = repo.index();

            index.add(file, blobHash);
        } catch (IOException e) {
            e.printStackTrace();
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
