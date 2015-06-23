// Trying out pi4j
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;
import java.util.Random;

public class SerialController {
    public static void main(String args[]) throws InterruptedException {
        System.out.println(">>> pi4j example...");
        System.out.println(">>> connection settings: 38400, N, 8, 1");
        System.out.println(">>> receiving data on serial port...");
        Random rnd = new Random();

        // creating instance of the serial communication class
        final Serial serialPort = SerialFactory.createInstance();

        // creating and registering serial data listener
        serialPort.addListener(new SerialDataListener() {
                @Override
                public void dataReceived(SerialDataEvent event) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            });

        try {
            // open the default serial port provided on the GPIO header with
            // DEFAULT_COM_PORT = /dev/ttyAMA0, speed = 38400
            serialPort.open(serialPort.DEFAULT_COM_PORT, 38400);

            // keeping the program running until user terminates it
            while(true) {

                try {
                // writing a formatted string to the serial transmit buffer
                serialPort.write("Hey there! This is %sth formatted string", String.valueOf(rnd.nextInt(100)));
                serialPort.write("Hey there! This is %sth formatted string", String.valueOf(rnd.nextInt(100)));
                serialPort.write("Hey there! This is %sth formatted string", String.valueOf(rnd.nextInt(100)));

                // writing individual bytes to the serial transmit buffer
                serialPort.write((byte) 10);
                serialPort.write((byte) 11);

                // writing individual chars to the serial transmit buffer
                serialPort.write('x');
                serialPort.write('y');
                }
                catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
                // wait 1 second
                Thread.sleep(1000);
            }

        } catch (SerialPortException ex) {
            System.out.println(">>> SERIAL PORT ERROR!");
            ex.printStackTrace();
            return;
        }
    }
}
