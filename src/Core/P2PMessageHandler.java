package Core;

import java.nio.ByteBuffer;
import java.util.*;

public class P2PMessageHandler {


    public int peerID;
    public static P2PLogger logger;
    public List<Integer> neighbors = new ArrayList<>();

    //TODO move these field out of the class
    public List<Integer> preferredNeighbors = new ArrayList<>();
    public static int[][] downloadingNum; // row is receiver and col is sender
    public int optUnchockedNeighbor;
    public static Map<Integer, Set<Integer>> interestedSet;
    public static Map<Integer, Set<Integer>> unchokepeers;
    public static Map<Integer, int[]> peerBitField = new HashMap<>();
    public static int numOfPiece;

    //constructor
    //assign the peerID and add neighbors in the list
    public P2PMessageHandler(int numOfNeighbors, int peerID) {
        this.peerID = peerID;
        for(int i = 0; i < numOfNeighbors; i++) {
            if(i != peerID)
                neighbors.add(i);
        }
        downloadingNum = new int[numOfNeighbors + 1][numOfNeighbors + 1];
        Arrays.fill(downloadingNum, 0);
    }

    //send choke message
    public static void sendChoke(Client client, int receiverID){

    }

    //receive choke message
    public static void receiveChoke(P2PMessage msg, Client client, int senderID){

    }

    //send unchoke message
    public static void sendUnchoke(Client client, int receiverID){


        //send request
    }

    //receive unchoke message
    public static void receiveUnchoke(P2PMessage msg, Client client, int senderID){

    }




    //generate interested message and ask client to send interested message
    public static void sendInterested(Client client, int receiverID) {
        P2PMessage interested = new P2PMessage(P2PMessage.msgType.interested);
        byte[] actualMessage = interested.messageToBytes();
        client.sendMessage(actualMessage, receiverID);
    }

    // receive interested message
    public static void receiveInterested(P2PMessage msg, Client client, int senderID) {
        //add sendID to the interested set
        interestedSet.computeIfAbsent(client.peerID, x -> new HashSet<>()).add(senderID);
    }


    public static void sendNotInterested(Client client, int receiverID) {
        P2PMessage notInterested = new P2PMessage(P2PMessage.msgType.notInterested);
        byte[] actualMessage = notInterested.messageToBytes();
        client.sendMessage(actualMessage, receiverID);
    }

    public static void receiveNotInterested(P2PMessage msg, Client client, int senderID) {
        //remove sender from the interested set
        interestedSet.get(client.peerID).remove(senderID);
    }


    // send have message
    // have message include the length of the message, type, and index of the piece
    public static void sendHave(Client client, int index, int receiverID){
        P2PMessage have = new P2PMessage(P2PMessage.msgType.have, index);
        byte[] actualMessage = have.messageToBytes();
        client.sendMessage(actualMessage, receiverID);
    }

    // receive have message
    public static void receiveHave(P2PMessage msg, Client client, int senderID) {
        //check whether current bitfield has the index
        int[] curBitField = peerBitField.get(client.peerID);
        int curIndex = msg.getIndex();

        if(curBitField[curIndex] == 1){
            sendNotInterested(client, senderID);
        }else{
            sendInterested(client, senderID);
        }
        int[] senderBitField = peerBitField.get(senderID);
        senderBitField[curIndex] = 1;
    }

    // send bit field
    public static void sendBitfield(Client client, int receiverID){
        //check if bitfield is empty, if so, do not send bitfield
        //if bitfield is not empty, send bitfield
        if(!peerBitField.containsKey(client.peerID)){
            int[] defaultBitField = new int[numOfPiece];
            Arrays.fill(defaultBitField, 0);
            peerBitField.put(client.peerID, defaultBitField);
        }else{
            int[] curBitField = peerBitField.get(client.peerID);
            String strBitField = Arrays.toString(curBitField).replaceAll("\\[|\\]|,|\\s", "");
            P2PMessage bitField = new P2PMessage(P2PMessage.msgType.bitField, strBitField);
            byte[] actualMessage = bitField.messageToBytes();
            client.sendMessage(actualMessage, receiverID);
        }
    }


