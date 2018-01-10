package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class NotInterested extends ActualMessages implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1999869045818793984L;
	/**
	 * 
	 */
	
	int clientPID;
	public int getClientPID() {
		return clientPID;
	}

	public void setClientPID(int clientPID) {
		this.clientPID = clientPID;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	boolean finished=false;
	
	public NotInterested() {
		
	}
	
	public NotInterested(int msgLength, int msgType,int senderPID, int clientPID, boolean finished) {
		super(msgLength, msgType, senderPID);
		this.setFinished(finished);
		this.setClientPID(clientPID);
	}
	
	public void SendNotInterestedMsg (OutputStream outputStrm ) throws IOException {
		  
		synchronized (outputStrm) {
			ObjectOutputStream objOutputStrm = new ObjectOutputStream(outputStrm);  			  
			objOutputStrm.writeObject(this);	
		}
		
	}
	
	public boolean ReceiveNotInterestedMsg (InputStream inpStrm) throws IOException {
		
		try {
			
			ObjectInputStream objInpStrm = new ObjectInputStream(inpStrm);  
			NotInterested notIntMsg = (NotInterested)objInpStrm.readObject(); 
			
			if (notIntMsg == null) {
				return false;
			}
			else {
				return true;
			}
		} 
		catch (ClassNotFoundException e) {
			System.out.println(e);
			return false;
		}
		
	}
}
