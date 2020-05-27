package redis;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jarvis.cache.reflect.generics.ParameterizedTypeImpl;
import com.jarvis.cache.serializer.HessianSerializer;
import com.jarvis.cache.serializer.NullValue;
import com.jarvis.cache.to.CacheWrapper;

import db.game.bean.PlayerDBBean;
import logic.character.PlayerViewService;
import utils.DateEx;
import utils.StrEx;


/**
 * 
 * @Description 外部redis序列化
 * @author LiuJiang
 * @date 2018年6月23日 下午4:22:01
 *
 */
public class ExternalRedisSerializer extends HessianSerializer {
    // private final Charset charset;

    // private static final SerializerFeature[] FEATURES =
    // {SerializerFeature.DisableCircularReferenceDetect};

    private static final ObjectMapper MAPPER = new ObjectMapper();


    public ExternalRedisSerializer() {
        // this(Charset.forName(StrEx.Charset_UTF8));

        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.registerModule(
                new SimpleModule().addSerializer(new NullValueSerializer((String) null)));
        MAPPER.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    private class NullValueSerializer extends StdSerializer<NullValue> {

        private static final long serialVersionUID = 1999052150548658808L;

        private final String classIdentifier;

        /**
         * @param classIdentifier can be {@literal null} and will be defaulted to {@code @class}.
         */
        NullValueSerializer(String classIdentifier) {

            super(NullValue.class);
            this.classIdentifier =
                    StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java. lang.Object,
         * com.fasterxml.jackson.core.JsonGenerator,
         * com.fasterxml.jackson.databind.SerializerProvider)
         */
        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {

            jgen.writeStartObject();
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }
    }

    // public ExternalRedisSerializer(Charset charset) {
    // this.charset = charset;
    // }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(final Object obj) throws Exception {
        // 保存到redis的PlayerDBBean只需要简化后的view相关的数据
        if (obj instanceof CacheWrapper) {
            CacheWrapper<PlayerDBBean> wrapper = (CacheWrapper<PlayerDBBean>) obj;
            Field field = wrapper.getClass().getDeclaredField("cacheObject");
            Field.setAccessible(new Field[] {field}, true);
            Object f = field.get(obj);
            if (f instanceof PlayerDBBean) {
                PlayerDBBean viewBean = null;
                viewBean = (PlayerViewService.getInstance()).toViewBean((PlayerDBBean) f);
                wrapper.setCacheObject(viewBean);
            }
            Field.setAccessible(new Field[] {field}, false);
        }
        // 测试环境下用json格式
        // if ((GameServer.getInstance().isTestServer()
        // && !GameServer.getInstance().isRootDrangServer())) {
        return jsonSerialize(obj);
        // } else {
        // return super.serialize(obj);
        // }
    }

    @Override
    public Object deserialize(final byte[] bytes, final Type returnType) throws Exception {
        // 测试环境下用json格式
        // if ((GameServer.getInstance().isTestServer()
        // && !GameServer.getInstance().isRootDrangServer())) {
        return jsonDeserialize(bytes, returnType);
        // } else {
        // return super.deserialize(bytes, returnType);
        // }
    }

    private byte[] jsonSerialize(final Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return MAPPER.writeValueAsBytes(obj);
    }

