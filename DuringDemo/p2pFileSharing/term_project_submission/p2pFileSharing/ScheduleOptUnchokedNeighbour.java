package p2pFileSharing;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;

public class ScheduleOptUnchokedNeighbour {

	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public ScheduleOptUnchokedNeighbour(peerProcess peerProcess, int optUnchokIntervl) {
		final Runnable optUnChoke = new Runnable() {
			public void run() {

				try {
					ResourceMaintainance resmain = new ResourceMaintainance();

					System.out.println("Timer inside optimistically unchoking");
					synchronized (peerProcess.InterestedPeersList) {
						if (peerProcess.InterestedPeersList.isEmpty()) {
							return;
						}
					}

					// selecting the optimistically unchoked peer's id
					int chosenPeerId = 0;
					synchronized (peerProcess.InterestedPeersList) {
						synchronized (peerProcess.PreferredNeighborsSet) {
							chosenPeerId = resmain.SelectOptNeighbors(peerProcess.PreferredNeighborsSet,
									peerProcess.InterestedPeersList);									
						}
					}

					optimisticallyUnchokedNeighbour(peerProcess, chosenPeerId);
				}

				catch (Exception ex) {
					System.out.println(ex);
				}

			}

			public void optimisticallyUnchokedNeighbour(peerProcess peerProcess, int chosenPeerId) throws IOException {
				System.out.println("Optimistically unchoked neighbor: " + chosenPeerId);
				peerProcess.optSelectedPeerID = chosenPeerId;
				SetupClientConnection clientConObj = peerProcess.clientmap.get(chosenPeerId);
				
				
				if (clientConObj != null && chosenPeerId != 0) {
					ChokeUnchoke unchokemsg = new ChokeUnchoke(0, 1,clientConObj.peerID);
					unchokemsg.SendUnchokeMessage(clientConObj.out);
					peerProcess.log.displayLogMessages(LogConstants.CHANGE_OPT_UNCHOKED, chosenPeerId, null, 0, 0);
				}
			}
		};
		final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(optUnChoke, optUnchokIntervl,
				optUnchokIntervl, SECONDS);

	}
}