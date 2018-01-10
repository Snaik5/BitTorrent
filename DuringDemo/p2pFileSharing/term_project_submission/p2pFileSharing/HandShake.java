package p2pFileSharing;
import java.io.*;
import java.net.*;
import java.util.BitSet;

public class HandShake implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4185516488150153858L;
	//The handshake consists of three parts: handshake header, zero bits, and peer ID
	
	String HandshakeHeader;
	byte zeroBitArray[] = new byte[10];
	
	public byte[] getZeroBitArray() {
		return zeroBitArray;
	}

	public void setZeroBitArray(byte[] zeroBitArray) {
		this.zeroBitArray = zeroBitArray;
	}

	public String getHandshakeHeader() {
		return HandshakeHeader;
	}

	public void setHandshakeHeader(String handshakeHeader) {
		HandshakeHeader = handshakeHeader;
	}

	public int getPeerID() {
		return PeerID;
	}

	public void setPeerID(int peerID) {
		PeerID = peerID;
	}

	int PeerID;
	
	public HandShake (int peerID) {
		PeerID = peerID;
	}
	
	public HandShake (String handshakeHeader, int peerID) {
		this.setHandshakeHeader(handshakeHeader);
		this.setPeerID(peerID);
		this.setZeroBitArray(new byte[] {0,0,0,0,0,0,0,0,0,0});
		//PrintMessage ();
	}
	
	public void PrintMessage () {
		System.out.println("Header message: "+ this.getHandshakeHeader() );
		System.out.println("Zero byte array: ");
		byte[] zero = this.getZeroBitArray();
		for(int i=0;i<zero.length;i++)
		{
			System.out.print(zero[i]);
		}
		
		System.out.println("Peer ID: "+ this.getPeerID());
	}
	
	public void SendHandShakeMessage (OutputStream outStrm) throws IOException {
		
		  
		ObjectOutputStream objoutStrm = new ObjectOutputStream(outStrm);  			  
		objoutStrm.writeObject(this);
		System.out.println("Sending hs with " + this.PeerID);
		
	}
	
	public int ReceiveHandShakeMessage (InputStream inpStrm) throws IOException {
		try {
			ObjectInputStream objInpStrm = new ObjectInputStream(inpStrm);  
			HandShake HandShake = (HandShake)objInpStrm.readObject();  
			if (HandShake != null) {
			
				
				return HandShake.getPeerID();
				
			}
			else {
				return -1;
			}
			
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
		}
		
		return -1;
	}
}