package p2pFileSharing;

import java.io.IOException;
import java.util.HashSet;

public class CMessageHandler extends MessageHandlerParent {

	public void HandleMessages(int MsgType, Object obj, SetupClientConnection clientConObj,
			HashSet<Integer> localReceivedByteIndex) throws IOException {
		ClientMessageHelper cmh = new ClientMessageHelper();
		System.out.println("Handle message: " + MsgType);

		switch (MsgType) {

		case 0:
			// choking
			clientConObj.peerProcessObj.log.displayLogMessages(LogConstants.CHOKED,
					((ChokeUnchoke)obj).getSenderPID(), null, 0, 0);
			break;

		case 1:
			// unchoking
			clientConObj.peerProcessObj.log.displayLogMessages(LogConstants.UNCHOKED,
					((ChokeUnchoke)obj).getSenderPID(), null, 0, 0);
			cmh.clientUnchokeProcess(clientConObj);

			break;

		case 2:
			// interested
			cmh.clientInterestedProcess(obj, clientConObj);

			break;

		case 3:
			// not interested
			cmh.clientNotInterestedProcess(obj, clientConObj);

			break;

		case 4:
			// have
			cmh.clientHaveProcess(obj, clientConObj);

			break;

		case 5:
			// bitfield
			break;

		case 6:
			// request
			cmh.clientRequestProcess(obj, clientConObj);

			break;

		case 7:
			// piece
			cmh.clientPieceProcess(obj, clientConObj);
			break;

		default:
			cmh.clientOtherwise();

		}

	}
}
