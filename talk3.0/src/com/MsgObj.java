package com;

import java.io.Serializable;

/**
 * @author bocai
 * 
 */
public class MsgObj implements Serializable {
	short Msgtype;
	String sex;
	String sender;
	String getter;
	String content;

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public short getMsgtype() {
		return Msgtype;
	}

	public void setMsgtype(short msgtype) {
		Msgtype = msgtype;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getGetter() {
		return getter;
	}

	public void setGetter(String getter) {
		this.getter = getter;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
