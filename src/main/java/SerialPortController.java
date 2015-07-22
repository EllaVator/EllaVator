/**
    This class uses pi4j library to create a serial port instance.
    Other code and comments for this class was taken directly from sources.
    Original code by Ben Resner, 2000, benres@media.mit.edu
    Modified for talking elevator by T.Liadal July 2007.
*/

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class SerialPortController {
    // creating instance of the serial communication class
    final Serial serialPort = SerialFactory.createInstance();
    String receivedData = "";

    public SerialPortController(){
    // creating and registering serial data listener
    serialPort.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                receivedData = event.getData();
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    //a few printing methods for debugging
    public void printMessage(int[] message) {
        for(int i = 0; i<message.length; i++ ) {
            System.out.println("message i: " + i + " = " + message[i]);
        }
    }
    public void printMessageAndHex(byte[] message) {
        for(int i = 0; i<message.length; i++ ) {
            System.out.println("message i: " + i + " = " + message[i] +
                                " HEX: " + Integer.toHexString(message[i]));
        }
    }

    // writing data to serial port
    public void writeToPort(int[] message) {
        // open rasppi default serial port provided on the GPIO header with
        // DEFAULT_COM_PORT = /dev/ttyAMA0, speed = 38400
        if (serialPort.isOpen()) {
            serialPort.close();
        } else {
            try {
                serialPort.open(serialPort.DEFAULT_COM_PORT, 38400);
            } catch (SerialPortException ex) {
                ex.printStackTrace();  // we could use log4j here
            } finally {
                serialPort.close();
            }

            try {
                for (int i=0; i < message.length; i++){
                    serialPort.write((byte)message[i]);
                }
            } catch (IllegalStateException ex) {
                ex.printStackTrace();  // we could use log4j here
            } finally {
                serialPort.close();
            }
        }
    }

    /**
     * Reads all bytes that are being sent from the elevator, chops it up into strings that
     * start and end in 0x7E (126), and looks through these for messages with message-ID C9,
     * and checks what the floor byte is. The last one gets returned (might send several as
     * the elevator is moving, we want the newest value.)
     */
    public int askFloorInformation(int[] heartbeat) {
        /* WARNING: I didn't test readPortData() and whether what it returns can
        * be processed by findValidSubstrings without errors */
        byte[] byteString = readPortData(heartbeat);
        // fills the arraylist with valid substrings
        ArrayList messages = findValidSubstrings(byteString);

        System.out.println("messages ArrayList has " + messages.size() + " elements now.");
        //returns -1 if no floor is set
        int floor = -1;
        for(int i=0; i<messages.size(); i++) {
                int temp = checkMessage((byte[])messages.get(i));
                if(temp!=-1){ floor = temp;}
        }
        return floor;
    }


    /********** private methods ahead, step lightly :) **********/

    /**
     * Sends a heartbeat to the elevator, who writes status information back.
     * We wait until we have gotten a long enough message, then return it.
     * @return
     */
    private byte[] readPortData(int[] heartbeat) {
        writeToPort(heartbeat);  // writing the heartbeat to get a reply

        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //returns data collected by Listener
        return receivedData.getBytes(Charset.forName("UTF-8"));
    }

    /**
    * Looks for the start and stop flag and copies them and the bytes between
    * to an ArrayList.
    * If no such substring exist it returns an empty ArrayList, and other parts of the
    * program have to check the length of the returned message.
    * @param byteString
    * @return
    */
    private ArrayList findValidSubstrings(byte[] byteString)
    {
        ArrayList validSubstrings = new ArrayList();

        int countingIndex = 0;
        int startFlagIndex = -1;
        int stopFlagIndex  = -1;

        boolean endOfByteString = false;

        while(!endOfByteString){
            boolean startFound = false;
            boolean stopFound = false;
            for(int i= countingIndex ; i< byteString.length ; i++) {
                if((byteString[i]==126) && !startFound)
                {
                    startFlagIndex = i;
                    startFound = true;
                }
                else if((byteString[i]==126) && startFound) {
                    if(!stopFound) {
                        stopFlagIndex = i;
                        stopFound = true;
                    }
                    if(stopFound) {
                        addByteStringToArrayList(byteString, startFlagIndex, stopFlagIndex, validSubstrings);
                        countingIndex=i+1;
                        if(countingIndex>=byteString.length) { endOfByteString = true;}
                        break;
                    }
                }
            } //end for
        } // end while
        return validSubstrings;
    }

    /**
    * Takes the start and the stop index of an interval in the bytestring, and copies the contents into
    * a new bytestring of the same length as the interval. This new bytestring is added to the
    * ArrayList.
    * @param byteString
    * @param startFlagIndex
    * @param stopFlagIndex
    * @param validSubstrings
    */
    private void addByteStringToArrayList(byte[] byteString, int startFlagIndex, int stopFlagIndex, ArrayList validSubstrings) {
        //System.out.println("adding an byte array, start = "+startFlagIndex+" stop ="+stopFlagIndex);
        byte[] message = new byte[stopFlagIndex - startFlagIndex +1];
        int messageIndex = 0 ;
        for(int i = startFlagIndex; i< stopFlagIndex; i++) {
            message[messageIndex] = byteString[i];
            messageIndex++;
        }
        //insert crc check here if ever necessary
        validSubstrings.add(message);
    }

    /**
     * We are looking for messages with message ID 0xc9 (201), the message ID is in
     * the 4th byte (indexed 3).
     * If we have a message of the right kind, we check the 11th byte to see
     * which floor we are in. It ends with 00001111 because we don't care about the first part
     * of the byte (which tells us if buttons have been pressed from the outside or
     * inside).
     */
    private int checkMessage(byte[] message){
        if(message[3]==(byte)0xc9) { // -55 dec = 0xc9 hex
            int val =(int)(message[10]&0x0f); //0x0f (15)
            //System.out.println("found floor: " + val);
            return val;
        }
        else {
            return -1;
        }
    }
}
