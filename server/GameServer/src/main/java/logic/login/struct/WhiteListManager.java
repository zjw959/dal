package logic.login.struct;

import java.util.ArrayList;

/**
 * 白名单
 */
public class WhiteListManager {
    /**
     * 单例接口
     * 
     * @return
     */
    public static WhiteListManager GetInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {

        INSTANCE;

        WhiteListManager processor;

        Singleton() {
            this.processor = new WhiteListManager();
        }

        WhiteListManager getProcessor() {
            return processor;
        }
    }

    private String m_whiteListKey = "Aa1Bb2Cc3Dd4Ee5";
    private int m_whiteListKeyUseTime = 0;
    private ArrayList<String> m_legalIpAddress = new ArrayList<>();
    private ArrayList<String> m_legalUsers = new ArrayList<>();


    /**
     * ip地址是否在白名单
     * 
     * @param ipAddress
     */
    public boolean addressInWhiteList(String ipAddress) {
        // 内网直接过
        if (ipAddress.startsWith("/127.0") || ipAddress.startsWith("/192.168")
                || ipAddress.startsWith("/10."))
            return true;

        String formatIpAddress = _FormatIpAddress(ipAddress);
        for (int a = 0; a < m_legalIpAddress.size(); ++a) {
            if (m_legalIpAddress.get(a).equals(formatIpAddress))
                return true;
        }
        return false;
    }

    /**
     * 用户名是否在白名单
     * 
     * @param userId
     * @return
     */
    public boolean UserInWhiteList(String userId) {
        for (int a = 0; a < m_legalUsers.size(); ++a) {
            if (m_legalUsers.get(a).equals(userId))
                return true;
        }
        return false;
    }

    /**
     * 移除白名单IP
     * 
     * @param value
     */
    public void RemoveIpAddress(String value) {
        m_legalIpAddress.remove(value);
    }

    /**
     * 移除白名单用户
     * 
     * @param value
     */
    public void RemoveUser(String value) {
        m_legalUsers.remove(value);
    }


    /**
     * 登录key是否正确
     * 
     * @param whiteListKey
     * @return
     */
    public int LegalWhiteListKey(String whiteListKey) {
        if (m_whiteListKey.equals(whiteListKey)) {
            return m_whiteListKeyUseTime++;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 添加合法的登录ip
     * 
     * @param ipAddress
     * @return
     */
    public boolean AddLegalIpAddress(String ipAddress) {
        if (!m_legalIpAddress.contains(ipAddress))
            m_legalIpAddress.add(ipAddress);
        return true;
    }

    /**
     * 添加合法的用户名
     * 
     * @param user
     * @return
     */
    public boolean AddLegalUser(String user) {
        if (!m_legalUsers.contains(user))
            m_legalUsers.add(user);
        return true;
    }

    /**
     * 修改登录Key
     * 
     * @param key
     * @return
     */
    public boolean ChangeWhiteListKey(String key) {
        m_whiteListKey = key;
        return true;
    }

    private String _FormatIpAddress(String address) {
        int beg = address.indexOf("/");
        int end = address.indexOf(":");
        return address.substring(beg + 1, end);
    }

    // public void Save() {
    // JSONObject object = new JSONObject();
    // object.put("whiteListKey", m_whiteListKey);
    // JSONArray ips = new JSONArray();
    // for (int a = 0; a < m_legalIpAddress.size(); ++a) {
    // ips.add(m_legalIpAddress.get(a));
    // }
    //
    // object.put("ip", ips);
    // JSONArray users = new JSONArray();
    // for (int b = 0; b < m_legalUsers.size(); ++b) {
    // users.add(m_legalUsers.get(b));
    // }
    // object.put("user", users);
    // object.put("whiteListKeyUseTime", m_whiteListKeyUseTime);
    //
    // t_game_global bean = new t_game_global();
    // bean.setT_data(object.toString());
    // bean.setT_id(GlobalType.WHITE_LIST.GetId());
    //
    // PlayerRestoreProcessor.getInstance().submitRequest(new Handler() {
    // @Override
    // public void action() {
    // try {
    // TGameGlobalDao.insertAndUpdate(bean);
    // LOGGER.info("save writeList data done");
    // } catch (Exception e) {
    // LOGGER.error(e, e);
    // }
    // }
    // });
    // }
    //
    // public void Load() throws Exception {
    // // try
    // // {
    // t_game_global data = TGameGlobalDao.selectOneById(GlobalType.WHITE_LIST.GetId());
    // if (data == null) {
    // return;
    // }
    //
    // JSONObject object = (JSONObject) JSONObject.parse(data.getT_data());
    // m_whiteListKey = object.getString("whiteListKey");
    // m_whiteListKeyUseTime = object.getIntValue("whiteListKeyUseTime");
    //
    // JSONArray ips = object.getJSONArray("ip");
    // for (int a = 0; a < ips.size(); ++a)
    // m_legalIpAddress.add(ips.getString(a));
    //
    // JSONArray users = object.getJSONArray("user");
    // for (int a = 0; a < users.size(); ++a)
    // m_legalUsers.add(users.getString(a));
    //
    // // }
    // // catch (Exception e)
    // // {
    // // LOGGER.error(e, e);
    // // }
    // }
}
