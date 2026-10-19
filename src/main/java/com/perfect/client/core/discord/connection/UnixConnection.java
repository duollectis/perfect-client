package com.perfect.client.core.discord.connection;

import com.google.gson.JsonParser;
import com.perfect.client.core.discord.system.Opcode;
import com.perfect.client.core.discord.system.Packet;

import java.io.IOException;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class UnixConnection extends DiscordConnection {
    private final Selector selector;
    private final SocketChannel socketChannel;
    private final Consumer<Packet> callback;

    public UnixConnection(String name, Consumer<Packet> callback) throws IOException {
        this.selector = Selector.open();
        this.socketChannel = SocketChannel.open(UnixDomainSocketAddress.of(name));
        this.callback = callback;

        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        Thread thread = new Thread(this::run);
        thread.setName("Discord IPC - Read thread");
        thread.start();
    }

    private void run() {
        State state = State.OPCODE;

        ByteBuffer intB = ByteBuffer.allocate(4);
        ByteBuffer dataB = null;

        Opcode opcode = null;

        try {
            while (true) {
                selector.select();

                switch (state) {
                    case OPCODE -> {
                        socketChannel.read(intB);
                        if (intB.hasRemaining()) {
                            break;
                        }

                        opcode = Opcode.valueOf(Integer.reverseBytes(intB.getInt(0)));
                        state = State.LENGTH;

                        intB.rewind();
                    }
                    case LENGTH -> {
                        socketChannel.read(intB);
                        if (intB.hasRemaining()) {
                            break;
                        }

                        dataB = ByteBuffer.allocate(Integer.reverseBytes(intB.getInt(0)));
                        state = State.DATA;

                        intB.rewind();
                    }
                    case DATA -> {
                        socketChannel.read(dataB);
                        if (dataB.hasRemaining()) {
                            break;
                        }

                        String data = Charset.defaultCharset().decode(dataB.rewind()).toString();
                        callback.accept(new Packet(opcode, JsonParser.parseString(data).getAsJsonObject()));

                        dataB = null;
                        state = State.OPCODE;
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void write(ByteBuffer buffer) {
        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            selector.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private enum State {
        OPCODE,
        LENGTH,
        DATA
    }
}