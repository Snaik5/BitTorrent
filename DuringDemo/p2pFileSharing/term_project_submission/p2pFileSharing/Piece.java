package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Piece extends ActualMessages{
	
	private static final long serialVersionUID = -4414909703676606839L;
	

	/**
	 * piece messages have a payload which consists of a 4-byte piece index field and the content of the piece.
	 */
	
	int pieceIndex;
	ArrayList<Integer> contentOfPiece;
	public int getPieceIndex() {
		return pieceIndex;
	}

	public void setPieceIndex(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}

	public ArrayList<Integer> getContentOfPiece() {
		return contentOfPiece;
	}

	public void setContentOfPiece(ArrayList<Integer> contentOfPiece) {
		this.contentOfPiece = contentOfPiece;
	}

	public int getPeerId() {
		return peerId;
	}

	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}

	int peerId;
	
	public Piece() {
		
	}
	
	public Piece (int msgLen, int msgType, int senderPid,int pieceIndex, ArrayList<Integer> contentOfPiece) {
		super (msgLen, msgType,senderPid);
		this.setPieceIndex(pieceIndex);
		this.setContentOfPiece(contentOfPiece);
	}
	
	public void SendPieceMsg (OutputStream outStrm) throws IOException {
		
		System.out.println("piece sending ");
		ObjectOutputStream objOutStrm;
		synchronized (outStrm) {
			objOutStrm = new ObjectOutputStream(outStrm);  			  
			objOutStrm.writeObject(this);
		}
		System.out.println("exiting send piece ");
		
	}
	
	public int ReceivePieceMsg (InputStream inpStrm) throws IOException {
		
		try {
			ObjectInputStream objInpStrm = new ObjectInputStream(inpStrm);  
			Piece pieceMsg = (Piece)objInpStrm.readObject(); 
			
			if (pieceMsg == null) {
				return -1;
				
			}
			else {
				return pieceMsg.getPieceIndex();
			}
		} 
		catch (ClassNotFoundException e) {
			System.out.println(e);
			return -1;
		}
		
	}

}
