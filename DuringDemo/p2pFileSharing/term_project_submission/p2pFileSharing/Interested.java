package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Interested extends ActualMessages implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3785441522803009791L;
	int clientPID;
	
	public Interested () {

	}
	
	public int getClientPID() {
		return clientPID;
	}

	public void setClientPID(int clientPID) {
		this.clientPID = clientPID;
	}

	
	public Interested (int msgLength, int msgType, int senderPID,int clientPID) {
		super(msgLength, msgType,clientPID);
		this.setClientPID(clientPID);
	}
	
	public void SendInterestedMsg (OutputStream outputStream) throws IOException {
		ObjectOutputStream objOutStrm;
		synchronized (outputStream) {
			objOutStrm = new ObjectOutputStream(outputStream);  			  
			objOutStrm.writeObject(this);
		}
	}
	
	public boolean ReceiveInterestedMsg (InputStream inpStrm) throws IOException {
		
		try {
			ObjectInputStream objInpStrm = new ObjectInputStream(inpStrm);  
			Interested intrstMsg = (Interested)objInpStrm.readObject(); 
			
			if (intrstMsg == null) {
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
