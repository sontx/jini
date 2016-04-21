package com.blogspot.sontx.jini;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 21/4/2016.
 */
public class INIFile {
    public static final String DELIM = "=";
    private File file;
    private Charset charset;
    private INIMemory memory = new INIMemory();

    public INIFile(String filePath) throws IOException, INIBadFormatException {
        this(filePath, Charset.defaultCharset());
    }

    public INIFile(String filePath, @NotNull Charset charset) throws IOException, INIBadFormatException {
        this.file = new File(filePath);
        this.charset = charset;
        if (this.file.isFile())
            importFromFile();
    }

    public void put(String key, String value) throws IOException {
        synchronized (this) {
            memory.put(key, value);
            exportToFile();
        }
    }

    public String get(String key) {
        synchronized (this) {
            return memory.get(key);
        }
    }

    public void remove(String key) throws IOException {
        synchronized (this) {
            memory.remove(key);
            exportToFile();
        }
    }

    public void clear() {
        synchronized (this) {
            memory.clear();
            file.delete();
        }
    }

    private void exportToFile() throws IOException {
        if (file.exists())
            file.delete();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
        Iterable<String> keys = memory.getKeys();
        String pattern = "%s" + DELIM + "%s";
        try {
            for (String key : keys) {
                writer.write(String.format(pattern, key, memory.get(key)));
                writer.newLine();
            }
        } finally {
            try {
                writer.close();
            } catch (IOException e) {}
        }
    }

    private void importFromFile() throws IOException, INIBadFormatException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        memory.clear();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!"".equals(line = line.trim()))
                    processLine(line);
            }
        } finally {
            try {
                reader.close();
            } catch (IOException e) {}
        }
    }

    private void processLine(String line) throws INIBadFormatException {
        if (line == null)
            throw new INIBadFormatException("Line is NULL");
        line = line.trim();
        if (line.length() == 0)
            throw new INIBadFormatException("Line is Empty");
        int delimIndex = line.indexOf(DELIM);
        if (delimIndex < 1)
            throw new INIBadFormatException(String.format("Pair key and value is invalid: %s", line));
        String key = line.substring(0, delimIndex).trim();
        String value = line.substring(delimIndex + 1).trim();
        memory.put(key, value);
    }
}
