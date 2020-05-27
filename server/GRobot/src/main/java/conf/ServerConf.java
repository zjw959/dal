package conf;

/**
 * @function 服务器配置
 */
public final class ServerConf {

    public ServerConf(String name) {
        this.name = name;
        // this.ip = ip;
        // this.gamePort = gamePort;
        // this.httpPort = httpPort;
        // this.platformId = platformId;
        // this.serverId = serverId;
    }

    /**
     * 服务器名称
     */
    private String name;
    /**
     * 服务器IP地址
     */
    private String ip;
    /**
     * 游戏端口
     */
    private int gamePort;
    /**
     * HTTP工具端口
     */
    private int httpPort;
    /**
     * 平台ID
     */
    private int platformId;
    /**
     * 服务器ID
     */
    private int serverId;

    private int selectId;

    @Override
    public String toString() {
        return name;
    }

    public int getSelectId() {
        return selectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public String getIp() {
    // return ip;
    // }
    //
    // public void setIp(String ip) {
    // this.ip = ip;
    // }

    // public int getGamePort() {
    // return gamePort;
    // }
    //
    // public void setGamePort(int gamePort) {
    // this.gamePort = gamePort;
    // }

    // public int getHttpPort() {
    // return httpPort;
    // }
    //
    // public void setHttpPort(int httpPort) {
    // this.httpPort = httpPort;
    // }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public void setIndex(int selectId) {
        this.selectId = selectId;
    }
}


