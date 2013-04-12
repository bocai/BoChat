package com;
import java.io.Serializable;

/**
 * @author bob
 *
 */
public class MsgObj implements Serializable {
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	short Msgtype;
	String  sex;
	String sender;
	String getter;
	String content;
	
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
