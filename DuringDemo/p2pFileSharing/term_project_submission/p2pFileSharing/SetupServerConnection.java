package p2pFileSharing;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

class SetupServerConnection implements Runnable, Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2773024189278957199L;
	public Socket connectionSocket;
	ServerSocket serverSocket;

	int PeerID;
	int clientPeerID;

	peerProcess peerProcessObject;
	
	BitFieldMessage myBitFields;
	BitFieldMessage clientPeerBitFieldMsg;
	
	boolean interested;
	boolean notInterested;
	
	public Interested interestedMesg = new Interested();
	public ActualMessages actualMesg = new ActualMessages();
	
	int peerPort;
	boolean initialStage = false;

	public SetupServerConnection(int peerId, peerProcess peerProcess) {
		this.PeerID = peerId; // My peer ID.
		this.peerProcessObject = peerProcess;
	}

	public void run() {
		boolean isActivated = true;
		try {
			while (isActivated) {
				serverEstablish();
			}

		} catch (SocketException e) {
			e.printStackTrace();
			isActivated = false;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void serverEstablish() throws UnknownHostException, IOException {
		System.out.println("server getting started");
		Socket socket = this.peerProcessObject.serverSocket.accept();
		SetupClientConnection clientConObj = new SetupClientConnection(socket, this.PeerID, this.PeerID,
				this.peerProcessObject);
		Thread listenThread = new Thread(clientConObj);
		listenThread.start();
		this.peerProcessObject.log.displayLogMessages(LogConstants.RCV_TCP,
				this.clientPeerID, null, 0, 0);
		// peerProcessObj.log.TCP_rcv(this.cPeerID);
		peerProcessObject.startedThreads.add(listenThread);
	}

}
