package Controllers;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.io.IOException;

public class ServletInputStreamMock extends ServletInputStream {
    private final BufferedReader reader;

    public ServletInputStreamMock(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public int read() throws IOException {
        return reader.read();
    }

    @Override
    public boolean isFinished() { return false; }

    @Override
    public boolean isReady() { return true; }

    @Override
    public void setReadListener(ReadListener readListener) {}
}