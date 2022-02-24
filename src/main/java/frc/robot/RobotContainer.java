package frc.robot;

import frc.robot.commands.AutoCommandGroup;
import frc.robot.commands.FieldSpaceDrive;
import frc.robot.commands.RobotSpaceDrive;
import frc.robot.commands.RotateToAngle;
import frc.robot.commands.RotateToZero;
import frc.robot.commands.Shoot;
import frc.robot.commands.Load;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.SwerveDriveSubsystem;
import frc.robot.subsystems.ConveyorSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
//import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.LimeLightSubsystem;

public class RobotContainer {
    public SwerveDriveSubsystem swerveDriveSubsystem;
    public NavXHandler navX;
    public ShooterSubsystem shooterSubsystem;
    public IntakeSubsystem intakeSubsystem;
    public ConveyorSubsystem conveyorSubsystem;
    //public LiftSubsystem liftSubsystem;

    public FieldSpaceDrive fieldSpaceDriveCommand;
    private RobotSpaceDrive robotSpaceDriveCommand;
    private AutoCommandGroup auton; 
    public Shoot shoot;
    public Load backLoad;
    private JoystickHandler joystickHandler3;
    private JoystickHandler joystickHandler4;
    public LimeLightSubsystem limeLightSubsystem;

    public LimeLightSubsystem getLimeLightSubsystem() {
        return limeLightSubsystem;
    }

    public SwerveDriveSubsystem getSwervedriveSubsystem() {
        return swerveDriveSubsystem;
    }

    public NavXHandler getNavXHandler() {
        return navX;
    }

    public ShooterSubsystem getShooterSubsystem() {
        return shooterSubsystem;
    }

    public IntakeSubsystem getIntakeSubsystem() {
        return intakeSubsystem;
    }

    public ConveyorSubsystem getConveyorSubsystem() {
        return conveyorSubsystem;
    }

    // public LiftSubsystem getLiftSubsystem() {
    //     return liftSubsystem;
    // }

    public RobotContainer() {
        swerveDriveSubsystem = new SwerveDriveSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        joystickHandler3 = new JoystickHandler(3);
        joystickHandler4 = new JoystickHandler(4);
        limeLightSubsystem = new LimeLightSubsystem(0);
        intakeSubsystem = new IntakeSubsystem();
        conveyorSubsystem = new ConveyorSubsystem();
        //liftSubsystem = new LiftSubsystem();

        navX = new NavXHandler(); // navx input

        //Field space uses navx to get its angle
        fieldSpaceDriveCommand = new FieldSpaceDrive(swerveDriveSubsystem, joystickHandler3, navX);
        robotSpaceDriveCommand = new RobotSpaceDrive(swerveDriveSubsystem, joystickHandler3);
        backLoad = new Load(shooterSubsystem, conveyorSubsystem);
        swerveDriveSubsystem.setDefaultCommand(fieldSpaceDriveCommand);

        shoot = new Shoot(shooterSubsystem, conveyorSubsystem, joystickHandler4);
        shooterSubsystem.setDefaultCommand(shoot); //Check shoot for shoot button mapping

        limeLightSubsystem.setServoAngle(35);
        limeLightSubsystem.setPipeline(1);
        RotateToZero.setInitialAngle(navX.getAngleRad());
        navX.setInitialAngle();
        fieldSpaceDriveCommand.zero();

        configureButtonBindings();
    }

    public AutoCommandGroup getAutoCommandGroup() {
        auton = new AutoCommandGroup(this);
        return auton;
    }

    private void configureButtonBindings() {
        RotateToAngle.setInitialAngle(navX.getAngleRad());
        RotateToZero.setInitialAngle(navX.getAngleRad());
        // Hold button 8 to set the swerve just forward, this is for calibration
        // purposes
        joystickHandler3.button(8).whileHeld(() -> swerveDriveSubsystem.drive(0.1, 
        0, 0), swerveDriveSubsystem);

        // This will set the current orientation to be "forward" for field drive
        joystickHandler3.button(3).whenPressed(fieldSpaceDriveCommand::zero);

        // Holding 7 will enable robot space drive, instead of field space
        joystickHandler3.button(7).whenHeld(robotSpaceDriveCommand);

        joystickHandler4.button(6).whileHeld(backLoad);

        joystickHandler4.button(6).whenReleased(backLoad::stop);

        joystickHandler4.button(1).whileActiveContinuous(() -> intakeSubsystem.backAngle(), intakeSubsystem)
                .whenInactive(intakeSubsystem::stopAngle);
        joystickHandler4.button(2).whileActiveContinuous(() -> intakeSubsystem.intake(), intakeSubsystem)
                .whenInactive(intakeSubsystem::stopIntake);
        joystickHandler4.button(3).whileActiveContinuous(() -> intakeSubsystem.backIntake(), intakeSubsystem)
                .whenInactive(intakeSubsystem::stopIntake);
        joystickHandler4.button(4).whileActiveContinuous(() -> intakeSubsystem.angle(), intakeSubsystem)
                .whenInactive(intakeSubsystem::stopAngle);

        joystickHandler4.button(5).whenPressed(() -> swerveDriveSubsystem.autoCali(), swerveDriveSubsystem);
        //joystickHandler4.button(8).whileHeld(() -> shooterSubsystem.setShooterAngle(30), shooterSubsystem);
        joystickHandler4.button(8).whenReleased(() -> shooterSubsystem.temp(), shooterSubsystem);
        // joystickHandler3.button(2).whileActiveContinuous(() -> liftSubsystem.liftUp(), liftSubsystem)
        //         .whenInactive(liftSubsystem::liftStop);

        // joystickHandler3.button(6).whileActiveContinuous(() -> liftSubsystem.liftDown(), liftSubsystem)
        //         .whenInactive(liftSubsystem::liftStop);
    }
}
