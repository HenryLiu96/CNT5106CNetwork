package Core;

import Util.TypeTransfer;

import java.nio.ByteBuffer;
import java.util.*;

public class P2PMessageHandler {

    public int peerID;
    public static P2PLogger logger;
    private Random r = new Random();

    //TODO move these field out of the class
    public List<Integer> preferredNeighbors = new ArrayList<>();
//
//    public static int numOfNeighbors;
//    public static int[][] downloadingNum = new int[numOfNeighbors + 1][numOfNeighbors + 1]; // row is receiver and col is sender
//    public int optUnchockedNeighbor;
    public static Map<Integer, Set<Integer>> interestedSet;
    public static Map<Integer, Set<Integer>> unchokepeers;
//    public static Map<Integer, int[]> peerBitField = new HashMap<>();
//    public static int numOfPiece;

    //constructor
    //assign the peerID
    public P2PMessageHandler(int peerID) {
        this.peerID = peerID;
    }

    //send choke message
    public void sendChoke(int receiverID){
        P2PMessage choke = new P2PMessage(P2PMessage.msgType.choke);
        byte[] actualMessage = choke.messageToBytes();
//        client.sendMessage(actualMessage, receiverID);
    }

    //receive choke message
    public void receiveChoke(P2PMessage msg, int senderID){

    }

    //send unchoke message
    public void sendUnchoke(int receiverID){
        P2PMessage unchoke = new P2PMessage(P2PMessage.msgType.unchoke);
        byte[] actualMessage = unchoke.messageToBytes();
//        client.sendMessage(actualMessage, receiverID);
    }

    //receive unchoke message
    public void receiveUnchoke(P2PMessage msg, int senderID) throws Exception {
        boolean[] curBitField = ServerThreadPool.getStatusMap().getBitField(this.peerID);
        boolean[] senderBitField = ServerThreadPool.getStatusMap().getBitField(senderID);
        Set<Integer> notHave = new HashSet<>();
        for(int i = 0; i < curBitField.length; i++){
            if(senderBitField[i] && !curBitField[i]){
                notHave.add(i);
            }
        }
        if(notHave.size() > 0){
            int target = r.nextInt(notHave.size());
            int i = 0;
            for(int index : notHave){
                if(target == i){
                    sendRequest(index, senderID);
                    break;
                }
                i++;
            }
        }
    }




    //generate interested message and ask client to send interested message
    public void sendInterested(int receiverID) {
        P2PMessage interested = new P2PMessage(P2PMessage.msgType.interested);
        byte[] actualMessage = interested.messageToBytes();
//        client.sendMessage(actualMessage, receiverID);
    }

    // receive interested message
    public void receiveInterested(P2PMessage msg, int senderID) {
        //add sendID to the interested set
        //TODO update interested list
        interestedSet.computeIfAbsent(this.peerID, x -> new HashSet<>()).add(senderID);
    }


    public void sendNotInterested(int receiverID) {
        P2PMessage notInterested = new P2PMessage(P2PMessage.msgType.notInterested);
        byte[] actualMessage = notInterested.messageToBytes();
        //client.sendMessage(actualMessage, receiverID);
    }

    public void receiveNotInterested(P2PMessage msg, int senderID) {
        //remove sender from the interested set
        //TODO update interested list
        interestedSet.get(this.peerID).remove(senderID);
    }


    // send have message
    // have message include the length of the message, type, and index of the piece
    public void sendHave(int index, int receiverID){
        P2PMessage have = new P2PMessage(P2PMessage.msgType.have, index);
        byte[] actualMessage = have.messageToBytes();
        //client.sendMessage(actualMessage, receiverID);
    }

    // receive have message
    public void receiveHave(P2PMessage msg, int senderID) throws Exception {
        //check whether current bitfield has the index
        boolean[] curBitField = ServerThreadPool.getStatusMap().getBitField(this.peerID);
//        int[] curBitField = peerBitField.get(this.peerID);
        int curIndex = msg.getIndex();
        //only send interested if current peer doesn't have the piece
        //when receive a piece of file, check the bitfield, and decide whether send not interested
        if(!curBitField[curIndex]){
            sendInterested(senderID);
        }
        ServerThreadPool.getStatusMap().updateBitField(senderID, curIndex);

        //check if has interested piece
        boolean interest = false;
        for(boolean interestPiece : curBitField){
            if(interestPiece){
                interest = true;
                break;
            }
        }
        if(!interest){
            for(int peerID : ServerThreadPool.getStatusMap().getPeerSet()){
                if(peerID == this.peerID)
                    continue;
                sendNotInterested(peerID);
            }
        }



//        int[] senderBitField = peerBitField.get(senderID);
//        senderBitField[curIndex] = 1;
    }

