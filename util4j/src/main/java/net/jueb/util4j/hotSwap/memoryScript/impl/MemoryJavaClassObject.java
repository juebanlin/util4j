package net.jueb.util4j.hotSwap.memoryScript.impl;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class MemoryJavaClassObject extends SimpleJavaFileObject {

    protected final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public MemoryJavaClassObject(String name, Kind kind) {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
    }

    public byte[] getBytes() {
        return stream.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return stream;
    }
}