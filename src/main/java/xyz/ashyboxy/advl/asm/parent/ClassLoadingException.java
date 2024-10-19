package xyz.ashyboxy.advl.asm.parent;

public class ClassLoadingException extends ClassNotFoundException {
    public ClassLoadingException() {
        super(null);
    }

    public ClassLoadingException(String s) {
        super(s, null);
    }

    public ClassLoadingException(String s, Throwable ex) {
        super(s, ex);
    }
}
