package com.dz.broker;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonStreamReader extends Reader {
    private final String source;
    private long lastModification = -1;

    public JsonStreamReader(String source) {
        this.source = source;
    }

    @Override
    public void read() throws IOException {
        InputStream inputStream;
        try {
            URL url = new URL(source);

            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            long lastModified = httpCon.getLastModified();
            if (lastModified == this.lastModification) {
                return;
            }
            this.lastModification = lastModified;
            inputStream = url.openStream();
        } catch (MalformedURLException e) {
            long lastModified = Files.getLastModifiedTime(Paths.get(source)).toMillis();
            if (lastModified == this.lastModification) {
                return;
            }
            this.lastModification = lastModified;
            inputStream = new FileInputStream(source);
        }

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        reader.beginArray();
        while (reader.hasNext()) {
            Quote quote = gson.fromJson(reader, Quote.class);
            Broker.QUOTE_QUEUE.add(quote);
        }
        reader.endArray();
        reader.close();
    }
}
