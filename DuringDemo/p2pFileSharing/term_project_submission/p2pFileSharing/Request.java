package p2pFileSharing;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/*
 * ‘request’ messages have a payload which consists of a 4-byte piece index field
 */
public class Request extends ActualMessages{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5705539337942180418L;
	int reqMsgPayload;
	
	public int getReqMsgPayload() {
		return reqMsgPayload;
	}

	public void setReqMsgPayload(int reqMsgPayload) {
		this.reqMsgPayload = reqMsgPayload;
	}

	public Request () {
		
		
	}
	
	public Request (int msgLength, int msgType,int senderPID, int reqMsgPayload) {
		super (msgLength, msgType,senderPID);
		this.reqMsgPayload = reqMsgPayload;
	}
	
	public void SendRequestMsg (OutputStream outStrm) throws IOException {

		synchronized (outStrm) {
			ObjectOutputStream objOutStrm = new ObjectOutputStream(outStrm);  			  
			objOutStrm.writeObject(this);
		}
		
	}
	
	public int ReceiveRequestMsg (InputStream inpStrm) throws IOException {
		
		try {
			ObjectInputStream objInpStrm = new ObjectInputStream(inpStrm);  
			Request reqMsg = (Request)objInpStrm.readObject(); 
			
			if (reqMsg != null) {
				return reqMsg.getReqMsgPayload();
			}
			else {
				return -1;
			}
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
			return -1;
		}
		finally {
			//is.close();
			//ois.close();
		}
	}
	

}
