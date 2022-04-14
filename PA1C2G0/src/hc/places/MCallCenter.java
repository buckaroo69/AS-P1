package hc.places;

import hc.MFIFO;
import hc.active.TCommsHandler;
import hc.enums.ReleasedRoom;
import hc.interfaces.ICallCenterWaiter;
import hc.interfaces.IFIFO;

/**
 * Class responsible for propagating availability notifications
 * Stores a queue of tasks
 * Supports manual/auto mode
 * In manual mode requests are propagated to controller process and echoed after user approval
 */
public class MCallCenter extends Thread implements ICallCenterWaiter{

    private final TCommsHandler comms;
    private final IFIFO<ReleasedRoom> requests; //this fifo provides all the synchronicity we need
    private boolean manual;
    private ICallCenterWaiter entranceHall;
    private ICallCenterWaiter waitingHall;
    private ICallCenterWaiter medicalHall;


    public MCallCenter(boolean manual, TCommsHandler tCommsHandler,int people){

        this.manual = manual;
        comms = tCommsHandler;
        requests = new MFIFO(ReleasedRoom.class,people);
    }

    /**
     * Sets this class's operation mode, only ever called by comms so safe by default
     * @param b True for manual operation, False for automatic mode
     */
    public void setManual(boolean b) {
    manual = b;
    }

    /**
     * Registers a new movement request from a room that got was freed up
     * @param room The room type that got freed up
     */
    public void notifyAvailable(ReleasedRoom room){
        if (!manual)
            requests.put(room);
        else{
            comms.requestPermission(room.name());
        }
    }


    /**Called by communication socket after movement is approved
     * Releases ONE patient request for the given room
     *
     * @param ID: The allowed room's name
     */
    public void releaseRequest(String ID){
        switch (ID){
            case "EVH":
                requests.put(ReleasedRoom.EVH);
                break;
            case "WTR_ADULT":
                requests.put(ReleasedRoom.WTR_ADULT);
                break;
            case "WTR_CHILD":
                requests.put(ReleasedRoom.WTR_CHILD);
                break;
            case "MDW_ADULT":
                requests.put(ReleasedRoom.MDW_ADULT);
                break;
            case "MDW_CHILD":
                requests.put(ReleasedRoom.MDW_CHILD);
                break;
            case "MDR_ADULT":
                requests.put(ReleasedRoom.MDR_ADULT);
                break;
            case "MDR_CHILD":
                requests.put(ReleasedRoom.MDR_CHILD);
                break;
            default:
                throw new RuntimeException("Unknown room was released");
        }
    }


    /**
     * gets the latest request
     * lock into appropriate hall
     * notify oldest patient in hall if any
     *
     */
    public void run(){
        ReleasedRoom handling;
        while (!Thread.interrupted()){
            handling = requests.get();
            switch (handling){
                case EVH:
                    entranceHall.notifyAvailable(handling);
                    break;
                case WTR_ADULT:
                case WTR_CHILD:
                case MDW_ADULT:
                case MDW_CHILD:
                    waitingHall.notifyAvailable(handling);
                    break;
                case MDR_ADULT:
                case MDR_CHILD:
                    medicalHall.notifyAvailable(handling);
                    break;

            }

        }
    }

    /**
     * Called by instance to claim that simulation has finished running
     */
    public void notifyOver(){
        comms.notifyDone();
    }


    public void setEntranceHall(ICallCenterWaiter entranceHall) {
        this.entranceHall = entranceHall;
    }

    public void setWaitingHall(ICallCenterWaiter waitingHall) {
        this.waitingHall = waitingHall;
    }

    public void setMedicalHall(ICallCenterWaiter medicalHall) {
        this.medicalHall = medicalHall;
    }
}
