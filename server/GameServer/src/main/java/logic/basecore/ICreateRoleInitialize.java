package logic.basecore;

/**
 * 创建新角色初始化接口
 * 
 * 慎用. 因为上线后的版本.老用户不会调用该接口
 */
public interface ICreateRoleInitialize {
    public void createRoleInitialize() throws Exception;
}
