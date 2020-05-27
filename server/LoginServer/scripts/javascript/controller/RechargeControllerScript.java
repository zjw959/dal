package javascript.controller;

import gm.db.pay.bean.PayDBBean;
import gm.utils.PayUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.constant.EScriptIdDefine;
import com.controller.RechargeController;
import com.iscript.IRechargeControllerScript;

public class RechargeControllerScript extends IRechargeControllerScript {
    private static final Logger log = Logger.getLogger(RechargeControllerScript.class);

    private static final String SUCCESS_JSON = "{\"errno\":\"0\",\"errmsg\":\"成功\"}";
   	private static final String TOKEN_VALIDATE_FAIL_JSON = "{\"errno\":\"1\",\"errmsg\":\"Token校验失败\"}";
   	private static final String UNKNOW_ERROR = "{\"errno\":\"100\",\"errmsg\":\"未知错误\"}";
   	private static final String FAIL_ORDER = "{\"errno\":\"100\",\"errmsg\":\"status fail\"}";
    private static final String RECHARGR_PREFIX = "rechargeInfo==> ";
   
   	private String secret = "68d937472760024c9925cde480ad3a05";
   	private String SUCCESS = "1";
    
    @Override
    public int getScriptId() {
        return EScriptIdDefine.Recharge_Controller_SCRIPTID.Value();
    }

	@Override
	public void rechargeCallBack(RechargeController rechargeController, 
			HttpServletRequest request,HttpServletResponse response) throws IOException {
		String sid = request.getParameter("sid");//约战没有服务器id   统一规定为1
		String uid = request.getParameter("uid");//黑桃回调过来的玩家id
		String orderno = request.getParameter("orderno");//黑桃订单
		String amount = request.getParameter("amount");//真实充值金额
		String status = request.getParameter("status");//状态，1成功，2失败  如果传过来的是失败则不处理
		String extinfoArray = request.getParameter("extinfo");//自定义信息--目前商量好的自定义信息就是游戏生成的订单id
		String htnonce = request.getParameter("htnonce");
		String httoken = request.getParameter("httoken");//验证令牌

//		log.info("recharge start sid={},uid={},orderno={},amount={},status={},extinfo={},"
//				+ "htnonce={},httoken={}",sid,uid,orderno,amount,status,extinfoArray,htnonce,httoken);
        String orderInfo =
                " sid=" + sid + ",uid=" + uid + ",orderno=" + orderno + ",amount=" + amount
                        + ",status=" + status + ",extinfo=" + extinfoArray + ",htnonce=" + htnonce
                        + ",httoken=" + httoken;
        log.info(RECHARGR_PREFIX + "start" + orderInfo);
		if(status==null||!status.equals(SUCCESS)){
			sendToClient(response, FAIL_ORDER);
			return;//我也不知道为什么失败了还要发过来
		}
		
		// Token Rule httoken = md5(sid + uid + orderno + amount + extinfoArray +
		// htnonce+{secret}); 注：{secret}由黑桃互动提供，参数之间的 + 仅表示字符串的拼接。
		StringBuffer sb = new StringBuffer();
		sb.append(sid).append(uid).append(orderno)
		.append(amount).append(extinfoArray).append(htnonce).append(secret);
		byte[] key = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			key = md5.digest(sb.toString().getBytes());
		} catch (NoSuchAlgorithmException e) {
			sendToClient(response, TOKEN_VALIDATE_FAIL_JSON);
			return;
		}
		String myToken = byteToString(key);
		if (!myToken.equals(httoken)) {
            log.info(RECHARGR_PREFIX + "Token校验失败 myToken=" + myToken + " " + orderInfo);
			sendToClient(response, TOKEN_VALIDATE_FAIL_JSON);
			return;
		}
		
		//在这里做一个判断   查询数据库里面有没有该订单的数据  如果没有就是数据对不上  当错误处理
		String[] array = extinfoArray.split("_");
		Integer playerId = Integer.parseInt(array[0]);
		String extinfo =array[1];
		PayDBBean info = PayUtils.selectPayByPlayerIdAndOrderId(playerId, extinfo);
		if(info==null){
            log.info(RECHARGR_PREFIX + "游戏服查询  没有该订单 " + orderInfo);
			sendToClient(response, UNKNOW_ERROR);
			return;
		}
		int Pay_amount = (int)(Float.parseFloat(amount)*100);
		boolean backStatus = PayUtils.updatePayByCallBack(orderno, extinfo, 
				playerId,  Pay_amount, new Date(), extinfo,htnonce,httoken);
		if(!backStatus){
            log.info(RECHARGR_PREFIX + "插入数据库失败 " + orderInfo);
			sendToClient(response, UNKNOW_ERROR);
			return;
		}
		sendToClient(response, SUCCESS_JSON);
	}
	
	private void sendToClient( HttpServletResponse response, String content ) throws IOException{
		response.setContentType( "application/json;charset=utf-8");
		PrintWriter writer = null;
		try{
			writer = response.getWriter();
			writer.write( content );
		}finally{
			if( null != writer ) {
				writer.close();
				writer = null;
			}
		}
	}
	
	private static final int PAD_BELOW = 0x10;
	private static final int TWO_BYTES = 0xFF;
	
	 // 转换字节数组为16进制字串
 	private static String byteToString(byte[] array) {
 		StringBuffer sb = new StringBuffer(32);
 		for (int j = 0; j < array.length; ++j) {
 			int b = array[j] & TWO_BYTES;
 			if (b < PAD_BELOW)
 				sb.append('0');
 			sb.append(Integer.toHexString(b));
 		}
 		return sb.toString();
 	}
 	
 	public static void main(String[] args) {
 		String uid = "41054354";
 		String sid = "1";
 		String orderno = "1808101557041001004854EH";
 		String amount = "30";
 		String htnonce = "1533887824";
 		String secret = "68d937472760024c9925cde480ad3a05";
 		String extinfo = "20180810155704534046223";
 		//黑桃传过来的token 
// 		String httoken = "b98ade938b6fef991de16cdfc49bd700";
 		StringBuffer sb = new StringBuffer();
		sb.append(sid).append(uid).append(orderno)
		.append(amount).append(extinfo).append(htnonce).append(secret);//这里朱哥说调整了顺序 将秘钥放在最后
		byte[] key = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			key = md5.digest(sb.toString().getBytes());
		} catch (NoSuchAlgorithmException e) {
			return;
		}
		String myToken = byteToString(key);
		System.out.println("我计算出来的token=" + myToken);
	}
}
