package p2pFileSharing;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class peerProcess extends Thread implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2335685548731132220L;

	static Hashtable<Integer, SetupClientConnection> clientmap = new Hashtable<Integer, SetupClientConnection>();
	
	int serverPeerID;
	
	boolean checkIfClient = false;
	ServerSocket serverSocket;
	public HashSet<SetupServerConnection> serverConSet = new HashSet<SetupServerConnection>();
	
	int myPeerID;
	int optSelectedPeerID;
	public HashSet<Integer> receivedBytePos = new HashSet<Integer>();
	static HashMap<String, String> commonProperties = ConfigProperties.getCommonConfig();
	Vector<PeerDetails> peerVector;
	int peerVectorLen;
	public BitFieldMessage myBitFields;
	public ArrayList<Integer> haveList = new ArrayList<Integer>();
	public HashSet<Integer> neededBytePos = new HashSet<Integer>();
	public HashSet<Integer> PreferredNeighborsSet = new HashSet<Integer>();
	public HashSet<Integer> interestedPeersSet = new HashSet<Integer>();
	Set<Integer> InterestedPeersList = Collections.synchronizedSet(interestedPeersSet);
	public HashSet<Integer> requestedBytePos = new HashSet<Integer>();
	static Hashtable<Integer, Integer> pieceDownloadList;
	
	ArrayList<Thread> startedThreads = new ArrayList<Thread>();
	public LogMessageUtil log;
	
	public peerProcess(int peerID) throws UnknownHostException, IOException {
		this.serverPeerID = peerID;
		this.myPeerID = peerID;
		establishNetwork();
	}

	public static void main(String[] args) {

		int myPeerId = Integer.parseInt(args[0]);
		//int myPeerId = 1002;
		FileManipulator.directoryCreate(myPeerId);
		peerProcess mainPeerProcess;
		try {
			mainPeerProcess = new peerProcess(myPeerId);

			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			PeerProcessHelper.schedulerCalls(mainPeerProcess);
			for (int i = 0; i < mainPeerProcess.startedThreads.size(); i++) {
				try {
					mainPeerProcess.startedThreads.get(i).join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.println("Exiting");

			while (args != null)
				;
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			System.out.println("Unknown Host Exception");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("IOException encountered");
		}

		// serverSock.close();
	}

	private InetAddress getInetaddress() throws SocketException, UnknownHostException {
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		HashSet<String> myIPs = new HashSet<>();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration<InetAddress> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress add = (InetAddress) ee.nextElement();
				myIPs.add(add.getHostAddress());

			}
		}

		ArrayList<String> IPAddressesFromPeerInfo = new ArrayList<>();

		for (int k = 0; k < peerVectorLen; k++) {
			IPAddressesFromPeerInfo.add(peerVector.get(k).peerIpAddress);
		}
		int pos = 0;
		for (int k = 0; k < IPAddressesFromPeerInfo.size(); k++) {
			if (myIPs.contains(IPAddressesFromPeerInfo.get(k))) {

				pos = k;
				break;
			}
		}

		System.out.println(peerVector.get(pos).peerProcessId + " " + peerVector.get(pos).peerIpAddress + " "
				+ peerVector.get(pos).peerPortNo + " " + peerVector.get(pos).hasFile);

		System.out.println(
				"NumberOfPreferredNeighbors" + ConfigProperties.getCommonConfig().get("NumberOfPreferredNeighbors"));
		InetAddress inetAddress = InetAddress.getByName(peerVector.get(pos).peerIpAddress);
		return inetAddress;
	}

	void establishNetwork() throws UnknownHostException, IOException {
		startedThreads = new ArrayList<Thread>();
		this.peerVector = ConfigProperties.getPeerInfo();
		this.peerVectorLen = this.peerVector.size();
		int clientPeerId = 0;
		PeerDetails peerdetail;
		this.log = new LogMessageUtil(this.myPeerID);
		for (int i = 0; i < peerVectorLen; i++) {

			peerdetail = (PeerDetails) this.peerVector.elementAt(i);
			clientPeerId = Integer.parseInt(peerdetail.getPeerProcessId());

			if (clientPeerId == this.myPeerID) {

				serverSetUp(peerdetail);
				

			} else {

				try {
					if (clientmap.containsKey(clientPeerId)) {
						return;
					}
					BitFieldMessage bitField = new BitFieldMessage(4, 5, this.myPeerID);
					bitField.intializeBitFieldMessage(this.myPeerID, this);
					this.myBitFields = bitField;
					clientSetUp(clientPeerId, peerdetail);
					
					// this.log.TCP_send(clientPeerId);
				} catch (IOException e) {
					// e.printStackTrace();
					System.out.println("connection failed: " + myPeerID);
				}

			}
		}

	}

	/**
	 * @param peerdetail
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public void serverSetUp(PeerDetails peerdetail)
			throws NumberFormatException, IOException, SocketException, UnknownHostException {
		int peerPort = Integer.parseInt(peerdetail.getPeerPortNo());
		this.serverSocket = new ServerSocket(peerPort, 0, getInetaddress());
		Thread ServerThread = new Thread(new SetupServerConnection(this.myPeerID, this));
		ServerThread.start();
	}

	/**
	 * @param clientPeerId
	 * @param peerdetail
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public void clientSetUp(int clientPeerId, PeerDetails peerdetail)
			throws UnknownHostException, IOException, NumberFormatException {
		System.out.println("Going to connect to " + clientPeerId + " me " + peerdetail.getPeerIpAddress() + " "
				+ peerdetail.getPeerPortNo() + " " + myPeerID);
		Socket clientSock = new Socket(peerdetail.getPeerIpAddress(), Integer.parseInt(peerdetail.getPeerPortNo()));
		SetupClientConnection clientCon = new SetupClientConnection(clientSock, this.myPeerID,
				Integer.parseInt(peerdetail.getPeerProcessId()), this);
		Thread ClientThreads = new Thread(clientCon);
		ClientThreads.start();
		this.log.displayLogMessages(LogConstants.SEND_TCP, clientPeerId, null, 0, 0);
		this.checkIfClient = true;
		clientmap.put(clientPeerId, clientCon);
		System.out.println("Put in map client Thread" + clientPeerId);
		startedThreads.add(ClientThreads);
	}
}