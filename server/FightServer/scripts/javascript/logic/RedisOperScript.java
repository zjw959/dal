package javascript.logic;

import java.util.ArrayList;
import java.util.List;

import logic.constant.EScriptIdDefine;
import redis.IRedisOperScript;
import redis.base.RedisCmd;
import redis.base.RedisOper;
import redis.service.ERedisType;
import redis.service.RedisServices;

public class RedisOperScript extends IRedisOperScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.REDIS_OPERATE_SCRIPT.Value();
    }

    @Override
    public List<String> match(String queueKey, long endTime, int size, String[] sha)
            throws Exception {
        Object[] params = new String[7];
        // keycount
        params[1] = "1";
        // key
        params[2] = queueKey;
        // argvs
        // begintime
        params[3] = String.valueOf(0);
        params[4] = String.valueOf(endTime);
        // beigninedx
        params[5] = "0";
        // endindex
        params[6] = String.valueOf(size);


        // 测试单队列10W的执行时间.
        // 该lua脚本需要支持后台重载
        redis.base.RedisOper redisOper =
                RedisServices.getRedisService(ERedisType.Fight.getType()).getRedisOper();

        // 首先判断sha1值是否已经存在
        if (sha[0] == null || sha[0].isEmpty()) {
            scriptLoad(redisOper, queueKey, sha);
        }

        // sha1的脚本是否存在
        boolean isExist = (Boolean) redisOper.execute(RedisCmd.scriptexists, queueKey, sha[0]);
        if (!isExist) {
            scriptLoad(redisOper, queueKey, sha);
        }

        params[0] = sha[0];
        ArrayList ids = (ArrayList) redisOper.execute(RedisCmd.evalsha, queueKey, params);

        return ids;
    }

    private void scriptLoad(RedisOper redisOper, String key, String[] sha) throws Exception {
        String luaScript = "redis.call('zremrangebyscore',KEYS[1],ARGV[1],ARGV[2]) "
                + "if redis.call('ZCARD',KEYS[1]) >= tonumber(ARGV[4]) then "
                + "local rems = redis.call('ZREVRANGE',KEYS[1],ARGV[3],ARGV[4]-1) "
                + "redis.call('ZREM',KEYS[1],unpack(rems)) " + "return rems " + "end ";
        String _sha1 = (String) redisOper.execute(RedisCmd.scriptload, key, luaScript);
        if (_sha1 == null || _sha1.isEmpty()) {
            throw new Exception("can not load script");
        }
        sha[0] = _sha1;
    }
}
