/**
 *
 */
package gitlet;

import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * @author william
 */
public class ReferenceManager extends LazySerialManager<Reference> {

    public ReferenceManager(Path base) {
        super(base);
    }

    public Reference add(ReferenceType type, String fileName, Reference ref) {
        try {
            this.add(type.getBaseDir() + fileName, ref);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(
                    "A " + type.toString().toLowerCase()
                            + " with that name already exists.");
        }
        return ref;
    }

    public Reference add(ReferenceType type, Reference ref) {
        return this.add(type, type.toString(), ref);
    }

    public Reference get(ReferenceType type, String fileName) {
        Reference ref =
                this.get(Reference.class, type.getBaseDir() + fileName);
        if (ref == null) {
            throw new IllegalStateException(
                    "No such " + type.toString().toLowerCase() + " exists.");
        }
        return ref;

    }

    public Reference get(ReferenceType type) {
        return this.get(type, type.toString());
    }

    /**
     * Determines if the reference manager contains a given file.
     * @param branch
     * @param name
     * @return
     */
    public boolean contains(ReferenceType branch, String name) {
        return this.contains(Reference.class, branch.getBaseDir() + name);
    }

    public String resolve(ReferenceType type, String fileName) {
        Reference cur = this.get(type, fileName);
        while (cur.targetIsReference()) {
            cur = this.get(cur.targetType(), cur.target());
        }

        return cur.target();

    }

    public String resolve(ReferenceType type) {
        return this.resolve(type, type.toString());
    }

    public void remove(ReferenceType type, String fileName) {
        try {
            this.remove(Reference.class, type.getBaseDir() + fileName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "A " + type.toString().toLowerCase()
                            + " with that name does not exist.");
        }
    }

    /**
     * Iterates over the references of a certain type within the manager.
     * @param type
     *            The type of reference.
     * @param action
     *            The action.
     */
    public void forEach(ReferenceType type,
            BiConsumer<? super String, Reference> action) {
        this.forEach(Reference.class, (file, ref) -> {
            if (file.startsWith(type.getBaseDir())) {
                action.accept(file.replace(type.getBaseDir(), ""), ref);
            }
        });
    }

    @Override
    protected boolean niceSerialization() {
        return false;
    }

}
