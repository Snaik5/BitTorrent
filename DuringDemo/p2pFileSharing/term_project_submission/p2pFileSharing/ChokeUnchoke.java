package p2pFileSharing;

import java.io.IOException;
import java.util.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChokeUnchoke extends ActualMessages {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6319025711068732705L;

	/**
	 * 
	 */

	public ChokeUnchoke() {

	}
	
	public boolean ReceiveChokeUnchokeMsg(InputStream inpStrm) throws IOException {

		try {

			ObjectInputStream objInpStrm = new ObjectInputStream(inpStrm);
			ChokeUnchoke chokeunchoke = (ChokeUnchoke) objInpStrm.readObject();

			if (chokeunchoke != null) {
				return true;
			} else {
				return false;
			}
		} catch (ClassNotFoundException ex) {
			System.out.println(ex);
			return false;
		} finally {
			// is.close();
			// ois.close();
		}
	}

	public ChokeUnchoke(int MsgLen, int MsgType, int senderPid) {
		super(MsgLen, MsgType, senderPid);
	}

	public void SendUnchokeMessage(OutputStream outputStrm) throws IOException {

		synchronized (outputStrm) {
			ObjectOutputStream objOutputStrm = new ObjectOutputStream(outputStrm);
			objOutputStrm.writeObject(this);
		}

	}

	public void SendChokeMessage(OutputStream outputStrm) throws IOException {

		synchronized (outputStrm) {
			ObjectOutputStream objOutputStrm = new ObjectOutputStream(outputStrm);
			objOutputStrm.writeObject(this);
		}
	}

	

}
