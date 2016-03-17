/*
 * Team 5973 - Moonshots - FRC Stronghold 2016
 * BASED ON STRONGBACK
 * Copyright 2015, Strongback and individual contributors by the @authors tag.
 * See the COPYRIGHT.txt in the distribution for a full listing of individual
 * contributors.
 *
 * Licensed under the MIT License; you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://opensource.org/licenses/MIT
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myteam.robot;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

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


public class Robot extends IterativeRobot {
	
	//motor declarations
    private static final int JOYSTICK_PORT = 0; // in driver station
    private static final int LMOTOR_PORT = 5;
    private static final int RMOTOR_PORT = 2; 
    private static final int RARM_PORT = 3; 
    private static final int LARM_PORT = 4;
    //limits are coefficients to the joystick sensitivity variable
    private static final double ARM_UP_LIMIT = 1;
    private static final double ARM_DOWN_LIMIT = 1;
    private static final boolean SLOW_ON_AUTONOMOUS = false;
    private TankDrive drive;
    private ContinuousRange driveSpeed;
    private ContinuousRange turnSpeed;

    //We moved this up here so we can output this variable in the teleop
    protected ContinuousRange sensitivity;
    //Used to limit and format the number of console outputs
    private int filter = 0;
	private String pattern = "###.###";
	private DecimalFormat myFormat = new DecimalFormat(pattern);
	private double sen;
    

    
    
    @Override
    public void robotInit() {
    	//Execution period extended so it doesn't time out per cycle
    	//Records no data and no events, a feature of Strongback we will look at next year
    	Strongback.configure().recordNoData().recordNoCommands().recordNoEvents()
    		.useExecutionPeriod(200, TimeUnit.MILLISECONDS).initialize();
        
    	//create motors and link them to a drive
    	Motor left = Hardware.Motors.victorSP(LMOTOR_PORT).invert();
    	//DoubleToDoubleFunction SPEED_LIMITER = Values.limiter(-0.1, 0.1);
    	Motor right = Hardware.Motors.victorSP(RMOTOR_PORT);
        drive = new TankDrive(left, right); 
        
        //create the arm motors and combine them together
        Motor armLeft = Hardware.Motors.victorSP(LARM_PORT);
        Motor armRight = Hardware.Motors.victorSP(RARM_PORT);
        //Arms combined so they always move in sync
        Motor arm = Motor.compose(armLeft, armRight);
        
        
        //Camera must be in the outside USB port. Check Rio dashboard for the name of the camera
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
        
        
        //Triggers get only .7 of our sensitivity
        reactor.onTriggered(joystick.getTrigger(), () -> arm.setSpeed(-sensitivity.read()*ARM_DOWN_LIMIT));
        reactor.onUntriggered(joystick.getTrigger(), () -> arm.stop());
        //limit switches here are very important and needed
        //We'd add physical triggers that would stop the arm from over extending
        //the motor's encoders COULD be used but are tricky as they reset every time
        reactor.onTriggered(joystick.getThumb(), () -> arm.setSpeed(sensitivity.read()*ARM_UP_LIMIT));
        reactor.onUntriggered(joystick.getThumb(), () -> arm.stop());     
       
    }

    @Override
    public void autonomousInit() {
        //Start Strongback functions ...
        Strongback.start();
        
        if(SLOW_ON_AUTONOMOUS) Strongback.submit(new TimedDriveCommand(drive, -0.35, 0.0, false, 5.0));
        else{Strongback.submit(new TimedDriveCommand(drive, -0.5, 0.0, false, 4.5));}

        //Moves forward for 5 seconds
        
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
        
        //Prints the sensitivity value every 40 cycles
        if(filter % 40 == 0){
        	sen = sensitivity.read();
        	System.out.println("Sensitivity: " + myFormat.format(sen) 
        			+ " // arm UP: " + myFormat.format(sen*ARM_UP_LIMIT)
        			+ " // arm DOWN: " + myFormat.format(sen*ARM_DOWN_LIMIT));
        }
        filter++;
    }

    @Override
    public void disabledInit() {
        // Tell Strongback that the robot is disabled so it can flush and kill commands.
    	//cam.closeCamera();
    	Strongback.disable();
        
    }

}
