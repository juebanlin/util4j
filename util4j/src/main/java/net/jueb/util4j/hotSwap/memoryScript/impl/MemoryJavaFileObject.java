package net.jueb.util4j.hotSwap.memoryScript.impl;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class MemoryJavaFileObject extends SimpleJavaFileObject {

    private CharSequence content;

    public MemoryJavaFileObject(String className, CharSequence content) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content;
    }

}