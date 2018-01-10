package p2pFileSharing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourceMaintainance {
	public ResourceMaintainance() {
	}

	/*
	 * Have message functionality
	 */
	public synchronized ArrayList<Integer> prepareHaveList(HashSet<Integer> globalHashSet,
			HashSet<Integer> localHashSet) {
		ArrayList<Integer> preparedHaveList = new ArrayList<>();
		java.util.Iterator<Integer> it = globalHashSet.iterator();
		while (it.hasNext()) {
			int newOne = it.next();
			if (!(localHashSet.contains(newOne)))
				preparedHaveList.add(newOne);
		}
		return preparedHaveList;
	}

	/*
	 * Unchoke and Choke message functionality
	 */
	public synchronized HashSet<Integer> SelectPreferredNeighbors(Set<Integer> ListOfInterestedPeers,
			int noOfPrefNeigh, peerProcess peerProcessObj) {
		HashSet<Integer> preferredNeighbours = new HashSet<>();
		List<Integer> usersList = new ArrayList<Integer>(ListOfInterestedPeers);
		Collections.shuffle(usersList);
		for (int i = 0; i < noOfPrefNeigh; i++) {
			preferredNeighbours.add(usersList.get(i));
		}
		
		int[] array = preferredNeighbours.stream().mapToInt(o -> o).toArray();
		Integer[] result = Arrays.stream(array).boxed().toArray( Integer[]::new );
		peerProcessObj.log.displayLogMessages(LogConstants.CHANGE_PREFERRED_NEIGHBOURS, 0, result, 0, 0);
		return preferredNeighbours;
	}

	public synchronized int SelectOptNeighbors(Set<Integer> PreferredNeighbors, Set<Integer> ListOfInterestedPeers)
			throws Exception {
		List<Integer> interestedListForchoosing = new ArrayList<Integer>(ListOfInterestedPeers);
		List<Integer> prefNeigh = new ArrayList<Integer>(PreferredNeighbors);
		interestedListForchoosing.removeAll(prefNeigh);
		if (interestedListForchoosing != null && !interestedListForchoosing.isEmpty()) {
			Collections.shuffle(interestedListForchoosing);
			return interestedListForchoosing.get(0);
		} else {
			System.out.println("From ResourceMaintainence, getPieceIndex, emptylist");
			return 0;
		}

	}

	public synchronized ArrayList<Integer> prepareChokeList(Set<Integer> PreferredNeighbors,
			Set<Integer> listofInterestedPeers) {
		ArrayList<Integer> InterestedPeersList = new ArrayList<Integer>(listofInterestedPeers);
		ArrayList<Integer> PreferredNeighborsList = new ArrayList<Integer>(PreferredNeighbors);
		if (InterestedPeersList != null && !InterestedPeersList.isEmpty()) {
			InterestedPeersList.removeAll(PreferredNeighborsList);
		}
		return InterestedPeersList;
	}

	/*
	 * Request message functionality
	 */
	public synchronized int getPieceIndex(HashSet<Integer> neededByteIndex) {
		List<Integer> neededlist = new ArrayList<Integer>(neededByteIndex);
		if (neededlist != null && !neededlist.isEmpty()) {
			Collections.shuffle(neededlist);
			return neededlist.get(0);
		} else {
			System.out.println("From ResourceMaintainence, getPieceIndex, emptylist");
			return 0;
		}
	}

	/*
	 * Bitfield message functionality
	 */
	

}