package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

class SetupClientConnection implements Runnable {

	public static final String P2PFILESHARINGPROJ = "P2PFILESHARINGPROJ";
	
	int peerID;
	int clientPeerID;
	peerProcess peerProcessObj;
	int portNumber;
	String hostName;
	
	InputStream in;
	OutputStream out;
	
	Socket clientSocket;
	Hashtable<Integer, SetupClientConnection> clientmap;
	
	BitFieldMessage myBitFields;
	BitFieldMessage serverPeerBitFieldMsg;
	
	
	Boolean finished = false;
	Boolean initialStage = false;
	ActualMessages actMsg = new ActualMessages();
	
	public SetupClientConnection(Socket socket, int mypeer_id, int peer_id, peerProcess peerProcessObj) throws IOException {
		 // Server's peer ID.
		
		// Client's peer ID
		this.clientSocket = socket;
		this.peerID = mypeer_id;
		this.clientPeerID = peer_id; 
		this.peerProcessObj = peerProcessObj;
		this.myBitFields = peerProcessObj.myBitFields;
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
		
	}
	

	public SetupClientConnection() {

	}

	public void run() {

		try {
			this.finished = false;
			//sockets for started clients
		
			 handShakeProcess();
			// Sending bitfield
			 bitFieldsProcess();
			CMessageHandler cmesgHandler = new CMessageHandler();			
			HashSet<Integer> localReceivedByteIndex = new HashSet<Integer>();
			Object readObj = null;
			while (true) {
				System.out.println("Client: Listening for messages");
				readObj = cmesgHandler.listenForMessages(this.in, this);
				System.out.println("Got message");
				int msgType = this.actMsg.getMessageType();
				
				cmesgHandler.HandleMessages(msgType, readObj, this, localReceivedByteIndex);
				readObj = null;

				// Thread.sleep(1000);

				if (this.finished == true) {
					NotInterested ntIm = new NotInterested(0, 3,peerID,clientPeerID, true);
					ntIm.SendNotInterestedMsg(this.out);
					break;
				}
			}

			System.out.println("Closing client socket");
			clientSocket.close();

			return;
		} catch (IOException ex) {
			System.out.println("IOException occured:" + ex);
		}
	}

	/**
	 * @throws IOException
	 */
	public void bitFieldsProcess() throws IOException {
		if(this.out!=null)
		{
		myBitFields.sendBitField(this.out);
		}
		// Now reveive a bitfield message from server.
		BitFieldMessage receiveBMsg = new BitFieldMessage();
		BitFieldMessage returnBMsg;

		returnBMsg = receiveBMsg.receiveBitField(this.in);

		this.serverPeerBitFieldMsg = returnBMsg;
	
		HashSet<Integer> res = myBitFields.receivedBitFieldsCompare(returnBMsg);

		if (res != null && res.size() != 0) {
			// send interested message.
			Interested intrstMsg = new Interested(0, 2,peerID, clientPeerID);
			intrstMsg.SendInterestedMsg(this.out);

		} else {

			// send not interested message.
			NotInterested notintrstMsg = new NotInterested(0, 3, peerID,clientPeerID, false);
			notintrstMsg.SendNotInterestedMsg(this.out);

		}
	}

	/**
	 * @throws IOException
	 */
	public void handShakeProcess() throws IOException {
		HandShake handshake = new HandShake(P2PFILESHARINGPROJ, peerID);
		handshake.SendHandShakeMessage(this.out);
		
		if ((this.clientPeerID = handshake.ReceiveHandShakeMessage(this.in)) != -1) {
			System.out.println("Handshake success ");

			if (!peerProcess.clientmap.contains(this.clientPeerID)) {
				System.out.println("Server side: Putting in map " + clientPeerID);
				peerProcess.clientmap.put(this.clientPeerID, this);
			}

		} else {
			System.out.println("Handshake Failed");
		}
	}

}