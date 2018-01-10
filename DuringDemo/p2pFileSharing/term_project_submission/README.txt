To run from command line, open the command prompt in the folder p2pFileSharing.

1. StartRemotePeers is dependent on Jsch jar.
Run command javac -cp ".;lib\jsch-0.1.54.jar" StartRemotePeers.java to associate with jar.
Example: <downloadedpath>\p2pFileSharing> javac -cp ".;lib\jsch-0.1.54.jar" StartRemotePeers.java

2. Compile all other classes from command line each one listed separately excluding StartRemotePeers.
Example: <downloadedpath>\p2pFileSharing>javac ActualMessages.java BitFieldMessage.java CMessageHandler.java ChokeUnchoke.java ClientMessageHelper.java ConfigProperties.java FileManipulator.java HandShake.java Have.java Interested.java LogConstants.java LogMessageUtil.java MessageHandlerParent.java NotInterested.java PeerDetails.java PeerProcessHelper.java Piece.java Request.java ResourceMaintainance.java SMessageHandler.java ScheduleOptUnchokedNeighbour.java SchedulePreferredNeighbours.java SchedulerHelper.java ServerMessageHelper.java SetupClientConnection.java SetupServerConnection.java peerProcess.java

3. Run project after including common.cfg and peerInfo.cfg  with command java p2pFileSharing/peerProcess <peerID>{Ex-1002 is the peerID } for project to work

<downloadedpath>:java p2pFileSharing/peerProcess 1002


To run from eclipse , import jar into a newly created JAVA project excluding src directory using the option , use project folder as root for sources and class files.


