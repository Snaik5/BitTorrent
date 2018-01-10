package p2pFileSharing;
import java.io.Serializable;

public class ActualMessages implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6564476501844460846L;
	/**
	 * An actual message consists of 4-byte message length field,
	 *  1-byte message type field, and a message payload with variable size
	 */
	
	int messageLength;
	int messageType;
	int senderPID;
	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getSenderPID() {
		return senderPID;
	}

	public void setSenderPID(int senderPID) {
		this.senderPID = senderPID;
	}

	public ActualMessages () {
		
	}
	
	public ActualMessages (int messageLength,int messageType, int senderPID) {
		this.messageLength = messageLength ;
		this.messageType = messageType;
		this.senderPID = senderPID;
	}	
}