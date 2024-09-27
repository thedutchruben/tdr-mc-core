package nl.thedutchruben.mccore.utils;

import com.google.gson.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonUtil {

    public static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeAdapter(Enchantment.class, new EnchantmentAdapter())
                .create();
    }

    private static class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
        @Override
        public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", itemStack.getType().name());
            jsonObject.addProperty("amount", itemStack.getAmount());
            if (itemStack.hasItemMeta()) {
                ItemMeta meta = itemStack.getItemMeta();
                jsonObject.add("meta", context.serialize(meta));
            }
            return jsonObject;
        }

        @Override
        public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            ItemStack itemStack = new ItemStack(
                    org.bukkit.Material.valueOf(jsonObject.get("type").getAsString()),
                    jsonObject.get("amount").getAsInt()
            );
            if (jsonObject.has("meta")) {
                ItemMeta meta = context.deserialize(jsonObject.get("meta"), ItemMeta.class);
                itemStack.setItemMeta(meta);
            }
            return itemStack;
        }
    }

    private static class EnchantmentAdapter implements JsonSerializer<Enchantment>, JsonDeserializer<Enchantment> {
        @Override
        public JsonElement serialize(Enchantment enchantment, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(enchantment.getName());
        }

        @Override
        public Enchantment deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            return Enchantment.getByName(jsonElement.getAsString());
        }
    }
}