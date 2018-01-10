package p2pFileSharing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ServerMessageHelper {
	public void serverOtherwise() {
		System.exit(0);
	}

	public void serverRequestProcess(Object obj, SetupServerConnection serverConObj) throws IOException {
		// Receive the request message.
		Request reqmsg = (Request) obj;
		int pieceIndex = reqmsg.getReqMsgPayload();

		if (pieceIndex != -1) {
			if (serverConObj.peerProcessObject.PreferredNeighborsSet.contains(serverConObj.clientPeerID)) {
				updateHaveList(serverConObj, pieceIndex);
			} else if (serverConObj.peerProcessObject.optSelectedPeerID == serverConObj.clientPeerID) {
				// then also update
				updateHaveList(serverConObj, pieceIndex);
			}

			else {

				serverConObj.peerProcessObject.log.displayLogMessages(LogConstants.CHOKED, serverConObj.clientPeerID,
						null, 0, 0);
				// serverConObj.pObj.log.Choked(serverConObj.cPeerID);

				while (!(serverConObj.peerProcessObject.PreferredNeighborsSet.contains(serverConObj.clientPeerID))
						|| (serverConObj.peerProcessObject.optSelectedPeerID != serverConObj.clientPeerID))
					serverConObj.peerProcessObject.log.displayLogMessages(LogConstants.UNCHOKED,
							serverConObj.clientPeerID, null, 0, 0);

				// serverConObj.pObj.log.Unchoked(serverConObj.cPeerID);
			}
		}
	}

	public void updateHaveList(SetupServerConnection serverConObj, int pieceIndex) {
		HashSet<Integer> localReceivedByteIndex;
		localReceivedByteIndex = serverConObj.peerProcessObject.receivedBytePos;

		ResourceMaintainance resmain = new ResourceMaintainance();
		ArrayList<Integer> haveList = resmain.prepareHaveList(serverConObj.peerProcessObject.receivedBytePos,
				localReceivedByteIndex);
		for (int i = 0; i < haveList.size(); i++) {

			localReceivedByteIndex.add(haveList.get(i));
		}

		// Send piece message moved, but printing here

		System.out.println("Sent piece:" + pieceIndex);
		System.out.println("end of piece msg transfer");
	}

	public void serverNotInterestedProcess(Object obj, SetupServerConnection serverConObj) {
		serverConObj.notInterested = true;
		serverConObj.clientPeerID = ((NotInterested) obj).getClientPID();

		// serverConObj.pObj.log.receivedNotInterested(serverConObj.cPeerID);
		if (((NotInterested) obj).finished == true) {
			if (serverConObj.peerProcessObject.InterestedPeersList.contains(serverConObj.clientPeerID))
				serverConObj.peerProcessObject.InterestedPeersList.remove(serverConObj.clientPeerID);
			serverConObj.peerProcessObject.log.displayLogMessages(LogConstants.RCV_NOT_INTERESTED,
					((NotInterested) obj).getClientPID(), null, 0, 0);
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void serverInterestedProcess(Object obj, SetupServerConnection serverConObj) {
		Interested fromClientIntMsg = (Interested) obj;
		serverConObj.interested = true;
		serverConObj.clientPeerID = fromClientIntMsg.getClientPID();

		// serverConObj.pObj.log.receivedInterested(serverConObj.cPeerID);
		serverConObj.peerProcessObject.InterestedPeersList.add(serverConObj.clientPeerID);

		while (!(serverConObj.peerProcessObject.PreferredNeighborsSet.contains(serverConObj.clientPeerID))
				|| (serverConObj.peerProcessObject.optSelectedPeerID != serverConObj.clientPeerID)) {
			serverConObj.peerProcessObject.log.displayLogMessages(LogConstants.RCV_INTERESTED,
					fromClientIntMsg.getClientPID(), null, 0, 0);
		
		}

		// serverConObj.pObj.log.Unchoked(serverConObj.cPeerID);
	}

}
