/*
 * This class uses pi4j library to create a serial port instance.
 */

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

public class SerialPortController {
    // creating instance of the serial communication class
    final Serial serialPort = SerialFactory.createInstance();

    public SerialPortController(){
    // creating and registering serial data listener
    serialPort.addListener(new SerialDataListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }

    public void writeToPort(int[] message) {
        // open rasppi default serial port provided on the GPIO header with
        // DEFAULT_COM_PORT = /dev/ttyAMA0, speed = 38400
        if (serialPort.isOpen()) {
            serialPort.close();
        } else {
            try {
                serialPort.open(serialPort.DEFAULT_COM_PORT, 38400);
            } catch (SerialPortException ex) {
                ex.printStackTrace();  // we could use log4j
            } finally {
                serialPort.close();
            }

            try {
                for (int i=0; i < message.length; i++){
                    serialPort.write((byte)message[i]);
                }
            } catch (IllegalStateException ex) {
                ex.printStackTrace();  // we could use log4j
            } finally {
                serialPort.close();
            }
        }
    }

    /*
    This method will read the next character available from the serial port receive buffer.
    NOTE: If a serial data listener has been implemented and registered with this class,
    then this method should not be called directly.
    A background thread will be running to collect received data from the serial port receive buffer and the received data will be available on via the event.

    Returns:
    next available character in the serial data buffer
    */
    //    public byte[] readPort(){
    //        //
    //    }
}