    // send bit field
    public void sendBitfield(int receiverID) throws Exception {
        //check if bitfield is empty, if so, do not send bitfield
        //if bitfield is not empty, send bitfield
        boolean[] curBitField = ServerThreadPool.getStatusMap().getBitField(this.peerID);
        if(!bitFieldIsEmpty(curBitField)){
            String strBitField = bitFieldToString(curBitField);
            P2PMessage bitField = new P2PMessage(P2PMessage.msgType.bitField, strBitField);
            byte[] actualMessage = bitField.messageToBytes();
            //client.sendMessage(actualMessage, receiverID);
        }
//        if(!peerBitField.containsKey(this.peerID)){
//            int[] defaultBitField = new int[numOfPiece];
//            Arrays.fill(defaultBitField, 0);
//            peerBitField.put(this.peerID, defaultBitField);
//        }else{
//            int[] curBitField = peerBitField.get(this.peerID);
//            String strBitField = Arrays.toString(curBitField).replaceAll("\\[|\\]|,|\\s", "");
//            P2PMessage bitField = new P2PMessage(P2PMessage.msgType.bitField, strBitField);
//            byte[] actualMessage = bitField.messageToBytes();
//            //client.sendMessage(actualMessage, receiverID);
//        }
    }


    //check if a bitField is empty
    public boolean bitFieldIsEmpty(boolean[] bitField){
        for (boolean b : bitField) {
            if (b) {
                return false;
            }
        }
        return true;
    }

    //convert bitField to string
    public String bitFieldToString(boolean[] bitField){
        StringBuilder sb = new StringBuilder();
        for(boolean hasPiece : bitField){
            if(hasPiece){
                sb.append("1");
            }else{
                sb.append("0");
            }
        }
        return sb.toString();
    }



    // receive bit field
    public void receiveBitField(P2PMessage msg, int senderID) throws Exception {
        //get bitField(string) and transfer to boolean array
        String strbitField = msg.getBitField();
        boolean[] receivedBitField = new boolean[strbitField.length()];
        for(int i = 0; i < strbitField.length(); i++){
            receivedBitField[i] = strbitField.charAt(i) != '0';
        }
        // check peeBitField
        // compare current bit field and received bit field
        // if current bit field has all the piece that in the received bit field, send not interested
        // else send interest message
        boolean[] curBitField = ServerThreadPool.getStatusMap().getBitField(this.peerID);
        boolean interest = false;
        for(int i = 0; i < curBitField.length; i++){
            if(receivedBitField[i] && !curBitField[i]){
                interest = true;
                break;
            }
        }
        if(interest){
            sendInterested(senderID);
        }else{
            sendNotInterested(senderID);
        }
    }


    // send request
    public void sendRequest(int index, int receiverID){
        P2PMessage request = new P2PMessage(P2PMessage.msgType.request, index);
        byte[] actualMessage = request.messageToBytes();
        //client.sendMessage(actualMessage, receiverID);
    }


    // receive request
    // After receiving the request, send the piece to the peer
    public void receiveRequest(P2PMessage msg, int senderID){
        //TODO get unchoke peer set
        if (unchokepeers.get(this.peerID).contains(senderID)){        //check unchokelist
            sendPiece(msg.getIndex(), senderID);
        }
    }

    // send piece
    // get piece from the file
    public void sendPiece(int index, int receiverID){
        FileHandler fh = new FileHandler();
        byte[] payload = fh.read(index, this.peerID);
        P2PMessage piece = new P2PMessage(P2PMessage.msgType.piece, index, payload);
        byte[] actualMessage = piece.messageToBytes();
        //client.sendMessage(actualMessage, receiverID);
    }



    // receive piece
    // check if the client has this piece of file
    // If so, download this data and save it to its file
    public void receivePiece(P2PMessage msg, int senderID) throws Exception {
        FileHandler fh = new FileHandler();
        int curIndex = msg.getIndex();
        byte[] curPayload = msg.getPayload();
        fh.write(curIndex, curPayload, this.peerID);
        //downloadingNum[this.peerID][senderID]++;

        //randomly select a not-have piece and send request
        boolean[] curBitField = ServerThreadPool.getStatusMap().getBitField(this.peerID);
        boolean[] senderBitField = ServerThreadPool.getStatusMap().getBitField(senderID);
        Set<Integer> notHave = new HashSet<>();
        for(int i = 0; i < curBitField.length; i++){
            if(senderBitField[i] && !curBitField[i]){
                notHave.add(i);
            }
        }
        notHave.remove(curIndex);
        if(notHave.size() > 0){
            int target = r.nextInt(notHave.size());
            int i = 0;
            for(int index : notHave){
                if(target == i){
                    sendRequest(index, senderID);
                    break;
                }
                i++;
            }
        }

        //TODO update requested

        //select peers that don't have this piece and send have message
        for(int peerID : ServerThreadPool.getStatusMap().getPeerSet()){
            if(peerID == senderID || peerID == this.peerID)
                continue;
            if(!ServerThreadPool.getStatusMap().getBitField(peerID)[curIndex])
                sendHave(curIndex, peerID);
        }
    }


    public static void main(String[] args){

    }

}
