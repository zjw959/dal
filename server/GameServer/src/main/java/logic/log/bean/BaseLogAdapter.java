package logic.log.bean;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import log.DalLog;
import log.ILogInfo;
import log.LoggerConstants;
import logic.character.bean.Player;

public class BaseLogAdapter implements ILogInfo {
    private static Gson gson;

    @Expose(serialize = false)
    private BaseLog baseLog;

    public BaseLogAdapter(Player player, String eventType, String eventName) {
        this.baseLog = new BaseLog(player, eventType, eventName);
    }

    public BaseLog getBaseLog() {
        return baseLog;
    }

    public void setBaseLog(BaseLog baseLog) {
        this.baseLog = baseLog;
    }

    public DalLog getLog() {
        return LoggerConstants.PLAYEREVENT;
    }

    @Override
    public void sendLog() {
        baseLog.setProperty(gson.toJsonTree(this));
        getLog().info(gson.toJson(baseLog));
    }

    
    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.serializeSpecialFloatingPointValues();
        builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                final Expose expose = fieldAttributes.getAnnotation(Expose.class);
                return expose != null && !expose.serialize();
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        });
        gson = builder.create();
    }
}
