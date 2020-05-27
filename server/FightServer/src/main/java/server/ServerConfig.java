package server;

public class ServerConfig {
    /** 测试服务器 **/
    private boolean isTestServer;
    /** 通信方式 */
    private int netType;
    /** 客户端服务器之间的监听端口 **/
    private int clientListenPort;
    /** 客户端连接空闲超时时长(单位:秒) **/
    private int clientChannelIdleTime;
    /** Http端口 **/
    private int httpListenPort;
    /** csv配置表获取地址 **/
    private String csvUrl;
    /** 服务器group **/
    private int serverGroup;
    /** 服务器id **/
    private int serverId;
    /** 队伍数量 组队 单人匹配 队伍数超过此上限 匹配间隔会加长 **/
    private int teamMaxMate;
    /** 匹配间隔次数 组队 单人匹配 队伍数超过上限 匹配间隔会加长 **/
    private int intevalMate;
    /** 服务器初始化信息地址 */
    private String initServerUrl;
    /** 公网ip */
    private String externalIp;
    /** 最大战斗队伍数 */
    private int maxFightNum;
    /** 单次tick时间 */
    private int tickInterval;
    
    /**
     * 单例枚举
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

    public static ServerConfig getInstance() {
        return Singleton.INSTANCE.getInstance();
    }


    public void load(String configPath) throws Exception {
        ServerConfigUtil util = new ServerConfigUtil(configPath);

        netType = util.getIntValue("net_type");
        clientListenPort = util.getIntValue("client_listen_port");
        clientChannelIdleTime = util.getIntValue("client_channel_idle_time");
        httpListenPort = util.getIntValue("http_listen_port");
        teamMaxMate = util.getIntValue("team_max_mate");
        intevalMate = util.getIntValue("inteval_mate");
        initServerUrl = util.getStringValue("init_server_url");
        tickInterval = util.getIntValue("tick_interval");
    }

    /**
     * 是否是测试服务器
     * 
     * @return
     */
    public boolean getIsTestServer() {
        return isTestServer;
    }

    public void setTestServer(boolean isTestServer) {
        this.isTestServer = isTestServer;
    }
    
    public int getNetType() {
        return netType;
    }
    
    public void setNetType(int netType) {
        this.netType = netType;
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

    public String getCsvUrl() {
        return csvUrl;
    }

    public void setCsvUrl(String csvUrl) {
        this.csvUrl = csvUrl;
    }
    
    public int getServerGroup() {
        return serverGroup;
    }
    
    public void setServerGroup(int serverGroup) {
        this.serverGroup = serverGroup;
    }
    
    public int getServerId() {
        return serverId;
    }
    
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
    
    public int getTeamMaxMate() {
        return teamMaxMate;
    }


    public int getIntevalMate() {
        return intevalMate;
    }

    public String getInitServerUrl() {
        return initServerUrl;
    }
    
    public String getExternalIp() {
        return externalIp;
    }
    
    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }
    
    public int getMaxFightNum() {
        return maxFightNum;
    }
    
    public void setMaxFightNum(int maxFightNum) {
        this.maxFightNum = maxFightNum;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }
}
