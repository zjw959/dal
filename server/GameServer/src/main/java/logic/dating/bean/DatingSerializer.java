package logic.dating.bean;

import java.lang.reflect.Type;
import org.apache.log4j.Logger;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/***
 * 约会
 * @author lihongji
 *
 */
public class DatingSerializer implements JsonDeserializer<CurrentDatingBean>,JsonSerializer<CurrentDatingBean> {

    private static final Logger LOGGER = Logger.getLogger(DatingSerializer.class);
    
    public CurrentDatingBean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        int datingType = json.getAsJsonObject().get("datingType").getAsInt();
        EDatingType type = EDatingType.datingType(datingType);
        Class<? extends CurrentDatingBean> clazz = type.getClazz();
        if (clazz == null) {
            LOGGER.error("class is null:" + datingType);
            return null;
        }
        return context.deserialize(json, clazz);
    }
    
    @Override
    public JsonElement serialize(CurrentDatingBean src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src, src.getClass());
    }
}
