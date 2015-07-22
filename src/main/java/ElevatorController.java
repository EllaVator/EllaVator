/* Some code for this class was taken directly from sources.
Original file created on January 2, 2000 by Ben Resner, benres@media.mit.edu
Modified for talking elevator by T.Liadal July 2007.*/

/* This class is used to control the elevator by receiving and sending commands
via serial port. It contains bunch of methods that basically emulate button actions.
*/
public class ElevatorController implements ElevatorControllerInterface {
    // floorX -- where X is the number of the floor
    final int[] floor0 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0, 0x20, 0x40, 0x0, 0x19, 0x9,  0x7e };
    final int[] floor0_5 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0, 0x20, 0x41, 0x1, 0x48, 0x1,  0x7e };
    final int[] floor1 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0, 0x20, 0x42, 0x0, 0xa9, 0x3a, 0x7e };
    final int[] floor1_5 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0, 0x20, 0x43, 0x1, 0xf8, 0x32, 0x7e };
    final int[] floor2 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0, 0x20, 0x44, 0x0, 0x79, 0x6e, 0x7e };
    final int[] floor3 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0, 0x20, 0x45, 0x0, 0xa1, 0x77, 0x7e };

    // if you send this message to the elevator it will respond with dynamic information, including the floor number
    final int[] heartbeat = {0x7E, 0x00, 0x03, 0xe0, 0x01, 0xFF, 0xFF, 0x00, 0x20, 0xc3, 0x1e, 0x64, 0x02, 0x08, 0x3a, 0x7E };

    private SerialPortController serialPort = null;

    // class constructor
    public ElevatorController() {
        serialPort = new SerialPortController();
    }

    // we ask the elevator to send us current floor number
    public int getCurrentFloor(){
        // this is a stub, implementation goes below
        return serialPort.askFloorInformation(heartbeat);
    }

    // this method emulates button push in the elevator
    public void pushButton(int number){
        switch (number) {
            case 0:
                serialPort.writeToPort(floor0);
                break;
            case 1:
                serialPort.writeToPort(floor0_5);
                break;
            case 2:
                serialPort.writeToPort(floor1);
                break;
            case 3:
                serialPort.writeToPort(floor1_5);
                break;
            case 4:
                serialPort.writeToPort(floor2);
                break;
            case 5:
                serialPort.writeToPort(floor3);
                break;
            default:
                System.out.format("ERROR: Floor number [%d] is out of range!", number);
                break;
        }
    }
}
