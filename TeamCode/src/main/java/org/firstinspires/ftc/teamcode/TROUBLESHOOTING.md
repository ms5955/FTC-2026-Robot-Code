# FTC Robot Troubleshooting Checklist

> **Purpose:** Before changing any code or tuning values, follow this checklist to identify the root cause of the problem.

---

# 🚨 General Rule

✅ Never change PID values randomly.

✅ Never change servo positions without testing.

✅ Verify hardware before modifying software.

✅ Make only **one change at a time**.

---

# 🎯 Limelight Problems

## ❌ No AprilTag Detected

### Hardware

* [ ] Limelight power LED is ON
* [ ] Ethernet cable is connected
* [ ] Camera lens is clean
* [ ] AprilTag is visible
* [ ] Camera is not blocked

### Robot Position

* [ ] Robot is facing the AprilTag
* [ ] Distance is within detection range
* [ ] Lighting is sufficient

### Software

* [ ] Correct pipeline selected

  * Blue → Pipeline 0
  * Red → Pipeline 1
* [ ] Limelight started using

```java
limelight.start();
```

* [ ] AprilTag ID is correct
* [ ] Telemetry shows `Tag Visible = true`

---

## ❌ Turret Not Following AprilTag

Check

* [ ] Limelight detects tag
* [ ] TX value changes
* [ ] Turret motor encoder resets correctly
* [ ] Turret power is changing
* [ ] PID constants are correct

Files to Check

* TurretSubsystem.java
* BlueTeleOp.java
* RedTeleOp.java

---

## ❌ Turret Moving in Wrong Direction

Check

* [ ] Motor direction
* [ ] TX sign (+ / -)
* [ ] Limelight offset
* [ ] Encoder direction

---

## ❌ Turret Oscillating

Do NOT immediately increase PID.

First check

* [ ] Encoder noise
* [ ] Limelight shaking
* [ ] Camera mounting
* [ ] Deadzone
* [ ] Mechanical play

Then tune

* kP
* kD
* Feedforward

---

# 🎯 Shooter Problems

## ❌ Shooter Doesn't Reach Velocity

Check

* [ ] Battery voltage
* [ ] Motors spinning freely
* [ ] Wheels rubbing
* [ ] Velocity mode enabled

Files

* ShooterSubsystem.java

Then verify

* Fast Velocity
* Slow Velocity
* Short Velocity
* PIDF

---

## ❌ Shooter Overshoots

Check

* [ ] PIDF values
* [ ] Velocity command
* [ ] Spin-up boost

---

## ❌ Shooter Never Stops

Check

```java
shooter.stop();
```

is being called.

---

# 🎯 Intake Problems

## Intake Not Running

Check

* [ ] Motor mapping
* [ ] Power
* [ ] Intake direction
* [ ] Jammed game pieces

---

## Intake Running Backwards

Check

```java
intake.setDirection(...)
```

---

# 🎯 Servo Problems

## Hudder Wrong Position

Check

* [ ] Servo name
* [ ] Servo direction
* [ ] Position values

Current Positions

| Position   | Value |
| ---------- | ----: |
| Initial    |  0.40 |
| Long Shot  |  0.22 |
| Short Shot |  0.12 |

---

## Stopper Doesn't Open

Check

* [ ] Servo plugged in
* [ ] Stopper values

| Position | Value |
| -------- | ----: |
| Open     |  0.60 |
| Closed   |  0.30 |

---

# 🎯 Drive Problems

## Robot Drives Crooked

Check

* [ ] Wheel directions
* [ ] Wheel installation
* [ ] Drive motor mapping
* [ ] Encoder directions

Files

* DriveSubsystem.java

---

## Robot Too Fast

Change

Drive Speed

BlueTeleOp.java

RedTeleOp.java

---

## Robot Too Slow

Check

* [ ] Battery
* [ ] Max Power
* [ ] Motor health

---

# 🎯 Autonomous Problems

## Robot Starts in Wrong Position

Check

Starting Pose

BlueUp

BlueUp4

RedUp

RedUp4

---

## Robot Misses Shooting Position

Check

* Path points
* Heading
* Drive speed
* PedroPathing path

---

## Robot Misses Intake

Check

* Intake path
* Intake timing
* Max power
* Intake direction

---

# 🎯 Turret Problems

## Wrong Angle

Check

* Field Angle
* Robot Heading
* IMU
* Pinpoint Heading

---

## Wrong Heading

Check

Pinpoint

```java
pinpoint.resetPosAndIMU();
```

---

# 🎯 Pinpoint Problems

## Position Incorrect

Check

* Encoder direction
* Encoder resolution
* Pod installation
* Reset IMU

---

# 🎯 PID Checklist

Before changing PID

* [ ] Hardware verified
* [ ] Battery above 13V
* [ ] Encoder working
* [ ] Motor direction correct
* [ ] Gearbox not slipping

Only then tune

* P
* I
* D
* F

---

# 📁 Files to Modify

| Problem           | File                                                  |
| ----------------- | ----------------------------------------------------- |
| Drive Speed       | BlueTeleOp.java / RedTeleOp.java                      |
| Shooter Velocity  | ShooterSubsystem.java                                 |
| Shooter PIDF      | ShooterSubsystem.java                                 |
| Hudder Position   | BlueUp.java / RedUp.java / BlueTeleOp.java            |
| Stopper Position  | ServoSubsystem.java                                   |
| Turret PID        | TurretSubsystem.java                                  |
| Limelight         | BlueTeleOp.java / RedTeleOp.java                      |
| Auto Paths        | BlueUp.java / RedUp.java / BlueUp4.java / RedUp4.java |
| Starting Position | BlueUp.java / BlueUp4.java / RedUp.java / RedUp4.java |

---

# ✅ Golden Rule

> **Always identify the problem first, verify the hardware second, and modify the code last. Record every change and test it before making another adjustment.**
