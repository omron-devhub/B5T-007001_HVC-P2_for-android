# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/cpp/src/include)

AUX_SOURCE_DIRECTORY(src/main/cpp/src/STB STB)
AUX_SOURCE_DIRECTORY(src/main/cpp/src/STB_FaceRecognition STBFr)
AUX_SOURCE_DIRECTORY(src/main/cpp/src/STB_Property STBPe)
AUX_SOURCE_DIRECTORY(src/main/cpp/src/STB_Tracker STBTr)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             stb

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/stblib.cpp
             ${STB} ${STBFr} ${STBPe} ${STBTr} )

# ライブラリプロジェクトのルート
# ここではこのファイルから２階層上を指定
set( LIB_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/libs )

# ライブラリの出力先
# 対応するプロセッサごとにフォルダを分けて出力
set( OUTPUT_DIR ${LIB_ROOT}/${ANDROID_ABI} )

# 出力先を指定（※STATICとはプロパティ名が違うので注意！）
set_target_properties( stb PROPERTIES LIBRARY_OUTPUT_DIRECTORY ${OUTPUT_DIR} )

# SHARED はこの時点でリンクするので、依存するライブラリがあるなら指示
target_link_libraries( stb
                       android
                       log )