    // receive bit field
    public static void receiveBitField(Client client, P2PMessage msg, int senderID){
        //get bitField(string) and transfer to int array
        String bitField = msg.getBitField();
        int[] receivedBitField = new int[bitField.length()];
        for(int i = 0; i < bitField.length(); i++){
            receivedBitField[i] = bitField.charAt(i) - '0';
        }

        int curPeerID = client.peerID;
        // check peeBitField
        // compare current bit field and received bit field
        // if current bit field has all the piece that in the received bit field, send not interested
        // else send interest message
        if(peerBitField.containsKey(curPeerID)){
            int[] curBitField = peerBitField.get(curPeerID);
            boolean interested = false;
            for(int i = 0; i < numOfPiece; i++){
                if(curBitField[i] == 0 && receivedBitField[i] == 1){
                    interested = true;
                    break;
                }
            }
            if(interested){
                sendInterested(client, senderID);
            }else{
                sendNotInterested(client, senderID);
            }
        }else{
            int[] defaultBitField = new int[msg.getNumOfPiece()];
            Arrays.fill(defaultBitField, 0);
            peerBitField.put(curPeerID, defaultBitField);
        }
    }


    // send request
    public static void sendRequest(Client client, int index, int receiverID){
        P2PMessage request = new P2PMessage(P2PMessage.msgType.request, index);
        byte[] actualMessage = request.messageToBytes();
        client.sendMessage(actualMessage, receiverID);
    }


    // receive request
    // After receiving the request, send the piece to the peer
    public static void receiveRequest(P2PMessage msg, Client client, int senderID){
        if (unchokepeers.get(client.peerID).contains(senderID)){        //check unchokelist
            sendPiece(client, msg.getIndex(), senderID);
        }
    }

    // send piece
    // get piece from the file
    public static void sendPiece(Client client, int index, int receiverID){
        FileHandler fh = new FileHandler();
        byte[] payload = fh.read(index, client.peerID);
        P2PMessage piece = new P2PMessage(P2PMessage.msgType.piece, index, payload);
        byte[] actualMessage = piece.messageToBytes();
        client.sendMessage(actualMessage, receiverID);
    }



    // receive piece
    // check if the client has this piece of file
    // If so, download this data and save it to its file
    public static void receivePiece(Client client, P2PMessage msg, int senderID){
        FileHandler fh = new FileHandler();
        int curIndex = msg.getIndex();
        byte[] curPayload = msg.getPayload();
        fh.write(curIndex, curPayload, client.peerID);
        //TODO download file and save it


        //update bitfield
        int[] curBitField = peerBitField.get(client.peerID);
        if(curBitField[curIndex] == 0){
            curBitField[curIndex] = 1;
            downloadingNum[client.peerID][senderID]++;
        }


        //randomly select peers to send have message
        //nextint generate a random integer from 0 to n - 1. so receiverID add one
        Random r = new Random();
        int receiverID = r.nextInt(peerBitField.size()) + 1;
        while(receiverID == senderID || receiverID == client.peerID){
            receiverID = r.nextInt(peerBitField.size()) + 1;
        }
        sendHave(client, curIndex, receiverID);


    }


    // Convert Integer to Byte array
    public static byte[] convertIntToByte(int num){
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    // Convert Byte array to Integer
    public static int convertByteToInt(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }


    // Convert

    public static void main(String[] args){
//        byte[] test = convertIntToByte(1231);
//        for(int i = 0; i < test.length; i++){
//            System.out.println(test[i]);
//        }
        Map<Integer, Set<Integer>> interest = new HashMap<>();
        interest.computeIfAbsent(1,  x -> new HashSet<>()).add(2);
        System.out.println(interest);
    }

}
