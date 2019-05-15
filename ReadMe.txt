----------------------------------------------------
 B5T-007001 Sample Code （for Android）
----------------------------------------------------

(1) Contents
  This code provides B5T-007001(HVC-P2) Java API class and sample code using that API class.
  With this sample code, you can execute "Detection Process" and "Registration Process for Face Recognition".

  The "Detection Process" can execute any functions in all 10 functions of B5T-007001,
  and outputs the result on the display.
  And this sample is avalable to use STB library which stabilizes the detected results by multiple frames
  and tracks detected faces and bodies.

  The "Registration Process" can execute the registration of the Face Recognition data
  on the album of B5T-007001.
  ( This code can register to only User ID = 0, Data ID = 0 on the album. )

(2) File description
  The source code exists in the following folder.
  - HVC-P2_android_sample/app/src/main/java/jp/co/omron

  1. HvcP2_sample
      MainActivity.java             Sample code main
  2. HvcP2_Api
      P2Def.java                    Numerical Value Definitions
      Connector.java                Connector parent class
      SerialConnector.java          Serial connector class（Connector sub-class）
      HvcP2Api.java                 B5T-007001 Java API class with applying STB Library
      HvcP2Wrapper.java             B5T-007001 command wrapper class
      HvcResult.java                Class storing the results after executing commands without applying STB Library
      HvcResultC.java               Wrapper class for C language of "HvcResult", which is used for STB-input
      HvcTrackingResult.java        Class storing the results after executing commands with applying STB Library
      HvcTrackingResultC.java       Wrapper class for C language of "HvcTrackingResult", which is used for STB-output
      OkaoResult.java               Class storing the results after executing commands (in common)
      Stabilization.java            STB library(C Library) java class
      GrayscaleImage.java           Class storing output image

(3) Development environment for building
  Android Studio                3.1.3
  Android SDK version           27

(4) About connecting Android device with B5T-007001
  For connecting B5T-007001 to Android device, it is required to prepare a USB Host cable (USB OTG cable) in advance.
  Please prepare this type of cable by yourself.
  The connection order is as follows. 
    [Android] -- [USB Host cable (USB OTG cable)] -- [USB cable] -- [HVC-P2]

  * For further details, please refer to the image file(DeviceConnect.jpg), stored in the same package.

[NOTES ON USAGE]
* This sample code and documentation are copyrighted property of OMRON Corporation
* This sample code does not guarantee proper operation
* This sample code is distributed in the Apache License 2.0.

----
OMRON Corporation 
Copyright 2018 OMRON Corporation, All Rights Reserved.
