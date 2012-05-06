package fi.local.social.network.activities;

import java.io.IOException;
import java.io.InputStream;

public final class ZeroAvailableInputStream extends InputStream {
    private final InputStream delegate;

    public ZeroAvailableInputStream(InputStream delegate) {
        this.delegate = delegate;
    }

    @Override public final int read() throws IOException {
        byte[] buffer = new byte[1];
        return read(buffer) == -1 ? -1 : buffer[0];
    }

    @Override public int read(byte[] buffer, int offset, int length) throws IOException {
        return delegate.read(buffer, offset, length);
    }

    @Override public void close() throws IOException {
        delegate.close();
    }

    @Override public int available() throws IOException {
        return 0;
    }
}