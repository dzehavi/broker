package com.dz.broker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class CsvStreamReader extends Reader {
    private final String path;
    long lastModification = -1;

    public CsvStreamReader(String path) {
        this.path = path;
    }

    @Override
    public void read() throws IOException {
        long lastModified = Files.getLastModifiedTime(Paths.get(path)).toMillis();
        if (lastModified == this.lastModification) {
            return;
        }
        this.lastModification = lastModified;

        Path source = Paths.get(path);
        try (Scanner scanner = new Scanner(source)) {
            scanner.useDelimiter("\n");
            scanner.nextLine(); // skip header line
            while (scanner.hasNext()) {
                String next = scanner.next();
                String[] split = next.split(",");
                Quote quote = new Quote(split[0], Double.parseDouble(split[2]));
                Broker.QUOTE_QUEUE.add(quote);
            }
        }
    }
}
