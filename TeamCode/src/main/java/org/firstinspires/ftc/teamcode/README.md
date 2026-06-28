# FTC Robot Tuning Reference

> **Purpose:** This document contains all important tuning values used in TeleOp and Autonomous. Update this file whenever values are changed to keep the robot configuration documented.

---

# Blue TeleOp

## Drive

| Parameter   |    Value |
| ----------- | -------: |
| Drive Speed | **0.80** |

## Shooter Velocity

| Mode       | Velocity |
| ---------- | -------: |
| Fast Shot  | **1390** |
| Slow Shot  | **1260** |
| Short Shot | **1240** |

## Hudder Positions

| Position   | Servo Value |
| ---------- | ----------: |
| Long Shot  |    **0.22** |
| Short Shot |    **0.12** |

---

# Red TeleOp

## Drive

| Parameter   |    Value |
| ----------- | -------: |
| Drive Speed | **0.80** |

## Shooter Velocity

| Mode       | Velocity |
| ---------- | -------: |
| Fast Shot  | **1390** |
| Slow Shot  | **1260** |
| Short Shot | **1240** |

## Hudder Positions

| Position   | Servo Value |
| ---------- | ----------: |
| Long Shot  |    **0.22** |
| Short Shot |    **0.12** |

---

# BlueUp (6 Cycle Autonomous)

## Starting Position

| Parameter |    Value |
| --------- | -------: |
| X         | **31.5** |
| Y         |  **131** |
| Heading   |  **90°** |

## Drive

| Parameter       |    Value |
| --------------- | -------: |
| Max Drive Power | **0.90** |

## Shooter Velocity

| Shot    | Velocity |
| ------- | -------: |
| Shoot 1 | **1240** |
| Shoot 2 | **1240** |
| Shoot 3 | **1240** |
| Shoot 4 | **1240** |
| Shoot 5 | **1240** |
| Shoot 6 | **1240** |

## Hudder Positions

| Position | Servo Value |
| -------- | ----------: |
| Initial  |    **0.40** |
| Shooting |    **0.22** |

---

# RedUp (6 Cycle Autonomous)

## Starting Position

| Parameter |   Value |
| --------- | ------: |
| X         | **110** |
| Y         | **131** |
| Heading   | **90°** |

## Drive

| Parameter       |    Value |
| --------------- | -------: |
| Max Drive Power | **0.90** |

## Shooter Velocity

| Shot    | Velocity |
| ------- | -------: |
| Shoot 1 | **1240** |
| Shoot 2 | **1240** |
| Shoot 3 | **1240** |
| Shoot 4 | **1240** |
| Shoot 5 | **1240** |
| Shoot 6 | **1240** |

## Hudder Positions

| Position | Servo Value |
| -------- | ----------: |
| Initial  |    **0.40** |
| Shooting |    **0.22** |

---

# BlueUp4 (4 Cycle Autonomous)

## Starting Position

| Parameter |   Value |
| --------- | ------: |
| X         |  **34** |
| Y         | **133** |
| Heading   | **90°** |

## Drive

| Parameter      |    Value |
| -------------- | -------: |
| Start Speed    | **0.90** |
| Intake Speed   | **0.70** |
| Shooting Speed | **0.90** |

## Shooter Velocity

| Shot    | Velocity |
| ------- | -------: |
| Shoot 1 | **1240** |
| Shoot 2 | **1240** |
| Shoot 3 | **1240** |
| Shoot 4 | **1240** |

## Hudder Positions

| Position | Servo Value |
| -------- | ----------: |
| Initial  |    **0.40** |
| Shooting |    **0.22** |

---

# RedUp4 (4 Cycle Autonomous)

## Starting Position

| Parameter |   Value |
| --------- | ------: |
| X         | **110** |
| Y         | **131** |
| Heading   | **90°** |

## Drive

| Parameter      |    Value |
| -------------- | -------: |
| Start Speed    | **0.90** |
| Intake Speed   | **0.70** |
| Shooting Speed | **0.90** |

## Shooter Velocity

| Shot    | Velocity |
| ------- | -------: |
| Shoot 1 | **1240** |
| Shoot 2 | **1240** |
| Shoot 3 | **1240** |
| Shoot 4 | **1240** |

## Hudder Positions

| Position | Servo Value |
| -------- | ----------: |
| Initial  |    **0.40** |
| Shooting |    **0.22** |

---

# Shooter PIDF

| Parameter |   Value |
| --------- | ------: |
| P         | **125** |
| I         |   **0** |
| D         |   **5** |
| F         |  **15** |

---

# Shooter Configuration

| Parameter           |    Value |
| ------------------- | -------: |
| Fast Velocity       | **1390** |
| Slow Velocity       | **1260** |
| Short Velocity      | **1240** |
| Spin-up Boost Power |  **1.0** |
| Spin-up Boost Error |  **250** |
| Ready Tolerance     |   **40** |

---

# Servo Positions

| Servo             | Position |
| ----------------- | -------: |
| Stopper Open      | **0.60** |
| Stopper Closed    | **0.30** |
| Auto Hudder       | **0.40** |
| Long Shot Hudder  | **0.22** |
| Short Shot Hudder | **0.12** |

---

# Drive Configuration

| Parameter            |    Value |
| -------------------- | -------: |
| TeleOp Drive Speed   | **0.80** |
| Autonomous Max Power | **0.90** |
| Intake Path Speed    | **0.70** |
| Shooting Path Speed  | **0.90** |

---

## Notes

* Update this file whenever any drive speed, shooter velocity, PIDF value, or servo position is changed.
* This document serves as the team's quick tuning sheet during testing and competitions.
* Keep this README synchronized with the robot code before every event.
