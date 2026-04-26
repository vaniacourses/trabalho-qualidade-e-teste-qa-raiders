class ServletInputStreamMock extends javax.servlet.ServletInputStream {
    private BufferedReader reader;
    public ServletInputStreamMock(BufferedReader reader) { this.reader = reader; }
    @Override public int read() throws IOException { return reader.read(); }
    @Override public boolean isFinished() { return false; }
    @Override public boolean isReady() { return true; }
    @Override public void setReadListener(javax.servlet.ReadListener readListener) {}
}