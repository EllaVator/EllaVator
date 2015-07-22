/* Interface for EllevatorControllers */

public interface ElevatorControllerInterface {
    // returns current floor number
    public int getCurrentFloor();

    // writes hex encoded floor number message to serial port
    public void pushButton(int floor);

}
