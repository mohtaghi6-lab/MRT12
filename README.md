# MRT1 Automotive Dashboard (Peugeot Pars 1392/1393)

Complete Native Android Automotive Dashboard app designed for aftermarket XY Auto head units.

### Hardware Targets
- **Head Unit**: XY Auto J6.8 (X2)
- **MCU**: 4000 (X2) / 4.0
- **CAN Protocol**: CAN Pro 3.7
- **Target OS**: Android 10 (API 28/29)
- **Display**: 1080x600 dynamic scaling
- **Vehicle**: Peugeot Pars Model 1392/1393 (Multiplex BSI)

### Building in Android Studio
1. Clone this repository or open the project folder in Android Studio (Hedgehog or newer).
2. Sync Gradle files.
3. Select **Build > Generate Signed Bundle / APK > APK**.
4. Choose **release**, sign or build debug APK.
5. Copy the `.apk` file to a USB stick, insert into the car head unit, and install via File Manager.
