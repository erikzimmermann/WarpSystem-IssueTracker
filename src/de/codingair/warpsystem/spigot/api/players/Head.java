package de.codingair.warpsystem.spigot.api.players;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.codingair.codingapi.player.data.gameprofile.GameProfileUtils;
import de.codingair.codingapi.tools.items.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.Collection;
import java.util.UUID;

public class Head {
    private static final String URL = "http://textures.minecraft.net/texture/";
    private static final String textures = "{\"profileName\":\"BLANK\",\"textures\":{\"SKIN\":{\"url\":\"http:\\/\\/textures.minecraft.net\\/texture\\/%s\"}},\"profileId\":\"5bb6371844664586b7d0d410024eb458\",\"timestamp\":\"0\"}";
    private String id;

    public Head(String id) {
        this.id = id;
    }

    public Head(Player player) {
        GameProfile profile = GameProfileUtils.getGameProfile(player);

        Collection<Property> properties = profile.getProperties().get("textures");
        Property property = properties.toArray().length == 0 ? null : (Property) properties.toArray()[0];

        String pValue = property == null ? null : property.getValue();

        try {
            JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64Coder.decode(pValue)));
            JSONObject textures = (JSONObject) json.get("textures");
            JSONObject SKIN = (JSONObject) textures.get("SKIN");
            String url = (String) SKIN.get("url");
            id = url.replace(URL, "");
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }

    public GameProfile buildProfile() {
        GameProfile modified = new GameProfile(UUID.randomUUID(), "BLANK");

        modified.getProperties().put("textures", new Property("textures", Base64Coder.encodeString(String.format(textures, id)), "BLANK"));

        return modified;
    }

    public ItemStack buildItem() {
        return ItemBuilder.getHead(buildProfile());
    }

    public String getId() {
        return id;
    }
}
