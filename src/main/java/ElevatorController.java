import gnu.io.SerialPort;

/* This class is used to control the elevator by receiving and sending commands
via serial port. It contains bunch of methods that basically emulate button actions.
Floor codes are taken from sources. */
public class ElevatorController {
    private SerialPortController elevator = null;

    // class constructor
    public ElevatorController() {
        elevator = new SerialPortController(SerialControllerInterface.PORT_USB0,
                                              SerialControllerInterface.BAUD_38400,
                                              SerialPort.DATABITS_8,
                                              SerialPort.STOPBITS_1,
                                              SerialPort.PARITY_NONE);
    }

    // we ask the elevator to send us current floor number
    public int getCurrentFloor(){
        // this is a stub, implementation goes below
        return elevator.getCurrentFloor();
    }

    // this method emulates button push in the elevator
    public void pushButton(int number){
        switch (number) {
            case 0:
                elevator.move(SerialControllerInterface.floor0);
                break;
            case 1:
                elevator.move(SerialControllerInterface.floor0_5);
                break;
            case 2:
                elevator.move(SerialControllerInterface.floor1);
                break;
            case 3:
                elevator.move(SerialControllerInterface.floor1_5);
                break;
            case 4:
                elevator.move(SerialControllerInterface.floor2);
                break;
            case 5:
                elevator.move(SerialControllerInterface.floor3);
                break;
            default:
                System.out.format("ERROR: Floor number [%d] is out of range!", number);
                break;
        }
    }
}
