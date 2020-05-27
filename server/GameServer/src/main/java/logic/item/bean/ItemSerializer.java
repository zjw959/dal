package logic.item.bean;

import java.lang.reflect.Type;

import logic.constant.EItemType;

import org.apache.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import data.GameDataManager;
import data.bean.BaseGoods;

public class ItemSerializer implements JsonDeserializer<Item>, JsonSerializer<Item> {
    private static final Logger LOGGER = Logger.getLogger(ItemSerializer.class);

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        int templateId = json.getAsJsonObject().get("cid").getAsInt();
        BaseGoods itemBean = GameDataManager.getBaseGoods(templateId);
        if (itemBean == null) {
            LOGGER.error("item is null. templateId:" + templateId);
            return null;
        }

        EItemType type = EItemType.itemType(itemBean.getSuperType(), templateId);
        Class<? extends Item> clazz = type.getClazz();
        return context.deserialize(json, clazz);
    }

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, src.getClass());
    }
}
