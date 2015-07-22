import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

def SerialPortTest() {
    try {
        // creating instance of the serial communication class
        final Serial serialPort = SerialFactory.createInstance()
        // creating and registering serial data listener
        serialPort.addListener(new SerialDataListener() {
                @Override
                public void dataReceived(SerialDataEvent event) {
                    throw new UnsupportedOperationException("Not supported yet.")
                }
            });
        // open the default serial port provided on the GPIO header with
        // DEFAULT_COM_PORT = /dev/ttyAMA0, speed = 38400
        serialPort.open(serialPort.DEFAULT_COM_PORT, 38400)
        // keeping the program running until user terminates it
        while(true) {
            serialPort.write("Hey there! This is a formatted string on %s", "/dev/ttyAMA0")
            // writing individual bytes to the serial transmit buffer
            serialPort.write((byte) 10)
            // writing individual chars to the serial transmit buffer
            serialPort.write('x')
            // wait 1 second
            Thread.sleep(1000)
        }
    } catch (all) {
        assert true
        assert all in Exception
    }
}