    public Object jsonDeserialize(final byte[] bytes, final Type returnType) throws Exception {
        if (null == bytes || bytes.length == 0) {
            return null;
        }
        Type[] agsType = new Type[] {returnType};
        JavaType javaType = MAPPER.getTypeFactory()
                .constructType(ParameterizedTypeImpl.make(CacheWrapper.class, agsType, null));
        return MAPPER.readValue(bytes, javaType);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        long begin = System.currentTimeMillis();
        while (true) {
            // String json =
            // "{\"cacheObject\":{\"channelAppId\":\"1\",\"channelId\":\"LOCAL_TEST\",\"createtime\":1534155464158,\"currentServer\":2,\"data\":\"{\\\"BagManager\\\":{\\\"itemKV\\\":{\\\"198162790472126218\\\":{\\\"hid\\\":110101,\\\"id\\\":198162790472126218,\\\"cid\\\":1101011,\\\"num\\\":1,\\\"dTime\\\":0}},\\\"aSysDay\\\":1534176000000,\\\"aGameDay\\\":1534197600000,\\\"aSysWeek\\\":1534694400000,\\\"aGameWeek\\\":1534716000000,\\\"aSysMonth\\\":1535731200000,\\\"aGameMonth\\\":1535752800000},\\\"InfoManager\\\":{\\\"remark\\\":\\\"\\\",\\\"strength\\\":120,\\\"lastRecoverStrengthTime\\\":72,\\\"helpFightHeroCid\\\":0,\\\"absorbed\\\":0,\\\"glamour\\\":0,\\\"tender\\\":0,\\\"knowledge\\\":0,\\\"fortune\\\":0,\\\"aSysDay\\\":1534176000000,\\\"aGameDay\\\":1534197600000,\\\"aSysWeek\\\":1534694400000,\\\"aGameWeek\\\":1534716000000,\\\"aSysMonth\\\":1535731200000,\\\"aGameMonth\\\":1535752800000},\\\"HeroManager\\\":{\\\"heroKV\\\":{\\\"110101\\\":{\\\"cid\\\":110101,\\\"quality\\\":2,\\\"level\\\":2,\\\"exp\\\":10,\\\"skin\\\":{\\\"skinId\\\":198162790472126218},\\\"angel\\\":{\\\"awakeLevel\\\":1,\\\"useSkillStrategy\\\":1,\\\"skillStrategyKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"name\\\":\\\"天使页1\\\",\\\"alreadyUseSkillPiont\\\":0,\\\"angelKV\\\":{},\\\"passiveSkillKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"2\\\":{\\\"id\\\":2,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"3\\\":{\\\"id\\\":3,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"4\\\":{\\\"id\\\":4,\\\"isUnlock\\\":false,\\\"skillId\\\":0}}},\\\"2\\\":{\\\"id\\\":2,\\\"name\\\":\\\"天使页2\\\",\\\"alreadyUseSkillPiont\\\":0,\\\"angelKV\\\":{},\\\"passiveSkillKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"2\\\":{\\\"id\\\":2,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"3\\\":{\\\"id\\\":3,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"4\\\":{\\\"id\\\":4,\\\"isUnlock\\\":false,\\\"skillId\\\":0}}},\\\"3\\\":{\\\"id\\\":3,\\\"name\\\":\\\"天使页3\\\",\\\"alreadyUseSkillPiont\\\":0,\\\"angelKV\\\":{},\\\"passiveSkillKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"2\\\":{\\\"id\\\":2,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"3\\\":{\\\"id\\\":3,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"4\\\":{\\\"id\\\":4,\\\"isUnlock\\\":false,\\\"skillId\\\":0}}},\\\"4\\\":{\\\"id\\\":4,\\\"name\\\":\\\"天使页4\\\",\\\"alreadyUseSkillPiont\\\":0,\\\"angelKV\\\":{},\\\"passiveSkillKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"2\\\":{\\\"id\\\":2,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"3\\\":{\\\"id\\\":3,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"4\\\":{\\\"id\\\":4,\\\"isUnlock\\\":false,\\\"skillId\\\":0}}}}},\\\"crystalKV\\\":{\\\"1\\\":[],\\\"2\\\":[]},\\\"heroEquip\\\":{}}},\\\"helpFightHeroCid\\\":110101,\\\"formations\\\":{\\\"1\\\":{\\\"type\\\":1,\\\"status\\\":1,\\\"heroIds\\\":[110101]}},\\\"formationType\\\":1,\\\"aSysDay\\\":1534176000000,\\\"aGameDay\\\":1534197600000,\\\"aSysWeek\\\":1534694400000,\\\"aGameWeek\\\":1534716000000,\\\"aSysMonth\\\":1535731200000,\\\"aGameMonth\\\":1535752800000},\\\"HeroManagerView\\\":{\\\"helpHeroFightPower\\\":0,\\\"heroKV\\\":{},\\\"helpFightHeroCid\\\":0,\\\"formations\\\":{},\\\"formationType\\\":0,\\\"aSysDay\\\":1534176000000,\\\"aGameDay\\\":1534197600000,\\\"aSysWeek\\\":1534694400000,\\\"aGameWeek\\\":1534716000000,\\\"aSysMonth\\\":1535731200000,\\\"aGameMonth\\\":1535752800000}}\",\"describe\":\"\",\"exp\":0,\"fightpower\":244,\"gmlevel\":1,\"gold\":201050,\"heroId\":110101,\"ip\":\"/192.168.10.104:55720\",\"isForbid\":0,\"isOnline\":true,\"lastlogintime\":1534155464158,\"level\":60,\"logintime\":1534155464158,\"offlinetime\":1534156132810,\"onlinetime\":774313,\"playerId\":531816922,\"playername\":\"531816922\",\"rechargeDiamond\":0,\"skinCid\":1101011,\"strength\":0,\"systemDiamond\":1000500,\"username\":\"local_test_haha111\",\"viplevel\":0},\"expire\":900,\"expireAt\":1534158186260,\"expired\":false,\"lastLoadTime\":1534157286260}";
            // // Type[] agsType = new Type[] {PlayerDBBean.class};
            // // ParameterizedTypeImpl impl =
            // // ParameterizedTypeImpl.make(CacheWrapper.class, agsType, null);
            // // CacheWrapper<Object> a = JSON.parseObject(json, CacheWrapper.class);
            // // a.setCacheObject(
            // // ((com.alibaba.fastjson.JSONObject) a.getCacheObject())
            // // .toJavaObject(PlayerDBBean.class));
            //
            // // CacheWrapper<Object> a = GsonUtils.fromJson(json, CacheWrapper.class);
            //
            // if (System.currentTimeMillis() - begin > DateEx.TIME_MINUTE * 3) {
            // System.gc();
            // System.out.println("end");
            // break;
            // }

            String json =
                    "{\"cacheObject\":{\"channelAppId\":\"1\",\"channelId\":\"LOCAL_TEST\",\"createtime\":1534155464158,\"currentServer\":2,\"data\":\"{\\\"BagManager\\\":{\\\"itemKV\\\":{\\\"198162790472126218\\\":{\\\"hid\\\":110101,\\\"id\\\":198162790472126218,\\\"cid\\\":1101011,\\\"num\\\":1,\\\"dTime\\\":0}},\\\"aSysDay\\\":1534176000000,\\\"aGameDay\\\":1534197600000,\\\"aSysWeek\\\":1534694400000,\\\"aGameWeek\\\":1534716000000,\\\"aSysMonth\\\":1535731200000,\\\"aGameMonth\\\":1535752800000},\\\"InfoManager\\\":{\\\"remark\\\":\\\"\\\",\\\"strength\\\":120,\\\"lastRecoverStrengthTime\\\":72,\\\"helpFightHeroCid\\\":0,\\\"absorbed\\\":0,\\\"glamour\\\":0,\\\"tender\\\":0,\\\"knowledge\\\":0,\\\"fortune\\\":0,\\\"aSysDay\\\":1534176000000,\\\"aGameDay\\\":1534197600000,\\\"aSysWeek\\\":1534694400000,\\\"aGameWeek\\\":1534716000000,\\\"aSysMonth\\\":1535731200000,\\\"aGameMonth\\\":1535752800000},\\\"HeroManager\\\":{\\\"heroKV\\\":{\\\"110101\\\":{\\\"cid\\\":110101,\\\"quality\\\":2,\\\"level\\\":2,\\\"exp\\\":10,\\\"skin\\\":{\\\"skinId\\\":198162790472126218},\\\"angel\\\":{\\\"awakeLevel\\\":1,\\\"useSkillStrategy\\\":1,\\\"skillStrategyKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"name\\\":\\\"天使页1\\\",\\\"alreadyUseSkillPiont\\\":0,\\\"angelKV\\\":{},\\\"passiveSkillKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"2\\\":{\\\"id\\\":2,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"3\\\":{\\\"id\\\":3,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"4\\\":{\\\"id\\\":4,\\\"isUnlock\\\":false,\\\"skillId\\\":0}}},\\\"2\\\":{\\\"id\\\":2,\\\"name\\\":\\\"天使页2\\\",\\\"alreadyUseSkillPiont\\\":0,\\\"angelKV\\\":{},\\\"passiveSkillKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"2\\\":{\\\"id\\\":2,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"3\\\":{\\\"id\\\":3,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"4\\\":{\\\"id\\\":4,\\\"isUnlock\\\":false,\\\"skillId\\\":0}}},\\\"3\\\":{\\\"id\\\":3,\\\"name\\\":\\\"天使页3\\\",\\\"alreadyUseSkillPiont\\\":0,\\\"angelKV\\\":{},\\\"passiveSkillKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"2\\\":{\\\"id\\\":2,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"3\\\":{\\\"id\\\":3,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"4\\\":{\\\"id\\\":4,\\\"isUnlock\\\":false,\\\"skillId\\\":0}}},\\\"4\\\":{\\\"id\\\":4,\\\"name\\\":\\\"天使页4\\\",\\\"alreadyUseSkillPiont\\\":0,\\\"angelKV\\\":{},\\\"passiveSkillKV\\\":{\\\"1\\\":{\\\"id\\\":1,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"2\\\":{\\\"id\\\":2,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"3\\\":{\\\"id\\\":3,\\\"isUnlock\\\":false,\\\"skillId\\\":0},\\\"4\\\":{\\\"id\\\":4,\\\"isUnlock\\\":false,\\\"skillId\\\":0}}}}},\\\"crystalKV\\\":{\\\"1\\\":[],\\\"2\\\":[]},\\\"heroEquip\\\":{}}},\\\"helpFightHeroCid\\\":110101,\\\"formations\\\":{\\\"1\\\":{\\\"type\\\":1,\\\"status\\\":1,\\\"heroIds\\\":[110101]}},\\\"formationType\\\":1,\\\"aSysDay\\\":1534176000000,\\\"aGameDay\\\":1534197600000,\\\"aSysWeek\\\":1534694400000,\\\"aGameWeek\\\":1534716000000,\\\"aSysMonth\\\":1535731200000,\\\"aGameMonth\\\":1535752800000},\\\"HeroManagerView\\\":{\\\"helpHeroFightPower\\\":0,\\\"heroKV\\\":{},\\\"helpFightHeroCid\\\":0,\\\"formations\\\":{},\\\"formationType\\\":0,\\\"aSysDay\\\":1534176000000,\\\"aGameDay\\\":1534197600000,\\\"aSysWeek\\\":1534694400000,\\\"aGameWeek\\\":1534716000000,\\\"aSysMonth\\\":1535731200000,\\\"aGameMonth\\\":1535752800000}}\",\"describe\":\"\",\"exp\":0,\"fightpower\":244,\"gmlevel\":1,\"gold\":201050,\"heroId\":110101,\"ip\":\"/192.168.10.104:55720\",\"isForbid\":0,\"isOnline\":true,\"lastlogintime\":1534155464158,\"level\":60,\"logintime\":1534155464158,\"offlinetime\":1534156132810,\"onlinetime\":774313,\"playerId\":531816922,\"playername\":\"531816922\",\"rechargeDiamond\":0,\"skinCid\":1101011,\"strength\":0,\"systemDiamond\":1000500,\"username\":\"local_test_haha111\",\"viplevel\":0},\"expire\":900,\"expireAt\":1534158186260,\"lastLoadTime\":1534157286260}";
            Type[] agsType = new Type[] {PlayerDBBean.class};
            JavaType javaType = MAPPER.getTypeFactory()
                    .constructType(ParameterizedTypeImpl.make(CacheWrapper.class, agsType, null));
            MAPPER.readValue(json.getBytes(StrEx.Charset_UTF8), javaType);

            // CacheWrapper cache = new CacheWrapper<>();
            // PlayerDBBean playerDBBean = new PlayerDBBean();
            // cache.setCacheObject(playerDBBean);
            // byte[] bs = MAPPER.writeValueAsBytes(cache);
            // Type[] agsType = new Type[] {PlayerDBBean.class};
            // JavaType javaType = MAPPER.getTypeFactory()
            // .constructType(ParameterizedTypeImpl.make(CacheWrapper.class, agsType, null));
            // Object a = MAPPER.readValue(bs, javaType);

            if (System.currentTimeMillis() - begin > DateEx.TIME_MINUTE * 3) {
                System.gc();
                System.out.println("end");
                break;
            }
        }

        while (true) {
            Thread.sleep(100000);
        }
    }

}
