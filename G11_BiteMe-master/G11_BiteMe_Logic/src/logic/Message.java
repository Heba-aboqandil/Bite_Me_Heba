package logic;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	MessageType type;
	ArrayList<Object> object;

	public Message(MessageType type, ArrayList<Object> object) {
		this.type = type;
		this.object = object;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(ArrayList<Object> object) {
		this.object = object;
	}

}
