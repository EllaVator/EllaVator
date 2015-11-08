/**
    This class uses RXTX library to create a serial port instance.
    Other code and comments for this class was taken directly from sources.
    Original code by Ben Resner, 2000, benres@media.mit.edu
    Modified for talking elevator by T.Liadal July 2007.
*/

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class SerialPortController implements SerialControllerInterface {
    private InputStream istream;
    private OutputStream ostream;
    private byte[] receivedBytes;

    //constructor
    public SerialPortController (String portName, int baud, int bits, int stopbits, int parity)
    {
        CommPortIdentifier portId;
        CommPort comm;
        SerialPort serialPort;

        try {
            portId = CommPortIdentifier.getPortIdentifier(portName);
        } catch (NoSuchPortException ex) {
            System.out.println("NoSuchPortExcetion: " + ex.toString());
            throw new RuntimeException(ex);
        }
        try {
            comm = portId.open(this.getClass().getName(), 2000);
        } catch (PortInUseException ex) {
            System.out.println("PortInUseException: " + ex.toString());
            throw new RuntimeException(ex);
        }

        if (comm instanceof SerialPort) {
            serialPort = (SerialPort) comm;
            try {
                serialPort.setSerialPortParams(baud, bits, stopbits, parity);
                istream = serialPort.getInputStream();
                ostream = serialPort.getOutputStream();
            } catch (UnsupportedCommOperationException ex) {
                System.out.println("UnsupportedCommOperationException: " + ex.toString());
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                System.out.println("IOException: " + ex.toString());
                throw new RuntimeException(ex);
            }
        } else {
            System.out.println("ERROR! Serial port is not available.");
            System.exit(1);
        }
    }


    // writing data to serial port
    public void move(int[] message) {
        try {
            for (int i=0; i<message.length; i++) {
                ostream.write((byte) message[i]);
            }
        } catch (IOException ex) {
            System.out.println("IOException when writing to serial port: " + ex.toString());
            closeIOStreams();
            throw new RuntimeException(ex);
        }
    }

    /**
     * Reads received bytes from serial port buffer.
     * We wait until we have gotten a long enough message, then return it.
     * @return
     */
    public byte[] getReceivedBytes() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            System.out.println("EXCEPTION! InterruptedException: " + ex.getMessage());
            closeIOStreams();
            throw new RuntimeException(ex);
        }

        byte[] buffer = new byte[1024];
        int len = -1;
        String str = "";
        try {
            while ((len=this.istream.read(buffer)) > -1){
                str = new String(buffer, 0, len);
            }
        } catch (IllegalStateException ex) {
            System.out.println("IllegalStateException: " + ex.toString());
            closeIOStreams();
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.toString());
            closeIOStreams();
            throw new RuntimeException(ex);
        }
        if (str.isEmpty()) {
            System.out.println("WARNING! Serial buffer received nothing.");
            return receivedBytes;
        } else {
            return str.getBytes();
        }
    }

    /**
     * Reads all bytes that are being sent from the elevator, chops it up into strings that
     * start and end in 0x7E (126), and looks through these for messages with message-ID C9,
     * and checks what the floor byte is. The last one gets returned (might send several as
     * the elevator is moving, we want the newest value.)
     */
    public int getCurrentFloor() {
        /* WARNING: I didn't test readPortData() and whether what it returns can
        * be processed by findValidSubstrings without errors */
        move(SerialControllerInterface.heartbeat);
        byte[] bytes = getReceivedBytes();
        // fills the arraylist with valid substrings
        ArrayList messages = findValidSubstrings(bytes);
        System.out.println("messages ArrayList has " + messages.size() + " elements now.");
        //returns -1 if no floor is set
        int floor = -1;
        for(int i=0; i<messages.size(); i++) {
            int temp = checkMessage((byte[])messages.get(i));
            if(temp!=-1){ floor = temp;}
        }
        return floor;
    }


    /////////////////////////// private methods ///////////////////////////
    private void closeIOStreams() {
        try {
            istream.close();
            ostream.close();
        } catch (IOException ex) {
            System.out.println("ERROR! Failed to close IO streams: " + ex.toString());
            throw new RuntimeException(ex);
        }
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
    * Takes the start and the stop index of an interval in the byte string, and copies the contents into
    * a new byte string of the same length as the interval. This new byte string is added to the
    * ArrayList.
    * @param byteString
    * @param startFlagIndex
    * @param stopFlagIndex
    * @param validSubstrings
    */
    private void addByteStringToArrayList(byte[] byteString, int startFlagIndex,
            int stopFlagIndex, ArrayList validSubstrings) {
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