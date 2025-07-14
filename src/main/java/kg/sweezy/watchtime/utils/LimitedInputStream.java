package kg.sweezy.watchtime.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {
    private final InputStream file;
    private Long limit;

    public LimitedInputStream(InputStream file, Long limit) {
        this.file = file;
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        if(limit <= 0) return -1;
        int result = file.read();
        if(result != -1) limit--;
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if(limit <= 0) return -1;
        len = (int) Math.min(len, limit);
        int result = file.read(b, off, len);
        if(result != -1) limit -= result;
        return result;
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
