package com.perfect.client.core.discord.system;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.perfect.client.core.discord.connection.DiscordConnection;
import lombok.Getter;

import java.lang.management.ManagementFactory;
import java.util.function.BiConsumer;

public class DiscordIPC {
    private static final Gson GSON = new Gson();

    private static BiConsumer<Integer, String> onError = DiscordIPC::defaultErrorCallback;

    private static DiscordConnection c;
    private static Runnable onReady;

    private static boolean receivedDispatch;
    private static JsonObject queuedActivity;

    @Getter
    private static IPCUser user;

    public static void setOnError(BiConsumer<Integer, String> onError) {
        DiscordIPC.onError = onError;
    }

    public static boolean start(long appId, Runnable onReady) {
        c = DiscordConnection.open(DiscordIPC::onPacket);
        if (c == null) {
            return false;
        }

        DiscordIPC.onReady = onReady;

        JsonObject o = new JsonObject();
        o.addProperty("v", 1);
        o.addProperty("client_id", Long.toString(appId));
        c.write(Opcode.HANDSHAKE, o);

        return true;
    }

    public static boolean isConnected() {
        return c != null;
    }

    public static void setActivity(RichPresence presence) {
        if (c == null) {
            return;
        }

        queuedActivity = presence.toJson();
        if (receivedDispatch) {
            sendActivity();
        }
    }

    public static void stop() {
        if (c != null) {
            c.close();

            c = null;
            onReady = null;
            receivedDispatch = false;
            queuedActivity = null;
            user = null;
        }
    }

    private static void sendActivity() {
        JsonObject args = new JsonObject();
        args.addProperty("pid", getPID());
        args.add("activity", queuedActivity);

        JsonObject o = new JsonObject();
        o.addProperty("cmd", "SET_ACTIVITY");
        o.add("args", args);

        c.write(Opcode.FRAME, o);
        queuedActivity = null;
    }

    private static void onPacket(Packet packet) {
        JsonObject data = packet.data();

        if (packet.opcode() == Opcode.CLOSE) {
            if (onError != null) {
                onError.accept(data.get("code").getAsInt(), data.get("message").getAsString());
            }

            stop();
        } else if (packet.opcode() == Opcode.FRAME) {
            if (data.has("evt") && data.get("evt").getAsString().equals("ERROR")) {
                JsonObject d = data.getAsJsonObject("data");

                if (onError != null) {
                    onError.accept(d.get("code").getAsInt(), d.get("message").getAsString());
                }
            } else if (data.has("cmd") && data.get("cmd").getAsString().equals("DISPATCH")) {
                receivedDispatch = true;
                user = GSON.fromJson(data.getAsJsonObject("data").getAsJsonObject("user"), IPCUser.class);

                if (onReady != null) {
                    onReady.run();
                }
                if (queuedActivity != null) {
                    sendActivity();
                }
            }
        }
    }

    private static int getPID() {
        String pr = ManagementFactory.getRuntimeMXBean().getName();

        return Integer.parseInt(pr.substring(0, pr.indexOf(64)));
    }

    private static void defaultErrorCallback(int code, String message) {
        //     System.err.println("Discord IPC error " + code + " with message: " + message);
    }
}