package com.dz.broker;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/*
https://s3.amazonaws.com/test-data-samples/stocks.json, src/main/resources/csvstocks.csv. src/main/resources/jsonstocks.json
 */

public class Broker implements Runnable {

    static Queue<Quote> QUOTE_QUEUE = new LinkedBlockingDeque<Quote>();
    static Map<String, Double> quotes = new Hashtable<String, Double>();
    private boolean stop = false;

    public Double GetLowestPrice(String stockName) {
        return quotes.get(stockName);
    }

    public Map<String, Double> GetAllLowestPrices() {
        return quotes;
    }

    final ScheduledExecutorService scheduler;
    public Broker(String[] args) {
        scheduler = Executors.newScheduledThreadPool(args.length);
        for (String source: args) {
            Reader reader = factory(source);
            final ScheduledFuture<?> readerHandle =
                    scheduler.scheduleAtFixedRate(reader, 0, 20, TimeUnit.SECONDS);
        }
    }

    private Reader factory(String source) {
        if (source.endsWith(".json")) {
            return new JsonStreamReader(source);
        } else if (source.endsWith(".csv")) {
            return new CsvStreamReader(source);
        } else {
            return new Reader() {
                @Override
                public void read() throws IOException {

                }
            };
        }
    }

    public static void main(String[] args) {
        final Broker broker = new Broker(args);
        new Thread(broker).start();
/*
        Runnable printer = new Runnable() {
            @Override
            public void run() {
                System.out.println(broker.GetLowestPrice("AABA"));
            }
        };
        broker.scheduler.scheduleAtFixedRate(printer, 0, 5, TimeUnit.SECONDS);
*/

    }

    @Override
    public void run() {
        while (!stop) {
            Quote quote = QUOTE_QUEUE.peek();
            if (quote != null) {
                quote = QUOTE_QUEUE.remove();
                Double lowestPrice = quotes.get(quote.name);
                if (lowestPrice == null) {
                    lowestPrice = quote.price;
                } else {
                    lowestPrice = Math.min(lowestPrice, quote.price);
                }
                quotes.put(quote.name, lowestPrice);
            }
        }
    }
}
