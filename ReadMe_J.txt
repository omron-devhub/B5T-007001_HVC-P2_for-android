----------------------------------------------------
 B5T-007001 �T���v���R�[�h (Android��)
----------------------------------------------------

(1) �T���v���R�[�h���e
  �{�T���v����B5T-007001(HVC-P2)��Java API�N���X�Ƃ��̃N���X��p�����T���v���R�[�h��񋟂��܂��B
  �T���v���R�[�h�ł́u���o�����v�Ɓu��F�ؗp�f�[�^�o�^�v�����s�ł��܂��B
  
  �u���o�����v�ł͑S10�@�\�����s���A���̌��ʂ���ʏ�ɏo�͂��܂��B
  �܂��A�{�T���v���́u���艻���C�u����(STB library)�v���g�p���邱�Ƃ��ł�
  �����t���[�����ʂ�p�������ʂ̈��艻��g���b�L���O���\�ł��B
  �i��E�l�̂ɑ΂��ăg���b�L���O�A�N��E���ʁE�F�؂ɑ΂��Ĉ��艻�����s���Ă��܂��B�j
  
  �u��F�ؗp�f�[�^�o�^�v�ł͊�F�؃f�[�^��B5T-007001��̃A���o���ɓo�^���邱�Ƃ��ł��܂��B
  �i�{�T���v���ł�User ID = 0, Data ID = 0�̃A���o���f�[�^�ɂ̂ݓo�^�ł��܂��B�j

(2) �t�@�C������
  �ȉ��̃t�H���_�Ƀ\�[�X�t�@�C�������݂��܂��B
    HVC-P2_android_sample/app/src/main/java/jp/co/omron

  1. HvcP2_sample
      MainActivity.java             �T���v���R�[�h���C��
  2. HvcP2_Api
      P2Def.java                    ��`�l�N���X
      Connector.java                Connector�N���X�i�e�N���X�j
      SerialConnector.java          SerialConnector�N���X�iConnector�̃T�u�N���X�j
      HvcP2Api.java                 B5T-007001 Java API�N���X�i���ʈ��艻��j
      HvcP2Wrapper.java             B5T-007001 �R�}���h���b�p�N���X
      HvcResult.java                �R�}���h���s���ʊi�[�N���X�i���ʈ��艻�Ȃ��j
      HvcResultC.java               "HvcResult"��C����Java���b�p�N���X�iSTB�̓��͂Ƃ��Ďg�p�j
      HvcTrackingResult.java        �R�}���h���s���ʊi�[�N���X(���ʈ��艻��)
      HvcTrackingResultC.java       "HvcTrackingResult"��C����Java���b�p�N���X�iSTB�̏o�͂Ƃ��Ďg�p�j
      OkaoResult.java               �R�}���h���s���ʊi�[�N���X(���ʁj
      Stabilization.java            ���艻�N���X�`STB���C�u�����iC���C�u�����jWrapper
      GrayscaleImage.java           �o�͉摜�i�[�N���X

(3) �J����
  Android Studio                3.1.3
  Android SDK version           27

(4) B5T-007001 �� Android�[���̐ڑ��ɂ���
  USB OTG(On the Go) �P�[�u�����K�v�ł��B(USB OTG�P�[�u���͂��q�l�ɂĂ�������������)
  �ڑ����Ԃ͈ȉ��ɂȂ�܂��B
    [Android] -- [USB Host cable (USB OTG cable)] -- [USB cable] -- [B5T-007001]

  * �ڑ��̏ڍׂ́A��������摜(DeviceConnect.jpg)���Q�Ƃ��Ă��������B

[���g�p�ɂ�������]
�E�{�T���v���R�[�h����уh�L�������g�̒��쌠�̓I�������ɋA�����܂��B
�E�{�T���v���R�[�h�͓����ۏ؂�����̂ł͂���܂���B
�E�{�T���v���R�[�h�́AApache License 2.0�ɂĒ񋟂��Ă��܂��B

----
�I�������������
Copyright(C) 2018 OMRON Corporation, All Rights Reserved.
