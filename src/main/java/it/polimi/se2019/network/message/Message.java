package it.polimi.se2019.network.message;

import java.io.Serializable;

public class Message implements Serializable {

	private MessageType messageType;
	private MessageSubtype messageSubtype;


	public Message(MessageType messageType, MessageSubtype messageSubtype) {
		this.messageType = messageType;
		this.messageSubtype = messageSubtype;
	}


	public MessageType getMessageType() {
		return messageType;
	}

	public MessageSubtype getMessageSubtype() {
		return messageSubtype;
	}
}