/* Created Thu Jan 28 17:15:33 EST 2016 */
//recordnodata record no events
package com.myteam.robot;

import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.components.ui.ContinuousRange;
import org.strongback.components.ui.FlightStick;
import org.strongback.drive.MecanumDrive;
import org.strongback.drive.TankDrive;
import org.strongback.hardware.Hardware;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class Robot extends IterativeRobot {

    private static final int JOYSTICK_PORT = 1; // in driver station
    private static final int LMOTOR_PORT = 1;
    private static final int RMOTOR_PORT = 0;

    private TankDrive drive;
    private ContinuousRange driveSpeed;
    private ContinuousRange turnSpeed;
    
    //trying to move this joystick init outisde of robotInit so telopPeriodic can access stick control
    
    
    @Override
    public void robotInit() {
    	Strongback.configure().recordNoEvents().recordNoData().initialize();
    	
        Motor left = Hardware.Motors.talon(LMOTOR_PORT).invert();
        Motor right = Hardware.Motors.talon(RMOTOR_PORT).invert();
        
        FlightStick joystick = Hardware.HumanInterfaceDevices.logitechAttack3D(JOYSTICK_PORT);
        
        //TODO init drive
        drive = new TankDrive(left, right);
        
        // Set up the human input controls for teleoperated mode. We want to use the Logitech Attack 3D's throttle as a
        // "sensitivity" input to scale the drive speed and throttle, so we'll map it from it's native [-1,1] to a simple scale
        // factor of [0,1] ...

        ContinuousRange sensitivity = joystick.getThrottle().map(t -> (t + 1.0) / 2.0);
        driveSpeed = joystick.getPitch().scale(sensitivity::read); // scaled
        turnSpeed = joystick.getRoll().scale(sensitivity::read).invert(); // scaled and inverted
       
    }

    @Override
    public void autonomousInit() {
        // Start Strongback functions ...
        //Strongback.start();
        //Strongback.submit(new TimedDriveCommand(drive, 0.5, 0.5, false, 5.0));
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
        
    }

    @Override
    public void disabledInit() {
        // Tell Strongback that the robot is disabled so it can flush and kill commands.
        Strongback.disable();
    }

}
