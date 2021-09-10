package com.dz.broker;

import java.io.IOException;

public abstract class Reader implements Runnable{
    public abstract void read() throws IOException;

    @Override
    public void run() {
        try {
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
