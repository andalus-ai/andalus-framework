package tech.kayys.wayang.spi.sandbox;

public enum FilesystemAccess {
    NONE,
    READ,
    WRITE,
    READ_WRITE;

    public boolean readable() {
        return this == READ || this == READ_WRITE;
    }

    public boolean writable() {
        return this == WRITE || this == READ_WRITE;
    }
}
