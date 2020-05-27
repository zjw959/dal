package net.codec.util;

import java.io.Serializable;

/**
 * 游戏协议包类，描述具体游戏数据包结构。<br>
 * <br>
 * 封包规则：一个包分为包头和包体两部分，结构如下：<br>
 * 【分隔符|包长|校验和|code】【包体】。<br>
 * 其中，包头各部分长度为2字节。检验和计算范围从code开始，直到整个包结束。
 * 
 * @author wk.dai
 */
public class ProtocolFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4651962761126269482L;

	/**
	 * 协议加密类型
	 */
	private byte encrypt = EncryptType.NONE;

	/**
	 * 标记位(7road.pitaya中描述为分隔符)
	 */
	private short flag;

	/**
	 * 数据长度，不包含自身
	 */
	private int length;

	/**
	 * 校验和
	 */
	private short checksum;

	public ProtocolFields(short flag, int length, short checksum) {
		super();
		this.flag = flag;
		this.length = length;
		this.checksum = checksum;
	}

	public ProtocolFields(byte encrypt, short flag, short length, short checksum) {
		super();
		this.encrypt = encrypt;
		this.flag = flag;
		this.length = length;
		this.checksum = checksum;
	}

	public byte getEncrypt() {
		return encrypt;
	}

	public short getFlag() {
		return flag;
	}

	public int getLength() {
		return length;
	}

	public short getChecksum() {
		return checksum;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[encrypt=");
		builder.append(encrypt);
		builder.append(", flag=");
		builder.append(flag);
		builder.append(", length=");
		builder.append(length);
		builder.append(", checksum=");
		builder.append(checksum);
		builder.append("]");
		return builder.toString();
	}

}
