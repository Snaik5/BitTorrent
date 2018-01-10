package p2pFileSharing;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
/*
 * ‘have’ messages have a payload that contains a 4-byte piece index field
 */
public class Have extends ActualMessages {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7767594193706382309L;
	int pieceIndex;
	
	public int getPieceIndex() {
		return pieceIndex;
	}

	public void setPieceIndex(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}

	public Have () {
		
	}
	
	public Have (int msgLength, int msgType,int senderPID, int pieceIndex) {
		super (msgLength, msgType, senderPID);
		this.setPieceIndex(pieceIndex);
	}
	
	public void SendHaveMsg (OutputStream outputStrm) throws IOException {
		synchronized (outputStrm) {
			ObjectOutputStream objOutputStrm = new ObjectOutputStream(outputStrm);  			  
			objOutputStrm.writeObject(this);	
		}
		
	}
	
	public int ReceiveHaveMsg (InputStream inputStream ) throws IOException {
		
		try {
			ObjectInputStream objInpStream = new ObjectInputStream(inputStream);  
			Have haveMsg = (Have)objInpStream.readObject(); 
			
			if (haveMsg != null) {
				return haveMsg.getPieceIndex();
			}
			else {
				return -1;
			}
		} 
		catch (ClassNotFoundException e) {
			System.out.println(e);
			return -1;
		}
		
	}
}
