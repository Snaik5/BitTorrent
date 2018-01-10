package p2pFileSharing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ClientMessageHelper {

	public void clientOtherwise() {
		System.exit(0);
	}

	public void clientPieceProcess(Object obj, SetupClientConnection clientConObj) throws IOException {
		FileManipulator file = new FileManipulator();
		Piece piecemsg = (Piece) obj;

		// writing the piece
		file.formPiece(piecemsg.getContentOfPiece(), piecemsg.getPieceIndex(),  clientConObj.clientPeerID);

		System.out.println("Obtained piece with piece index:" + piecemsg.getPieceIndex());
		// updating the bitfield message
		synchronized (clientConObj.peerProcessObj.myBitFields) {
			if (!(clientConObj.peerProcessObj.myBitFields.contains(piecemsg.getPieceIndex()))) {
				clientConObj.peerProcessObj.myBitFields.setBitToOne (piecemsg.getPieceIndex());
			}
		}

		// update the have list with the index of the piece obtained
		synchronized (clientConObj.peerProcessObj.haveList) {
			clientConObj.peerProcessObj.haveList.add(piecemsg.getPieceIndex());
		}

		// update the needed bytes by removing the piece index obtained
		synchronized (clientConObj.peerProcessObj.neededBytePos) {
			clientConObj.peerProcessObj.neededBytePos.remove(piecemsg.getPieceIndex());
		}

		synchronized (clientConObj.peerProcessObj.neededBytePos) {
			synchronized (clientConObj.peerProcessObj.receivedBytePos) {
				// creating for temporary checks
				HashSet<Integer> forCheck = new HashSet<Integer>(clientConObj.peerProcessObj.neededBytePos);

				if (forCheck.isEmpty()) {
					terminateOnReceivingAllPieces(clientConObj);
				}

				else {
					requestingPiece(clientConObj, piecemsg);
				}
			}

		}
	}

	public void requestingPiece(SetupClientConnection clientConObj, Piece pmsg) throws IOException {
		int pieceIndex = -1;
		ResourceMaintainance resmain = new ResourceMaintainance();
		synchronized (clientConObj.peerProcessObj.neededBytePos) {
			pieceIndex = resmain.getPieceIndex(clientConObj.peerProcessObj.neededBytePos);
		}

		System.out.println("Sending req for: " + pieceIndex);
		
		//clientConObj.pObj.log.downloadedPiece(clientConObj.peerID, pieceIndex, clientConObj.pObj.receivedByteIndex.size());
		Request reqmsg1 = new Request(4, 6,clientConObj.peerID, pieceIndex);
		reqmsg1.SendRequestMsg(clientConObj.out);

		System.out.println("Sent req with pieceIndex: " + pieceIndex);
		synchronized (peerProcess.clientmap) {

			for (Integer key : peerProcess.clientmap.keySet()) {
				System.out.println("Sending have message to:" + key);
				clientConObj.peerProcessObj.log.displayLogMessages(LogConstants.DOWNLOADED_PIECE, pmsg.getSenderPID(), null, pmsg.getPieceIndex(), clientConObj.peerProcessObj.receivedBytePos.size());
				Have havemsg = new Have(4, 4, clientConObj.peerID, pmsg.getPieceIndex());
				havemsg.SendHaveMsg(peerProcess.clientmap.get(key).out);
			}
		}
	}

	public void terminateOnReceivingAllPieces(SetupClientConnection clientConObj) throws IOException {
		// Terminate when client has received all the pieces.
		FileManipulator file = new FileManipulator();
		file.configReading();
		file.formFile(file.fileToBeShared, file.pieceCount, clientConObj.clientPeerID);
		// remove from list of interested peers
		clientConObj.peerProcessObj.InterestedPeersList.remove(clientConObj.clientPeerID);
		// sends a Not Interested message
		clientConObj.peerProcessObj.log.displayLogMessages(LogConstants.SUCCESSFUL_DOWNLOAD, 0, null,0, 0);
		//clientConObj.pObj.log.completedDownload();
		clientConObj.finished = true;
	}

	public void clientRequestProcess(Object obj, SetupClientConnection clientConObj) throws IOException {
		// Get request and send piece
		Request reqMsg = (Request) obj;
		int pieceIndex = reqMsg.getReqMsgPayload();

		System.out.println("Got piece request for:" + pieceIndex);

		if (pieceIndex != -1) {
			// Send piece message.
			FileManipulator file = new FileManipulator();
			ArrayList<Integer> filePiece = file.readPiece(pieceIndex, clientConObj.peerID);
			Piece pmsg = new Piece(4, 7, clientConObj.peerID,pieceIndex, filePiece);
			pmsg.SendPieceMsg(clientConObj.out);
			System.out.println("Sent piece:" + pieceIndex);
			System.out.println("Ending piece message transferring");
		}
	}

	public void clientHaveProcess(Object obj, SetupClientConnection clientConObj) throws IOException {

		int indexPos = ((Have) obj).getPieceIndex();
		// before processing checking if in error state
		if (indexPos == -1) {
			System.out.println("Have message has not been received properly");
		} else {

			
			//clientConObj.pObj.log.receivedHave(clientConObj.clientPeerID, indexPos);
			clientConObj.serverPeerBitFieldMsg.setBitToOne (indexPos);
			if (clientConObj.myBitFields.getBitFieldPayload()[indexPos] == true) {
				// I have this and dont want this piece. send not interested
				NotInterested notIntrstmesg = new NotInterested(0, 3,clientConObj.peerID, clientConObj.clientPeerID, false);
				notIntrstmesg.SendNotInterestedMsg(clientConObj.out);
				clientConObj.peerProcessObj.log.displayLogMessages(LogConstants.RCV_HAVE, notIntrstmesg.getClientPID(), null, indexPos, 0);

			} else {
				// I want this!
				Interested intrstMsg = new Interested(0, 2,clientConObj.peerID, clientConObj.clientPeerID);
				intrstMsg.SendInterestedMsg(clientConObj.out);
			}
		}
	}

	public void clientNotInterestedProcess(Object obj, SetupClientConnection clientConObj) {
		NotInterested notIntrstmesg = (NotInterested) obj;
		clientConObj.clientPeerID = notIntrstmesg.getClientPID();
		peerProcess temp = clientConObj.peerProcessObj;
		
		//clientConObj.pObj.log.receivedNotInterested(clientConObj.clientPeerID);

		if (notIntrstmesg.finished == true) {
			if (temp.InterestedPeersList.contains(clientConObj.clientPeerID)) {
				clientConObj.peerProcessObj.InterestedPeersList.remove(clientConObj.clientPeerID);
				clientConObj.peerProcessObj.log.displayLogMessages(LogConstants.RCV_NOT_INTERESTED, notIntrstmesg.getSenderPID(), null, 0, 0);
			}
		}

	}

	public void clientInterestedProcess(Object obj, SetupClientConnection clientConObj) {
		peerProcess temp = clientConObj.peerProcessObj;
		//clientConObj.pObj.log.receivedInterested(clientConObj.clientPeerID);

		synchronized (clientConObj.peerProcessObj.InterestedPeersList) {
			System.out.println("Adding to ilist " + clientConObj.clientPeerID);
			clientConObj.peerProcessObj.InterestedPeersList.add(clientConObj.clientPeerID);
			
			if(clientConObj.peerProcessObj.checkIfClient == false)
				clientConObj.peerProcessObj.log.displayLogMessages(LogConstants.RCV_INTERESTED, clientConObj.clientPeerID, null, 0, 0);
			else
				clientConObj.peerProcessObj.log.displayLogMessages(LogConstants.RCV_INTERESTED,((Interested)obj).getSenderPID(), null, 0, 0);
		}
		
	}

	public void clientUnchokeProcess(SetupClientConnection clientConObj) throws IOException {
		ResourceMaintainance resmain = new ResourceMaintainance();
		int pieceIndex = 0;
		synchronized (clientConObj.peerProcessObj.neededBytePos) {

			pieceIndex = resmain.getPieceIndex(clientConObj.peerProcessObj.neededBytePos);
		}
		System.out.println("Sending request for " + pieceIndex);
		Request reqmsg1 = new Request(4, 6,clientConObj.peerID, pieceIndex);
		reqmsg1.SendRequestMsg(clientConObj.out);
	}

}
