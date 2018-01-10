package p2pFileSharing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class SchedulerHelper {
	
	public ArrayList<Integer> prepPrefNeighListRandomly(peerProcess peerProcess, ResourceMaintainance resmain,
			int numNeighCurr) {
		ArrayList<Integer> preferredPeerList;
		synchronized (peerProcess.PreferredNeighborsSet) {
			peerProcess.PreferredNeighborsSet = resmain.SelectPreferredNeighbors(peerProcess.InterestedPeersList, numNeighCurr, peerProcess);
			if (peerProcess.PreferredNeighborsSet.isEmpty()) {
				System.out.println("Empty");
			}
			preferredPeerList = new ArrayList<Integer>(peerProcess.PreferredNeighborsSet);
		}
		return preferredPeerList;
	}

	public int[] unchokingNewNeighbours(peerProcess peerProcess, ArrayList<Integer> preferredPeerList) {
		int[] peerIDlist = new int[peerProcess.PreferredNeighborsSet.size()];
		ChokeUnchoke unChokemsg = new ChokeUnchoke(0, 1,peerProcess.myPeerID);
		SetupClientConnection clientConObj;
		for (int i = 0; i < preferredPeerList.size(); i++) {
			peerIDlist[i] = preferredPeerList.get(i);
			System.out.println("peer ids" + peerIDlist[i]);
			Set idSet = peerProcess.clientmap.keySet();
			System.out.println("All ids : " + idSet);
			clientConObj = peerProcess.clientmap.get(peerIDlist[i]);
			if (clientConObj != null) {
				try {
					unChokemsg.SendUnchokeMessage(clientConObj.out);
				} catch (IOException e) {
					System.out.println("Client: "+clientConObj.clientPeerID+" terminated the connection");
				}
				System.out.println("Sending unchoked message to " + peerIDlist[i]);
			} else {
				System.out.println("no client connection !!");
			}
		}
		return peerIDlist;
	}
	
	public void chokePeersWithLowerDownloadRate(peerProcess peerProcess, ResourceMaintainance resmain)
			throws IOException {
		ChokeUnchoke chokemsg = new ChokeUnchoke(0, 0,peerProcess.myPeerID);
		ArrayList<Integer> chokeList = new ArrayList<Integer>();

		synchronized (peerProcess.PreferredNeighborsSet) {
			synchronized (peerProcess.InterestedPeersList) {
				SetupClientConnection clientConObj = new SetupClientConnection();

				chokeList = resmain.prepareChokeList(peerProcess.PreferredNeighborsSet,
						peerProcess.InterestedPeersList);
				for (int i = 0; i < chokeList.size(); i++) {
					if (chokeList.get(i) != peerProcess.optSelectedPeerID) {
						synchronized (peerProcess.clientmap) {
							clientConObj = peerProcess.clientmap.get(chokeList.get(i));
						}

						if (clientConObj != null) 
							chokemsg.SendChokeMessage(clientConObj.out);
						
					}
				}
			}
		}
	}
	
	public void highestDownloadNeighbourAdd(peerProcess peerProcess, ResourceMaintainance resmain,
			int numNeighCurr) {
		ArrayList<Integer> preferredListAccToDownloadRate;
		synchronized (peerProcess.PreferredNeighborsSet) {
			peerProcess.PreferredNeighborsSet = resmain.SelectPreferredNeighbors(peerProcess.InterestedPeersList, numNeighCurr, peerProcess);
			preferredListAccToDownloadRate = new ArrayList<Integer>(peerProcess.PreferredNeighborsSet);
		}

		int[] peer_IDs = new int[peerProcess.PreferredNeighborsSet.size()];

		ChokeUnchoke unchokemsg = new ChokeUnchoke(0, 1,peerProcess.myPeerID);
		synchronized (peerProcess.clientmap) {
			for (int i = 0; i < preferredListAccToDownloadRate.size(); i++) {
				peer_IDs[i] = preferredListAccToDownloadRate.get(i);
				System.out.println("sending to = " + peer_IDs[i]);

				SetupClientConnection clientConObj = peerProcess.clientmap.get(peer_IDs[i]);
				try {
					unchokemsg.SendUnchokeMessage( clientConObj.out);
				} catch (IOException e) {
					System.out.println("Client: "+clientConObj.clientPeerID+" terminated the connection");
				}
				System.out.println("Sending unchoked message to " + peer_IDs[i]);
			}
		}
	}

}