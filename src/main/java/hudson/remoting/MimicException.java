package hudson.remoting;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Exception that prints like the specified exception.
 *
 * This is used to carry the diagnostic information to the other side of the channel
 * in situations where we cannot use class remoting.
 *
 * @author Kohsuke Kawaguchi
 * @see Capability#hasMimicException()
 * @deprecated Use {@link ProxyException} instead.
 */
@Deprecated
class MimicException extends Exception {
    private final String className;
    MimicException(Throwable cause) {
        super(cause.getMessage());
        className = cause.getClass().getName();
        setStackTrace(cause.getStackTrace());

        if (cause.getCause()!=null)
            initCause(new MimicException(cause.getCause()));
    }

    @Override
    public String toString() {
        String s = className;
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    @Nullable
    public static Throwable make(@Nonnull Channel ch, @CheckForNull Throwable cause) {
        if (cause == null)  return null;

        // make sure the remoting layer of the other end supports this
        if (ch.remoteCapability.hasMimicException())
            return new MimicException(cause);
        else
            return cause;
    }

    private static final long serialVersionUID = 1L;
}
