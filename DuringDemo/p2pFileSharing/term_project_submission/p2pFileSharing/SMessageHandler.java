package p2pFileSharing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class SMessageHandler {

	public void HandleMessages(int MsgType, Object obj, SetupServerConnection serverConObj,
			HashSet<Integer> localReceivedByteIndex) throws IOException {

		System.out.println("Handle message:" + MsgType);
		ServerMessageHelper smh = new ServerMessageHelper();
		switch (MsgType) {

		case 0:
			serverConObj.peerProcessObject.log.displayLogMessages(LogConstants.CHOKED, serverConObj.clientPeerID, null,
					0, 0);
			break;
		case 1:

			serverConObj.peerProcessObject.log.displayLogMessages(LogConstants.UNCHOKED, serverConObj.clientPeerID,
					null, 0, 0);
			break;

		case 2:
			smh.serverInterestedProcess(obj, serverConObj);

			break;

		case 3:

			smh.serverNotInterestedProcess(obj, serverConObj);
			break;

		case 4:
			break;

		case 5:
			break;

		case 6:
			smh.serverRequestProcess(obj, serverConObj);

			break;

		case 7:
			break;

		default:
			smh.serverOtherwise();

		}

	}
}
