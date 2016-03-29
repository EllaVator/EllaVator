/* Serial port controller interface */
interface SerialControllerInterface {
    // Some constants
    public final String PORT_ttyS0 = "/dev/ttyS0";
    public final String PORT_COM1 = "COM1";  // Windows
    public final String PORT_ttyS1 = "/dev/ttyS1";
    public final String PORT_ttyS2 = "/dev/ttyS2";
    public final String PORT_ttyAMA0 = "/dev/ttyAMA0";  // default for Rasp Pi
    public final String PORT_USB0 = "/dev/ttyUSB0";  // default for Rasp Pi

    // Baudrates
    public final int BAUD_115200 = 115200;
    public final int BAUD_57600 = 57600;
    public final int BAUD_38400 = 38400;  // use this rate
    public final int BAUD_19200 = 19200;
    public final int BAUD_9600 = 9600;
    public final int BAUD_4800 = 4800;
    public final int BAUD_2400 = 2400;

    // floor codes
    public final int[] floor0 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0,
                                   0x20, 0x40, 0x0, 0x19, 0x9,  0x7e };
    public final int[] floor0_5 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0,
                                     0x20, 0x41, 0x1, 0x48, 0x1,  0x7e };
    public final int[] floor1 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0,
                                   0x20, 0x42, 0x0, 0xa9, 0x3a, 0x7e };
    public final int[] floor1_5 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0,
                                     0x20, 0x43, 0x1, 0xf8, 0x32, 0x7e };
    public final int[] floor2 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0,
                                   0x20, 0x44, 0x0, 0x79, 0x6e, 0x7e };
    public final int[] floor3 = { 0x7e, 0x0, 0x3, 0x42, 0x1, 0xff, 0xff, 0x0,
                                   0x20, 0x45, 0x0, 0xa1, 0x77, 0x7e };

    /** if you send this message to the elevator it will respond with dynamic information,
    including the floor number */
    public final int[] heartbeat = {0x7E, 0x00, 0x03, 0xe0, 0x01, 0xFF, 0xFF, 0x00,
                             0x20, 0xc3, 0x1e, 0x64, 0x02, 0x08, 0x3a, 0x7E };

    public void move(int[] message);

    public byte[] getReceivedBytes();

    public int getCurrentFloor();
}
