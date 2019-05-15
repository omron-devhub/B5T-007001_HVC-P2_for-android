----------------------------------------------------
 B5T-007001 サンプルコード (Android版)
----------------------------------------------------

(1) サンプルコード内容
  本サンプルはB5T-007001(HVC-P2)のJava APIクラスとそのクラスを用いたサンプルコードを提供します。
  サンプルコードでは「検出処理」と「顔認証用データ登録」が実行できます。
  
  「検出処理」では全10機能を実行し、その結果を画面上に出力します。
  また、本サンプルは「安定化ライブラリ(STB library)」を使用することもでき
  複数フレーム結果を用いた結果の安定化やトラッキングが可能です。
  （顔・人体に対してトラッキング、年齢・性別・認証に対して安定化を実行しています。）
  
  「顔認証用データ登録」では顔認証データをB5T-007001上のアルバムに登録することができます。
  （本サンプルではUser ID = 0, Data ID = 0のアルバムデータにのみ登録できます。）

(2) ファイル説明
  以下のフォルダにソースファイルが存在します。
    HVC-P2_android_sample/app/src/main/java/jp/co/omron

  1. HvcP2_sample
      MainActivity.java             サンプルコードメイン
  2. HvcP2_Api
      P2Def.java                    定義値クラス
      Connector.java                Connectorクラス（親クラス）
      SerialConnector.java          SerialConnectorクラス（Connectorのサブクラス）
      HvcP2Api.java                 B5T-007001 Java APIクラス（結果安定化後）
      HvcP2Wrapper.java             B5T-007001 コマンドラッパクラス
      HvcResult.java                コマンド実行結果格納クラス（結果安定化なし）
      HvcResultC.java               "HvcResult"のC言語Javaラッパクラス（STBの入力として使用）
      HvcTrackingResult.java        コマンド実行結果格納クラス(結果安定化後)
      HvcTrackingResultC.java       "HvcTrackingResult"のC言語Javaラッパクラス（STBの出力として使用）
      OkaoResult.java               コマンド実行結果格納クラス(共通）
      Stabilization.java            安定化クラス～STBライブラリ（Cライブラリ）Wrapper
      GrayscaleImage.java           出力画像格納クラス

(3) 開発環境
  Android Studio                3.1.3
  Android SDK version           27

(4) B5T-007001 と Android端末の接続について
  USB OTG(On the Go) ケーブルが必要です。(USB OTGケーブルはお客様にてご準備ください)
  接続順番は以下になります。
    [Android] -- [USB Host cable (USB OTG cable)] -- [USB cable] -- [B5T-007001]

  * 接続の詳細は、同梱する画像(DeviceConnect.jpg)を参照してください。

[ご使用にあたって]
・本サンプルコードおよびドキュメントの著作権はオムロンに帰属します。
・本サンプルコードは動作を保証するものではありません。
・本サンプルコードは、Apache License 2.0にて提供しています。

----
オムロン株式会社
Copyright(C) 2018 OMRON Corporation, All Rights Reserved.
