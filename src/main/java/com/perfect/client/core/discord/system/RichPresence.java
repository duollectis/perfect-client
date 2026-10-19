package com.perfect.client.core.discord.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RichPresence {

    @Setter
    private String details;
    @Setter
    private String state;

    private Assets assets;
    private Party party;
    private Timestamps timestamps;
    public List<Button> buttons = null;

    public void setLargeImage(String key, String text) {
        if (assets == null) {
            assets = new Assets();
        }

        assets.largeImage = key;
        assets.largeText = text;
    }

    public void setSmallImage(String key, String text) {
        if (assets == null) {
            assets = new Assets();
        }

        assets.smallImage = key;
        assets.smallText = text;
    }

    public void setPartyInfo(String partyName, int partySize, int partyMax) {
        if (party == null) {
            party = new Party();
        }

        party.partyName = partyName;
        party.partySize = partySize;
        party.partyMax = partyMax;
    }

    public void setStart(long time) {
        if (timestamps == null) {
            timestamps = new Timestamps();
        }

        timestamps.start = time;
    }

    public void setEnd(long time) {
        if (timestamps == null) {
            timestamps = new Timestamps();
        }

        timestamps.end = time;
    }

    public void addButton(String name, String url) {
        if (buttons == null) {
            buttons = new ArrayList<>();
        }

        Button button = new Button(name, url);
        buttons.add(button);
    }

    public JsonObject toJson() {
        // Основная инфа
        JsonObject o = new JsonObject();
        if (details != null) {
            o.addProperty("details", details);
        }
        if (state != null) {
            o.addProperty("state", state);
        }

        // Ассеты
        if (assets != null) {
            JsonObject a = getAssetsJsonObject();

            o.add("assets", a);
        }

        if (party != null) {
            JsonObject a = getPartyJsonObject();

            o.add("party", a);
        }

        // Время
        if (timestamps != null) {
            JsonObject t = new JsonObject();
            if (timestamps.start != null) {
                t.addProperty("start", timestamps.start);
            }
            if (timestamps.end != null) {
                t.addProperty("end", timestamps.end);
            }

            o.add("timestamps", t);
        }

        // Кнопочки
        if (buttons != null) {
            JsonArray b = new JsonArray();

            buttons.forEach(button -> {
                JsonObject buttonObject = new JsonObject();

                buttonObject.addProperty("label", button.label);
                buttonObject.addProperty("url", button.url);

                b.add(buttonObject);
            });

            o.add("buttons", b);
        }

        return o;
    }

    private @NotNull JsonObject getAssetsJsonObject() {
        JsonObject a = new JsonObject();

        if (assets.largeImage != null) {
            a.addProperty("large_image", assets.largeImage);
        }
        if (assets.largeText != null) {
            a.addProperty("large_text", assets.largeText);
        }
        if (assets.smallImage != null) {
            a.addProperty("small_image", assets.smallImage);
        }
        if (assets.smallText != null) {
            a.addProperty("small_text", assets.smallText);
        }

        return a;
    }

    private @NotNull JsonObject getPartyJsonObject() {
        JsonObject a = new JsonObject();

        if (party.partyName != null) {
            a.addProperty("party_name", party.partyName);
        }
        if (party.partySize != null && party.partyMax != null) {
            JsonArray sizeArray = new JsonArray();

            sizeArray.add(party.partySize);
            sizeArray.add(party.partyMax);

            a.add("size", sizeArray);
        }

        return a;
    }

    public static class Assets {
        public String largeImage, largeText;
        public String smallImage, smallText;
    }

    public static class Party {
        public String partyName;
        public Integer partySize, partyMax;
    }

    @AllArgsConstructor
    public static class Button {
        public String label;
        public String url;
    }

    public static class Timestamps {
        public Long start;
        public Long end;
    }
}