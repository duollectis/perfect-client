package com.perfect.client.core.discord.connection;

import com.google.gson.JsonParser;
import com.perfect.client.core.discord.system.Opcode;
import com.perfect.client.core.discord.system.Packet;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class WinConnection extends DiscordConnection {
    private final RandomAccessFile randomAccessFile;
    private final Consumer<Packet> callback;

    WinConnection(String name, Consumer<Packet> callback) throws IOException {
        this.randomAccessFile = new RandomAccessFile(name, "rw");
        this.callback = callback;

        Thread thread = new Thread(this::run);
        thread.setName("Discord IPC - Read thread");
        thread.start();
    }

    @Override
    protected void write(ByteBuffer buffer) {
        try {
            randomAccessFile.write(buffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        ByteBuffer intB = ByteBuffer.allocate(4);

        try {
            while (true) {
                readFully(intB);
                Opcode opcode = Opcode.valueOf(Integer.reverseBytes(intB.getInt(0)));

                readFully(intB);
                int length = Integer.reverseBytes(intB.getInt(0));

                ByteBuffer dataB = ByteBuffer.allocate(length);
                readFully(dataB);

                String data = StandardCharsets.UTF_8.decode(dataB.rewind()).toString();

                callback.accept(new Packet(opcode, JsonParser.parseString(data).getAsJsonObject()));
            }
        } catch (Exception ignored) {}
    }

    private void readFully(ByteBuffer buffer) throws IOException {
        buffer.rewind();

        while (randomAccessFile.length() < buffer.remaining()) {
            Thread.onSpinWait();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (buffer.hasRemaining()) {
            randomAccessFile.getChannel().read(buffer);
        }
    }

    @Override
    public void close() {
        try {
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}