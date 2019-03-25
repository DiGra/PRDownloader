package com.downloader.internal.stream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

public class FileDownloadRandomAccessFile implements FileDownloadOutputStream {

    private final FilterOutputStream out;
    private final FileDescriptor fd;
    private final RandomAccessFile randomAccess;
    private Cipher cipher = null;

    private FileDownloadRandomAccessFile(File file, Cipher cipher) throws IOException {
        this.cipher = cipher;
        randomAccess = new RandomAccessFile(file, "rw");
        fd = randomAccess.getFD();
        if (cipher != null) {
            out = new CipherOutputStream(new FileOutputStream(file), cipher);
        } else {
            out = new BufferedOutputStream(new FileOutputStream(file));
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flushAndSync() throws IOException {
        out.flush();
        fd.sync();
    }

    @Override
    public void close() throws IOException {
        out.close();
        randomAccess.close();
    }

    @Override
    public void seek(long offset) throws IOException {
        randomAccess.seek(offset);
    }

    @Override
    public void setLength(long totalBytes) throws IOException {
        randomAccess.setLength(totalBytes);
    }

    public static FileDownloadOutputStream create(File file, Cipher cipher) throws IOException {
        return new FileDownloadRandomAccessFile(file, cipher);
    }

}
