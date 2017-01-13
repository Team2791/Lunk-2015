package config;
import edu.wpi.first.wpilibj.Timer;

public class DrivePID {
  private double P, I, D;
  private double setpoint = 0.0;
  private double previousError = 0.0;
  private double currentError = 0.0;
  private double maxOutput = 1.0;
  private double minOutput = -1.0;
  private double integrator = 0.0;
  private double previousTime = 0.0;
  private double currentTime = 0.0;
  private double output = 0.0;
  private double deadZone = 0.0;
  private double[] dPresets;
  private int[] iPresets;
  private boolean PID_IN_USE = false;

  public DrivePID(double p, double i, double d) {
    P = p;
    I = i;
    D = d;
  }

  public void update_values(double p, double i, double d) {
    P = p;
    I = i;
    D = d;
  }

  public void createPresets(double presets[]) { dPresets = presets; }

  public void createPresets(int presets[]) { iPresets = presets; }

  public void reset() {
    previousError = 0.0;
    integrator = 0.0;
    previousTime = 0.0;
    output = 0.0;
  }

  public void disable() {
    PID_IN_USE = false;
    reset();
  }

  // ---------------------------------------------------------------------------------

  public void goToPreset(int index) {
    PID_IN_USE = true;
    if (dPresets != null) {
      setSetpoint(dPresets[index]);
    } else if (iPresets != null) {
      setSetpoint(iPresets[index]);
    }
  }

  public double getIPart() {
    if (previousTime == 0.0 || I == 0.0) {
      return 0.0;
    }

    integrator += ((currentError + previousError) / 2.0) * (currentTime - previousTime);

    if (integrator * I > maxOutput) {
      integrator = maxOutput / I;
    }
    if (integrator * I < minOutput) {
      integrator = minOutput / I;
    }

    return integrator * I;
  }

  public double getDPart() {
    if (previousTime == 0.0 || D == 0.0) {
      return 0.0;
    } else {
      return D * ((currentError - previousError) / (currentTime - previousTime));
    }
  }

  public double updateOutput(double currentVal) {
    double normalOutput = updateAndGetOutput(currentVal);

    if (currentError < deadZone && currentError > -deadZone) {
      return 0.0;
    } else {
      return normalOutput;
    }
  }

  public double updateAndGetOutput(double currentVal) {
    currentTime = Timer.getFPGATimestamp();
    currentError = currentVal - setpoint;
    double newOutput = getPPart() + getIPart() + getDPart();
    previousTime = Timer.getFPGATimestamp();
    previousError = currentError;

    if (newOutput > maxOutput) {
      newOutput = maxOutput;
    } else if (newOutput < minOutput) {
      newOutput = minOutput;
    }

    return newOutput;
  }

  public double getError() { return currentError; }

  public double getOutput() { return output; }

  public double getSetpoint() { return setpoint; }

  public double getPPart() { return currentError * P; }

  public boolean checkPIDUse() { return PID_IN_USE; }

  public void setSetpoint(double newSetpoint) { setpoint = newSetpoint; }

  public void setMinOutput(double min) { minOutput = min; }

  public void setMaxOutput(double max) { maxOutput = max; }

  public void setDeadzone(double zone) { deadZone = zone; }
}
