package p2pFileSharing;

public class PeerProcessHelper {

	/**
	 * @param mainPeerProcess
	 * @throws NumberFormatException
	 */
	public static void schedulerCalls(peerProcess mainPeerProcess) throws NumberFormatException {
		int unchokingInterval = Integer.parseInt(peerProcess.commonProperties.get("UnchokingInterval"));
		int OptUnchokedInterval = Integer.parseInt(peerProcess.commonProperties.get("OptimisticUnchokingInterval"));
		int numberMainThreads = mainPeerProcess.startedThreads.size();
		new SchedulePreferredNeighbours(mainPeerProcess, numberMainThreads, unchokingInterval);
		// SchedulePreferredNeighbours sss = new
		// SchedulePreferredNeighbours(pTemp, k, p) ;
		// PrefNeighborScheduler schedPref = new PrefNeighborScheduler(pTemp);
	
		new ScheduleOptUnchokedNeighbour(mainPeerProcess, OptUnchokedInterval);
	}

}
