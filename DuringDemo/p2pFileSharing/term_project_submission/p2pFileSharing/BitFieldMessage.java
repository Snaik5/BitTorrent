package p2pFileSharing;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.net.*;

class BitFieldMessage extends ActualMessages implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = -5246113979033792315L;
/*
 * ‘bitfield’ messages is only sent as the first message right after handshaking is done when a connection is established. 
 * ‘bitfield’ messages have a bitfield as its payload.
 * Peers that don’t have anything yet may skip a ‘bitfield’ message.
 */
	
	boolean[] bitFieldPayload;
	boolean skippedBitField;
	
	public boolean isSkippedBitField() {
		return skippedBitField;
	}

	public void setSkippedBitField(boolean skippedBitField) {
		this.skippedBitField = skippedBitField;
	}

	public boolean[] getBitFieldPayload() {
		return bitFieldPayload;
	}

	public void setBitFieldPayload(boolean[] bitFieldPayload) {
		this.bitFieldPayload = bitFieldPayload;
	}
	
	public BitFieldMessage () {
		super();
	}
	
	public BitFieldMessage(int msgLen, int msgType, int senderPeerId) {
		super(msgLen, msgType,senderPeerId);
	}
	
    public synchronized boolean contains (int index) {
		
		if (this.getBitFieldPayload()[index]==true) {
			return false;
		}
		else {
			return true;
		}
	}
    
	public synchronized void setBitToOne (int index) {
		this.getBitFieldPayload()[index]=true;	
	}
	
	public void intializeBitFieldMessage (int myPeerId, peerProcess peerProcess) throws IOException{

		FileManipulator file= new FileManipulator();
		file.configReading();
		this.setBitFieldPayload(new boolean[file.pieceCount]);
		
		fillBitFieldPayload(myPeerId, peerProcess, file);
	}
	
	public synchronized void sendBitField (OutputStream outputStream) throws IOException {
			
		if(this.getBitFieldPayload() !=null){
			synchronized (outputStream) {
				ObjectOutputStream objectOutStream = new ObjectOutputStream(outputStream); //possible cause of error :P  
				objectOutStream.writeObject(this);	
			}			
		}
	}
	
	public BitFieldMessage receiveBitField (InputStream inputStream ) throws IOException {
		
		try {
			
			ObjectInputStream objInpStream = new ObjectInputStream(inputStream);
			Object obj = objInpStream.readObject();
			
			if (!(obj instanceof BitFieldMessage)) {
				return null;
			}
			else {
			
				BitFieldMessage BitFieldMsg = (BitFieldMessage)obj;  
				System.out.println("Bitfield message type:"+ BitFieldMsg.getMessageType());
				if (BitFieldMsg.getBitFieldPayload() != null) {
					System.out.println("BitField message received");
					return BitFieldMsg;
				}
				else {
					System.out.println("BitField message not received");
					return null;
				}
			}
		} 
		catch (ClassNotFoundException e) {
			System.out.println(e);
			return null;
		}
		
	}
	
	// create list of piece indexes that the client peer has and the this server peer does not have
	public synchronized HashSet<Integer> receivedBitFieldsCompare (BitFieldMessage receivedBit) {
		if(receivedBit==null){
			return null;
		}
		boolean[] received = receivedBit.getBitFieldPayload();
		boolean[] mine = this.getBitFieldPayload();
		
		HashSet<Integer> pieceIndexList = new HashSet<Integer>();
		
		System.out.println("sizes = " + mine.length + " " + received.length);
		for (int i = 0; i < received.length; i++) {
			System.out.print(received[i] + "      " + mine[i]);
		}
		
		System.out.println();
		//System.out.println("Msg length:"+bMsg1.length);
		
		for (int i=0; i < mine.length; i++) {
			
			if (received[i]==true && mine[i] == false) {
				pieceIndexList.add(i);
			}
		}
		
		return pieceIndexList;
	}
	
	public void fillBitFieldPayload(int myPeerID, peerProcess peerProcess, FileManipulator file)
			throws IOException {
		if (file.fileCheck(myPeerID)){
			Arrays.fill(this.getBitFieldPayload(), true);
			file.fileSplit(file.fileToBeShared, file.fileSize, file.pieceSize, myPeerID);
			this.setSkippedBitField(false);
		}
		else {
			Arrays.fill(this.getBitFieldPayload(), false);
			this.setSkippedBitField(true);
			
			if (peerProcess.neededBytePos.size() == 0) {
				for (int i = 0; i < file.pieceCount; i++) {
					peerProcess.neededBytePos.add(i);
				}
			}
		}
	}

	
	
}
