/* Created Thu Jan 28 17:15:33 EST 2016 */

package com.myteam.robot;

import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.Motor;
import org.strongback.components.ui.ContinuousRange;
import org.strongback.components.ui.FlightStick;
import org.strongback.drive.TankDrive;
import org.strongback.hardware.Hardware;
import org.strongback.util.Values;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.vision.USBCamera;


public class Robot extends IterativeRobot {
	
    private static final int JOYSTICK_PORT = 0; // in driver station
    private static final int LMOTOR_PORT = 2;
    private static final int RMOTOR_PORT = 1;
    private static final int RARM_PORT = 3; 
    private static final int LARM_PORT = 4;
    private TankDrive drive;
    private ContinuousRange driveSpeed;
    private ContinuousRange turnSpeed;
    
    //Used to limit the number of console outputs
    public int filter = 0;
    
    
    
    //We moved this up here so we can 
    protected ContinuousRange sensitivity;
    
    //trying to move this joystick init outisde of robotInit so telopPeriodic can access stick control
    
    
    @Override
    public void robotInit() {
    	Strongback.configure().recordNoEvents().recordNoData().initialize();
    	
        //Motor left = Hardware.Motors.victor(LMOTOR_PORT).invert();
        Motor left = Hardware.Motors.victorSP(LMOTOR_PORT).invert();
        Motor right = Hardware.Motors.victorSP(RMOTOR_PORT);
        drive = new TankDrive(left, right); 
        
        Motor armLeft = Hardware.Motors.victorSP(LARM_PORT);
        Motor armRight = Hardware.Motors.victorSP(RARM_PORT);
        Motor arm = Motor.compose(armLeft, armRight);
        
        
        //CAMERA NIGHTMARE
        //USBCamera cam0 = new USBCamera();
        //cam0.startCapture();
        CameraServer camera = CameraServer.getInstance();
        camera.setQuality(25);
        camera.startAutomaticCapture("cam1");

        
        // Set up the human input controls for teleoperated mode. We want to use the Logitech Attack 3D's throttle as a
        // "sensitivity" input to scale the drive speed and throttle, so we'll map it from it's native [-1,1] to a simple scale
        // factor of [0,1] ...
        FlightStick joystick = Hardware.HumanInterfaceDevices.logitechAttack3D(JOYSTICK_PORT);
        SwitchReactor reactor = Strongback.switchReactor();
        // ContinuousRange sensitivity = joystick.getThrottle().map(t -> ((t + 1.0) / 2.0));
        sensitivity = joystick.getThrottle().map(Values.mapRange(-1.0,1.0).toRange(0.0, 1.0));
        driveSpeed = joystick.getPitch().scale(sensitivity::read); // scaled
        turnSpeed = joystick.getRoll().scale(sensitivity::read); // scaled and inverted
        
        
        reactor.onTriggered(joystick.getTrigger(), () -> arm.setSpeed(-sensitivity.read()));
        reactor.onUntriggered(joystick.getTrigger(), () -> arm.stop());
        //limit switches here are very important and needed
        //We'd add physical triggers that would stop the arm from over extending
        //the motor's encoders COULD be used but are tricky as they reset every time
        reactor.onTriggered(joystick.getThumb(), () -> arm.setSpeed(sensitivity.read()));
        reactor.onUntriggered(joystick.getThumb(), () -> arm.stop());     
       
    }

    @Override
    public void autonomousInit() {
        //Start Strongback functions ...
        Strongback.start();
        
        //this timedDrive class is in the package but it gets lost. Move the package
        //information around on the class and it will load right away
        Strongback.submit(new TimedDriveCommand(drive, -0.5, 0.0, false, 5.0));
    }
    
    @Override
    public void teleopInit() {
        // Kill anything running if it is ...
        Strongback.disable();
        // Start Strongback functions if not already running...
        Strongback.start();
        
    }

    @Override
    public void teleopPeriodic() {
        drive.arcade(driveSpeed.read(), turnSpeed.read()); 
        
        //Checking on the drive speed value every 10 cycles
        if(filter % 20 == 0)
        	System.out.println("Sensitivity: " + sensitivity.read());
        filter++;
    }

    @Override
    public void disabledInit() {
        // Tell Strongback that the robot is disabled so it can flush and kill commands.
    	//cam.closeCamera();
    	Strongback.disable();
        
    }

}
