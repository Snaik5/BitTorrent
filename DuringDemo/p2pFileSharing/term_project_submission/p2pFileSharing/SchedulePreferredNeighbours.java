package p2pFileSharing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;

class SchedulePreferredNeighbours {

	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public SchedulePreferredNeighbours(peerProcess peerProcess, int numberOfPrefNeigh, int unchokingIntrvl) {
		final Runnable kPref = new Runnable() {
			public void run() {
				try {
					SchedulerHelper shHelp = new SchedulerHelper();
					ResourceMaintainance resmain = new ResourceMaintainance();
					FileManipulator file = new FileManipulator();
					int numNeighCurr = 0;
					System.out.println("Timer in Schedule Pref Neighbours");
					boolean neighBourSelProcess = file.fileCheck(peerProcess.serverPeerID);
					//Random neighbour selection
					if (neighBourSelProcess) {
						System.out.println("Timer in Random selection of neighbours:"+ peerProcess.serverPeerID+" Peer has entire file");					
						if (peerProcess.InterestedPeersList.isEmpty()) 
							return;
						numNeighCurr = numberOfPrefNeigh;
						synchronized (peerProcess.InterestedPeersList) {
							if (peerProcess.InterestedPeersList.size() < numNeighCurr) {
								numNeighCurr = peerProcess.InterestedPeersList.size();
							}
						}

						ArrayList<Integer> preferredPeerList = shHelp.prepPrefNeighListRandomly(peerProcess, resmain, numNeighCurr);
						int[] peerIDlist = shHelp.unchokingNewNeighbours(peerProcess, preferredPeerList);			
						Integer[] peerIdList = Arrays.stream(peerIDlist).boxed().toArray(Integer[]::new);
						//peerProcess.log.displayLogMessages(LogConstants.CHANGE_PREFERRED_NEIGHBOURS, 0, peerIdList, 0, 0);
						
						int[] array = preferredPeerList.stream().mapToInt(o -> o).toArray();
						Integer[] result = Arrays.stream(array).boxed().toArray( Integer[]::new );
						peerProcess.log.displayLogMessages(LogConstants.CHANGE_PREFERRED_NEIGHBOURS, 0, result, 0, 0);
					}

					// Choose the neighbors who provide the server the highest download rate.
					else {
						System.out.println("Timer in neighbour choice with download rate comparison");
						
					//	synchronized (peerProcess.ListofInterestedPeers) {
					//		while (peerProcess.ListofInterestedPeers.isEmpty())
						//		;
					//	}

						boolean empty = false;

						synchronized (peerProcess.PreferredNeighborsSet) {
							empty = peerProcess.PreferredNeighborsSet.isEmpty();
						}

						if (!empty) {

							synchronized (peerProcess.PreferredNeighborsSet) {
								peerProcess.PreferredNeighborsSet.clear();
							}

							numNeighCurr = numberOfPrefNeigh;

							synchronized (peerProcess.InterestedPeersList) {
								if (peerProcess.InterestedPeersList.size() < numNeighCurr) {
									numNeighCurr = peerProcess.InterestedPeersList.size();
								}
							}

							shHelp.highestDownloadNeighbourAdd(peerProcess, resmain, numNeighCurr);

							shHelp.chokePeersWithLowerDownloadRate(peerProcess, resmain);

						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		
		};
		final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(kPref, unchokingIntrvl, unchokingIntrvl, SECONDS);

	}
}