package com.kevin.wallet.btc.model;

import java.util.List;

public class MultiSignTxReq {
	private byte[] rawTransaction;				//原始交易体
	private List<byte[]> privateKey;			//签名私钥列表
	private String redeemScript;				//赎回脚本

	public MultiSignTxReq() {}

	public byte[] getRawTransaction() {
		return rawTransaction;
	}

	public void setRawTransaction(byte[] rawTransaction) {
		this.rawTransaction = rawTransaction;
	}

	public List<byte[]> getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(List<byte[]> privateKey) {
		this.privateKey = privateKey;
	}

	public String getRedeemScript() {
		return redeemScript;
	}

	public void setRedeemScript(String redeemScript) {
		this.redeemScript = redeemScript;
	}
}