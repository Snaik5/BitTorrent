package p2pFileSharing;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogMessageUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7205406004804739599L;
	/**
	 * 
	 */
	
	Logger loggerUtil;
	public int peerId;
	public String location = System.getProperty("user.dir") +File.separator+"p2pFileSharing"+File.separator;
	LogManager logManager = LogManager.getLogManager();
	

	public LogMessageUtil(int peerId) {
		this.peerId = peerId;
		String destination = location + "log_peer_" + peerId + ".log";
		loggerUtil = Logger.getLogger("Log for the peer " + peerId);
		logManager.addLogger(loggerUtil);
		try {
			FileHandler fileHandler = new FileHandler(destination, false);
			fileHandler.setFormatter(new SimpleFormatter());
			loggerUtil.addHandler(fileHandler);
			loggerUtil.setLevel(Level.INFO);
		} catch (IOException E) {
			E.printStackTrace();
		}
	}

	public void displayLogMessages(String LogType, int requiredPeerId, Integer[] ListOfInterestedPeerIds,
			int pieceIndex, int NumberOfPieces) {
		switch (LogType) {
		case "SendTcp":
			/*
			 * Whenever a peer makes a TCP connection to other peer, it
			 * generates the following log: [Time]: Peer [peer_ID 1] makes a
			 * connection to Peer [peer_ID 2]. [peer_ID 1] is the ID of peer who
			 * generates the log, [peer_ID 2] is the peer connected from
			 * [peer_ID 1].
			 */
			if (requiredPeerId != 0) {
				loggerUtil.log(Level.INFO, "Peer " + peerId + " makes a connection to Peer " + requiredPeerId + "\n");
			}
			break;

		case "ReceiveTcp":
			/*
			 * Whenever a peer is connected from another peer, it generates the
			 * following log: [Time]: Peer [peer_ID 1] is connected from Peer
			 * [peer_ID 2]. [peer_ID 1] is the ID of peer who generates the log,
			 * [peer_ID 2] is the peer who has made TCP connection to [peer_ID
			 * 1].
			 * 
			 */
			if (requiredPeerId != 0) {
				loggerUtil.log(Level.INFO, "Peer " + peerId + " is connected from Peer " + requiredPeerId + "\n");
			}
			break;

		case "ChangeInPrefNeighbours":
			/*
			 * Whenever a peer changes its preferred neighbors, it generates the
			 * following log: [Time]: Peer [peer_ID] has the preferred neighbors
			 * [preferred neighbor ID list]. [preferred neighbor list] is the
			 * list of peer IDs separated by comma ‘,’.
			 */
			/*if (!(ListOfInterestedPeerIds.length == 0)) {		
				String str = Arrays.toString(ListOfInterestedPeerIds);
				str = str.substring(1, str.length() - 1);
				loggerUtil.log(Level.INFO, "Peer " + peerId + " has the preferred neighbors "+str);
			}
			break;*/
			int temp = 0;
			StringBuilder str = new StringBuilder();
			String comma = "";
			if(ListOfInterestedPeerIds.length != 0)
			{
				while(temp<ListOfInterestedPeerIds.length)
				{
					str.append(comma);
					str.append(ListOfInterestedPeerIds[temp].toString());
					comma = ", ";
					temp++;
				}
				loggerUtil.log(Level.INFO, "Peer " + peerId + " has the preferred neighbors "+str.toString());
			}
			break;

		case "ChangeInOptimisticallyUnchokedNeighbor":
			/*
			 * Whenever a peer changes its optimistically unchoked neighbor, it
			 * generates the following log: [Time]: Peer [peer_ID] has the
			 * optimistically unchoked neighbor [optimistically unchoked
			 * neighbor ID]. [optimistically unchoked neighbor ID] is the peer
			 * ID of the optimistically unchoked neighbor.
			 */
			if (requiredPeerId != 0) {
				loggerUtil.log(Level.INFO,
						"Peer " + peerId + " has the optimistically unchoked neighbor " + requiredPeerId + "\n");
				loggerUtil.log(Level.INFO,
						requiredPeerId + " is the peer ID of the optimistically unchoked neighbor." + "\n");
			}
			break;

		case "Unchoked":
			/*
			 * Whenever a peer is unchoked by a neighbor (which means when a
			 * peer receives an unchoking message from a neighbor), it generates
			 * the following log: [Time]: Peer [peer_ID 1] is unchoked by
			 * [peer_ID 2]. [peer_ID 1] represents the peer who is unchoked and
			 * [peer_ID 2] represents the peer who unchokes [peer_ID 1]
			 */
			if (requiredPeerId != 0) {
				loggerUtil.log(Level.INFO, "Peer " + peerId + " is unchoked by  " + requiredPeerId + "\n");
			}
			break;

		case "Choked":
			/*
			 * Whenever a peer is choked by a neighbor (which means when a peer
			 * receives a choking message from a neighbor), it generates the
			 * following log: [Time]: Peer [peer_ID 1] is choked by [peer_ID 2].
			 * [peer_ID 1] represents the peer who is choked and [peer_ID 2]
			 * represents the peer who chokes [peer_ID 1].
			 */
			if (requiredPeerId != 0) {
				loggerUtil.log(Level.INFO, "Peer " + peerId + " is choked by  " + requiredPeerId  + "\n");
			}
			break;

		case "ReceiveHave":
			/*
			 * Whenever a peer receives a ‘have’ message, it generates the
			 * following log: [Time]: Peer [peer_ID 1] received the ‘have’
			 * message from [peer_ID 2] for the piece [piece index]. [peer_ID 1]
			 * represents the peer who received the ‘have’ message and [peer_ID
			 * 2] represents the peer who sent the message. [piece index] is the
			 * piece index contained in the message.
			 */
			if (requiredPeerId != 0) {
				loggerUtil.log(Level.INFO, "Peer " + peerId + " received the have message from  " + requiredPeerId
						+ " for the piece " + pieceIndex + "\n");
			}
			break;

		case "RecieveInterested":
			/*
			 * Whenever a peer receives an ‘interested’ message, it generates
			 * the following log: [Time]: Peer [peer_ID 1] received the
			 * ‘interested’ message from [peer_ID 2].
			 * 
			 * [peer_ID 1] represents the peer who received the ‘interested’
			 * message and [peer_ID 2] represents the peer who sent the message
			 */
			if (requiredPeerId != 0) {
				loggerUtil.log(Level.INFO,
						"Peer " + peerId + " received the interested message from  " + requiredPeerId + "\n");
			}
			break;

		case "RecieveNotInterested":
			/*
			 * Whenever a peer receives a ‘not interested’ message, it generates
			 * the following log: [Time]: Peer [peer_ID 1] received the ‘not
			 * interested’ message from [peer_ID 2]. [peer_ID 1] represents the
			 * peer who received the ‘not interested’ message and [peer_ID 2]
			 * represents the peer who sent the message.
			 */
			if (requiredPeerId != 0) {
				loggerUtil.log(Level.INFO,
						"Peer " + peerId + " received the not interested message from  " + requiredPeerId + "\n");
			}
			break;

		case "DownloadedPiece":
			/*
			 * Whenever a peer finishes downloading a piece, it generates the
			 * following log: [Time]: Peer [peer_ID 1] has downloaded the piece
			 * [piece index] from [peer_ID 2]. Now the number of pieces it has
			 * is [number of pieces]. [peer_ID 1] represents the peer who
			 * downloaded the piece and [peer_ID 2] represents the peer who sent
			 * the piece. [piece index] is the piece index the peer has
			 * downloaded. [number of pieces] represents the number of pieces
			 * the peer currently has.
			 */
			if (requiredPeerId != 0) {
				int NumberOfPiecesFile =0;
				try {
				     NumberOfPiecesFile = p2pFileSharing.FileManipulator.countPieceFolder(peerId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loggerUtil.log(Level.INFO, "Peer " + peerId + " has downloaded the piece " + pieceIndex + " from "
						+ requiredPeerId + ". Now the number of pieces it has is " + NumberOfPiecesFile + ".\n");
			}

			break;

		case "SuccessfulDownload":
			/*
			 * Whenever a peer downloads the complete file, it generates the
			 * following log: [Time]: Peer [peer_ID] has downloaded the complete
			 * file.
			 */
			loggerUtil.log(Level.INFO, "Peer " + peerId + " has downloaded the complete file.");
			break;
		}
	}
}
