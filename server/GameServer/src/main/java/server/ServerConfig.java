package server;

import org.apache.log4j.Logger;

/**
 * 服务器配置
 */
public class ServerConfig {
    private final static Logger LOGGER = Logger.getLogger(ServerConfig.class);


    // HTTP后台控制打印. 默认关
    private boolean isPrintQueueSize = true;


    public static ServerConfig getInstance() {
        return Singleton.INSTANCE.getInstance();
    }


    public void load(String configPath, String[] args) throws Exception {
        ServerConfigUtil util = new ServerConfigUtil(configPath);

        // if (GameServer.getInstance().isIDEMode()) {
        // if (args.length < 1) {
        // LOGGER.error("IDEMode Miss serverId param, System Exit");
        // System.exit(-1);
        // } else {
        // serverId = Integer.valueOf(args[0]);
        // }
        // } else {
        // serverId = util.getIntValue("server_id");
        // }

        // if (util.getIntValue("isTestServer") == 1) {
        // isTestServer = true;
        // } else {
        // isTestServer = false;
        // }

        clientListenPort = util.getIntValue("client_listen_port");
        clientChannelIdleTime = util.getIntValue("client_channel_idle_time");
        httpListenPort = util.getIntValue("http_listen_port");
        // gameDBConfig = util.getDBConf("db-game");
        // gameLogDBConfig = util.getDBConf("db-game-log");
        serverName = util.getStringValueEmpty("serverName");
        // csvUrl = util.getStringValueEmpty("csv_url");
        initServerUrl = util.getStringValue("init_server_url");
        loginVerifyUrl = initServerUrl + "/account/loginVerify";
        loginSyncUrl = initServerUrl + "/server/onlineNum";
        // giftCodeVerifyUrl = util.getStringValue("giftcode_verify_url");
    }

    /**
     * 服务器Id
     * 
     * @return
     */
    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
        System.setProperty("serverId", String.valueOf(serverId));
    }


    /**
     * 专服id
     * 
     * @return
     */
    public int getSpecialId() {
        return specialId;
    }

    /**
     * 专服id
     * 
     * @return
     */
    public void setSpecialId(int specialId) {
        this.specialId = specialId;
        System.setProperty("specialId", String.valueOf(specialId));
    }

    /**
     * 监听客户端端口
     * 
     * @return
     */
    public int getClientListenPort() {
        return clientListenPort;
    }

    /**
     * 客户端idle时间间隔
     * 
     * @return
     */
    public int getClientChannelIdleTime() {
        return clientChannelIdleTime;
    }

    /**
     * http监听端口
     * 
     * @return
     */
    public int getHttpListenPort() {
        return httpListenPort;
    }

    // public DataBaseConfig getGameDBConfig() {
    // return gameDBConfig;
    // }

    // public DataBaseConfig getGameDataDBConfig() {
    // return gameDataDBConfig;
    // }

    // public DataBaseConfig getGameLogDBConfig() {
    // return gameLogDBConfig;
    // }
    public void setIsTestServer(boolean b) {
        this.isTestServer = b;
    }
    /**
     * 是否是测试服务器
     * 
     * @return
     */
    public boolean isTestServer() {
        return isTestServer;
    }

    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getCsvUrl() {
		return csvUrl;
	}

    public String getInitServerUrl() {
        return initServerUrl;
    }

    public String getLoginVerifyUrl() {
        return loginVerifyUrl;
    }

    public String getLoginSyncUrl() {
        return loginSyncUrl;
    }

    public String getGiftCodeVerifyUrl() {
        return giftCodeVerifyUrl;
    }

    public void setCsvUrl(String csvUrl) {
        this.csvUrl = csvUrl;
    }

    public void setGiftCodeVerifyUrl(String giftCodeVerifyUrl) {
        this.giftCodeVerifyUrl = giftCodeVerifyUrl;
    }


    public int getServerSize() {
        return serverSize;
    }

    public void setServerSize(int serverSize) {
        this.serverSize = serverSize;
    }

    /** 测试服务器 **/
    private boolean isTestServer;
    private String serverName;

    /** 服务器id **/
    private int serverId;
    /** 游戏服数量（启动时从登录服获取） **/
    private int serverSize = 1;
    /** 专服Id **/
    private int specialId;
    /** 客户端服务器之间的监听端口 **/
    private int clientListenPort;
    /** 客户端连接空闲超时时长(单位:秒) **/
    private int clientChannelIdleTime;
    /** Http端口 **/
    private int httpListenPort;
    /** csv配置表获取地址 **/
    private String csvUrl;

    /** Game库配置 */
    // private DataBaseConfig gameDBConfig;
    // /** Game Data库配置 */
    // private DataBaseConfig gameDataDBConfig;
    /** Game Log库配置 */
    // private DataBaseConfig gameLogDBConfig;
    /** 服务器初始化信息地址 */
    private String initServerUrl;
    /** 登录服验证地址 */
    private String loginVerifyUrl;
    /** 登录服信息同步地址 */
    private String loginSyncUrl;
    /** 礼包码兑换验证地址 */
    private String giftCodeVerifyUrl;

    /** ide size **/
    private boolean isPrintQueueSizeIde = false;


    public boolean isPrintQueueSize() {
        return isPrintQueueSize;
    }

    public boolean isPrintQueueSizeIde() {
        return isPrintQueueSizeIde;
    }

    public void setIsPrintQueueSize(boolean isPrintQueueSize) {
        this.isPrintQueueSize = isPrintQueueSize;
    }

    public void setIsPrintQueueSizeIde(boolean isPrintQueueSize) {
        this.isPrintQueueSizeIde = isPrintQueueSize;
    }

    /**
     * 单件枚举
     */
    private enum Singleton {
        INSTANCE;

        ServerConfig instance;

        Singleton() {
            this.instance = new ServerConfig();
        }

        ServerConfig getInstance() {
            return instance;
        }
    }
}
