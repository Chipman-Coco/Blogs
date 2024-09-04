---
theme: healer-readable
---
> 大家好，我是Marko Chai！希望通过本篇文章带领各位Android开发者了解做为一名高级工程师必备的技能--NDK开发

## 引言

​		Android NDK（Native Development Kit）是一个工具集，让开发者可以使用 C 和 C++ 进行 Android 应用开发。通过 NDK，你可以在性能要求较高的部分使用本地代码，如游戏、音频处理、图像处理等。本指南将带你从入门到精通，全面了解和掌握 NDK 开发。

## 一、使用场景和优势

**1、高性能计算**

*   **游戏开发**：许多高性能游戏需要复杂的图形渲染和物理计算，使用 C/C++ 可以显著提高性能。
*   **图像处理**：图像处理算法，如滤波、边缘检测等，使用 C/C++ 可以更快地处理大批量数据。

**2、多媒体处理**

*   **音频处理**：实时音频处理需要低延迟和高效的计算能力，C/C++ 更适合这种场景。

*   **视频编解码**：使用 FFmpeg 等库进行视频编码、解码和处理，可以提高效率。

**3、跨平台开发**

*   **重用现有代码**：许多应用已经有大量的 C/C++ 代码，通过 NDK，可以直接在 Android 上重用这些代码，减少开发时间。

*   **跨平台库**：使用跨平台库（如 OpenCV、Boost）可以简化开发，确保在多个平台上代码的一致性。

*   **丰富的库支持**：可以使用大量的开源 C/C++ 项目和资源，如 OpenCV、FFmpeg、SQLite 等，快速实现复杂功能。

**4、加密和安全**

*   **加密算法**：许多加密算法在 C/C++ 中实现更为高效，并且能更好地保护敏感数据。
*   **反逆向**：通过使用 C/C++ 代码编译的`.so`库，可以保护一些关键算法和实现，增加逆向工程的难度。

## 二、创建NDK项目

使用Android Studio快速创建一个NDK项目，并且还提供了基本的配置和示例代码，方便我们去学习和开发。

### **1、安装 NDK 和 CMake**

首先，需要确保已经安装了Android NDK。这里我们通过Android Studio来安装：

1. 打开 Android Studio。

2. 依次选择 **File > Settings > Appearance & Behavior > System Settings > Android SDK**。

   <img src="https://github.com/Chipman-Coco/Blogs/blob/main/hellondk/img/image-20240525095624980.png?raw=true" alt="image-20240525095624980" style="zoom:80%;" />

<img src="https://github.com/Chipman-Coco/Blogs/blob/main/hellondk/img/image-20240525100424726.png?raw=true" alt="image-20240525100424726" style="zoom:80%;" />

3. 在 **SDK Tools** 选项卡下，展开 **NDK (Side by side)** 和 **CMake**，各自选择其中一个版本勾选（可以选择最新版本），然后点击 **Apply**。

   <img src="https://github.com/Chipman-Coco/Blogs/blob/main/hellondk/img/image-20240525100553129.png?raw=true" alt="image-20240525095624980" style="zoom:80%;" />

   <img src="https://github.com/Chipman-Coco/Blogs/blob/main/hellondk/img/image-20240525100642329.png?raw=true" alt="image-20240525095624980" style="zoom:80%;" />

### **2、创建新项目并配置NDK**

1. 创建一个新的 Android 项目，选择 **Native C++** 模板。

<img src="https://github.com/Chipman-Coco/Blogs/blob/main/hellondk/img/image-20240525100736730.png?raw=true" alt="image-20240525100736730" style="zoom:80%;" />

我这里创建的项目名称叫`HelloNdk`，然后点击`Next`。

<img src="https://github.com/Chipman-Coco/Blogs/blob/main/hellondk/img/image-20240525100857872.png?raw=true" alt="image-20240525100857872" style="zoom:80%;" />

最后直接点击`Finish`，然后等待工程创建完成即可。

<img src="https://github.com/Chipman-Coco/Blogs/blob/main/hellondk/img/image-20240525101236277.png?raw=true" alt="image-20240525101236277" style="zoom:80%;" />

2.  在创建的项目 `app/build.gradle` 文件中，会包含以下配置：

    ```groovy
    android {
        ...
        defaultConfig {
            ...
            externalNativeBuild {
                cmake {
                    // 这里可以指定使用的C++标准库版本。如：cppFlags "-std=c++11"，表示使用C++11标准库
                    cppFlags ""
                }
            }
        }
        ...
        externalNativeBuild {
            // CMake 配置
            cmake {
                path file('src/main/cpp/CMakeLists.txt') // CMakeLists.txt文件路径
                version '3.22.1' // CMake版本
            }
        }
    }
    ```

### **3、C++代码分析**

在项目的 `app/src/main/cpp` 目录下会有一个 `native-lib.cpp` C++ 源文件：

```c
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_me_marko_hellondk_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
```

**包含的头文件：**

*   `#include <jni.h>`：引入 JNI（Java Native Interface）的头文件，这个文件定义了 JNI 的所有函数和数据结构，允许 C/C++ 代码与 Java 代码进行交互。

*   `#include <string>`：引入 C++ 标准库中的 `string` 类，用于处理字符串。

**外部 C 语言链接说明：**

*   `extern "C"`：告诉编译器按照 C 语言的方式来编译和链接这段代码。这是因为 JNI 函数需要使用 C 语言的函数命名规则，而不是 C++ 的命名规则。
*   `JNIEXPORT` 和 `JNICALL`：这些是 JNI 的宏，分别定义了函数的导出属性和调用约定。`JNIEXPORT` 使得这个函数可以被导出，以便 Java 代码能够调用它；`JNICALL` 指定了函数调用的约定，确保函数能够被正确调用。

**JNI 函数定义：**

*   `Java_me_marko_hellondk_MainActivity_stringFromJNI`：这是 JNI 函数的名称。按照 JNI 的命名规则，函数名称由 `Java_` 前缀、Java 包名、Java 类名以及 Java 方法名组成。这里表示这个本地方法对应 Java 中的 `me.marko.hellondk.MainActivity` 类的 `stringFromJNI` 方法。

*   `JNIEnv* env`：这是指向 JNI 环境的指针，通过这个指针可以调用很多 JNI 提供的函数。

*   `jobject /* this */`：这是调用这个本地方法的 Java 对象实例。在静态方法中，这个参数会被替换为 `jclass`。

**函数体：**

*   `std::string hello = "Hello from C++";`：定义一个 C++ 字符串并赋值为 `"Hello from C++"`。

*   `return env->NewStringUTF(hello.c_str());`：使用 JNI 环境指针 `env` 调用 `NewStringUTF` 方法，将 C++ 字符串转换为 JNI 字符串（`jstring`）。`hello.c_str()` 将 C++ 字符串转换为 C 风格字符串（即 `const char*`），以便 `NewStringUTF` 函数使用。

### **4、CMakeLists.txt配置**

```cmake
cmake_minimum_required(VERSION 3.22.1)

project("native-lib")

add_library(${CMAKE_PROJECT_NAME} SHARED
        native-lib.cpp)
        
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)
```

1.  `cmake_minimum_required(VERSION 3.22.1)`

    设置此项目所需的最小CMake版本。这个版本号用于确保你使用的 CMake 版本具有项目配置所需的所有功能。

    *PS：在上述安装NDK的步骤中，我们安装的CMake版本是3.22.1。*

2.  `project("native-lib")`

    定义一个名为 ”native-lib“ 的项目。`project` 命令可以包含可选的语言参数（如 C、C++ 等），但在这里没有指定，因此默认支持 C 和 C++。

3.  `add_library(${CMAKE_PROJECT_NAME} SHARED native-lib.cpp)`

    创建了一个名为 `native-lib` 的共享库（动态库）。`SHARED` 表示生成共享库，而不是静态库。`native-lib.cpp` 是源文件。这里使用了变量 `CMAKE_PROJECT_NAME`，该变量会自动包含 `project` 指令中定义的项目名称。

    *   `${CMAKE_PROJECT_NAME}`：使用项目名称 `native-lib`。
    *   `SHARED`：指示生成共享库（动态库）。
    *   `native-lib.cpp`：构建库所需的源文件。

4.  `target_link_libraries(${CMAKE_PROJECT_NAME} android log)`

    用于将 `android` 和 `log` 库链接到 `native-lib` 库中。`android` 库和 `log` 库是 Android NDK 提供的常用库，分别用于与 Android 系统进行交互和记录日志。

    *   `${CMAKE_PROJECT_NAME}`：目标库的名字，即 `native-lib`。
    *   `android`：链接到 Android 库，提供 Android 特定的 API。
    *   `log`：链接到日志库，提供日志记录功能。

## 三、JNI基础

Java Native Interface (`JNI`) 是 Java 与 C/C++ 代码交互的桥梁。了解 `JNI` 是使用 NDK 的基础。上述内容中讲解了`JNI`函数的**静态注册**和**动态注册**，下面我们再学习它的其它内容。

### 1、数据类型转换、方法签名

**基本数据类型转换：**

|  Java类型 |   JNI类型  | Type Signature（类型签名） |
| :-----: | :------: | :------------------: |
|   byte  |   jbyte  |           B          |
|   char  |   jchar  |           C          |
|  double |  jdouble |           D          |
|  float  |  jfloat  |           F          |
|   int   |   jint   |           I          |
|  short  |  jshort  |           S          |
|   long  |   jlong  |           J          |
| boolean | jboolean |           Z          |
|   void  |   void   |           V          |

**引用数据类型转换：**

|   Java类型   |     JNI类型     |  Type Signature（类型签名） |
| :--------: | :-----------: | :-------------------: |
|   Object   |    jobject    |         L+类名+;        |
|    Class   |     jclass    |   Ljava/lang/Class;   |
|   String   |    jstring    |   Ljava/lang/String;  |
|  Throwable |   jthrowable  | Ljava/lang/Throwable; |
|  Object\[] |  jobjectArray |        \[L+类名+;       |
|   byte\[]  |   jbyteArray  |          \[B          |
|   char\[]  |   jcharArray  |          \[C          |
|  double\[] |  jdoubleArray |          \[D          |
|  float\[]  |  jfloatArray  |          \[F          |
|   int\[]   |   jintArray   |          \[I          |
|  short\[]  |  jshortArray  |          \[S          |
|   long\[]  |   jlongArray  |          \[J          |
| boolean\[] | jbooleanArray |          \[Z          |

### 2、JNI函数注册

JNI函数分为**静态注册**和**动态注册**两种（上述示例代码中使用的是**静态注册**的方式）：

（1）**静态注册**：它通过特定的命名约定直接在 Java 代码和本地代码之间建立关联。

```c
// 函数名必须遵循以下规则：
Java_<package>_<class>_<method>
```

*   `package` 是包名，使用下划线替代点。
*   `class` 是类名。
*   `method` 是方法名。

如下，`Java_me_marko_hellondk_MainActivity_stringFromJNI` 表示 `me.marko.hellondk.MainActivity` 类中的 `stringFromJNI` 方法。

```c
extern "C"
JNIEXPORT jstring JNICALL
Java_me_marko_hellondk_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
```

（2）**动态注册**：允许在运行时注册本地方法，而不是通过函数名的命名约定。这对于方法名的重命名、重载和跨类方法调用非常有用。

*   定义本地方法。

*   创建一个包含**方法名**、**方法签名**和**函数指针**的数组。

*   在 `JNI_OnLoad` 函数中调用 `JNIEnv` 的 `RegisterNatives` 方法注册这些方法。

示例代码：

```java
package me.marko.hellondk;

public class MainActivity extends AppCompatActivity {
    
    static {
        System.loadLibrary("native-lib");
    }

    // 对应下面C++文件中的Native方法
    public native String stringFromJNI();
}
```

```c
#include <jni.h>
#include <string>

// 定义Native方法
jstring stringFromJNI(JNIEnv* env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

// 定义方法数组
static JNINativeMethod methods[] = {
    // {"方法名", "方法签名", "函数指针"}
    {"stringFromJNI", "()Ljava/lang/String;", (void*)stringFromJNI}
};

// 动态注册方法
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    // 获取JNIEnv
    jint getEnvResult = vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if (getEnvResult != JNI_OK) {
        return JNI_ERR;
    }
    // 通过JNIEnv的FindClass函数找到JAVA层的MainActivity类
    jclass clazz = env->FindClass("me/marko/hellondk/MainActivity");
    if (clazz == nullptr) {
        return JNI_ERR;
    }
    // 数组长度 sizeof返回一个对象在内存中所占的存储空间，单位是byte
    jint len = sizeof(methods) / sizeof(methods[0]);
    // 通过JNIEnv的RegisterNatives函数注册methods数组中的方法
   	jint registerNativesResult = env->RegisterNatives(clazz, methods, len);
    if (registerNativesResult < 0) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}
```

*静态注册与动态注册的对比*

*   **静态注册**：

    优点：简单，易于实现。

    缺点：方法名、类名必须遵循特定命名规则，灵活性差。

*   **动态注册**：

    优点：灵活，可以在运行时注册，适用于方法重载和跨类方法调用。

    缺点：实现稍复杂，需要更多的代码。

**选择哪种注册方式**

​	1）如果项目简单且方法较少，静态注册通常是首选，因为实现更简单。

​	2）如果项目复杂，需要频繁变更方法名或有很多重载方法，动态注册可能更合适，因为它提供了更大的灵活性。

### 3、JavaVM 和 JNIEnv

JNI 定义了两个关键数据结构，即“JavaVM”和“JNIEnv”。两者本质上都是指向函数表的指针。（在 C++ 版本中，它们是一些类，这些类具有指向函数表的指针，以及通过表间接传递的每个 JNI 函数的成员函数）。JavaVM 提供“调用接口”函数，用于创建和销毁 JavaVM。理论上，每个进程可以有多个 JavaVM，但 Android 只允许有一个。

*   JNIEnv 提供了大部分 JNI 函数。您的原生函数都会接收 JNIEnv 作为第一个参数。

*   该 JNIEnv 将用于线程本地存储。因此，**您无法在线程之间共享 JNIEnv**。如果代码段无法通过其他方法获取其 JNIEnv，您应该共享 JavaVM，并使用 `GetEnv` 发现线程的 JNIEnv。

*   JNIEnv 和 JavaVM 的 C 声明与 C++ 声明不同。`"jni.h"` 包含文件会提供不同的类型定义符，具体取决于该文件是包含在 C 还是 C++ 中。因此，我们不建议在这两种语言包含的头文件中包含 JNIEnv 参数。（换个说法：如果您的头文件需要 `#ifdef __cplusplus`，且该头文件中的任何内容引用 JNIEnv，您可能需要执行一些额外的操作。）

#### 实战一：JNI中获取 Java 字段

1.  **获取类引用**：首先需要获取包含目标字段的 Java 类的引用。
2.  **获取字段 ID**：使用字段名和签名获取字段的唯一标识（字段 ID）。
3.  **获取字段值**：通过字段 ID 获取字段的值。

**示例代码：**

假设有一个 Java 类 `Student`，包含一个字符串字段 `name`和一个整型字段`age`：

```java
package me.marko.hellondk;

public class Student {
    
    public String name;
    public int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

以下是如何在 JNI 中获取 `name` 、`age`字段的值：

```c
#include <jni.h>
#include <string>

extern "C" JNIEXPORT void JNICALL
Java_me_marko_hellondk_MainActivity_getStudentFieldFromJNI(JNIEnv* env, jobject obj) {
    // 获取 Student 类引用
    jclass cls = env->FindClass("me/marko/hellondk/Student");

    // 获取字段 ID
    jfieldID name_fid = env->GetFieldID(cls, "name", "Ljava/lang/String;");
    jfieldID age_fid = env->GetFieldID(cls, "age", "I");

    // 获取字段值
    jstring nameValue = (jstring) env->GetObjectField(obj, name_fid);
    jint ageValue = (jint) env->GetIntField(obj, age_fid);

    // 将name字段值转换为 C 字符串
    const char* nameChars = env->GetStringUTFChars(nameValue, nullptr);

    std::cout << "Student name: " << nameChars << ", age: " << ageValue << std::endl;

    // 释放字符串内存
    env->ReleaseStringUTFChars(nameValue, nameChars);
}
```

1.  使用 `FindClass` 获取类的类对象引用

```c
jclass cls = env->FindClass("me/marko/hellondk/Student");
```

2.  使用 `GetFieldID` 获取字段的字段 ID

```c
jfieldID name_fid = env->GetFieldID(cls, "name", "Ljava/lang/String;");
jfieldID age_fid = env->GetFieldID(cls, "age", "I");
```

`GetFieldID` 函数获取一个字段的 ID。参数包括：

*   `cls`：类引用。
*   `"name"`、`"age"`：字段名。
*   `"Ljava/lang/String;"`、`"I"`：字段的签名（类型描述符），这里是分别是 `String` 类型和`int`类型的签名。

3.  使用适当内容获取字段的内容，例如 `GetIntField`

```c
/** 获取到Java类中的字段值 */
jstring nameValue = (jstring) env->GetObjectField(obj, name_fid);
jint ageValue = (jint) env->GetIntField(obj, age_fid);
```

`GetObjectField` 函数获取对象类型字段的值。对于基本数据类型，可以使用 `GetIntField`、`GetBooleanField` 等函数。

4.  处理字段值

    ```c
    const char* nameChars = env->GetStringUTFChars(nameValue, nullptr);
    ```

    `GetStringUTFChars` 函数将 `jstring` 转换为 C 字符串。

5.  释放内存

    ```c
    env->ReleaseStringUTFChars(nameValue, nameChars);
    ```

    `ReleaseStringUTFChars` 函数释放 `GetStringUTFChars` 分配的内存。


> JNIENV在C语言和C++中调用方式是有区别的：
>
>     C风格：(*env)->NewStringUTF(env, “Hellow World!”);
>
>     C++风格：env->NewStringUTF(“Hellow World!”);
>
> 注：C++风格其实就是对C风格的再次封装

## 四、CMake

在 Android NDK 项目中，CMake 是主要的构建工具之一。通过定义 `CMakeLists.txt` 文件，可以简化构建过程，并自动处理不同平台上的差异。

### 常用 CMake 指令

1.  **`cmake_minimum_required`**：指定最低版本的 CMake，确保项目在特定版本下构建。
2.  **`project`**：定义项目名称和语言。
3.  **`add_library`**：创建静态库、共享库或模块库。
4.  **`target_link_libraries`**：将库文件链接到目标文件，解决依赖关系。
5.  **`include_directories`**：添加头文件目录，使这些目录下的头文件可以被找到。
6.  **`find_library`**：查找系统中预编译的库文件，并将其路径存储到变量中。
7.  **`set`**：设置变量的值，用于存储路径、标志等。
8.  **`file`**：操作文件，如生成、读取、写入等。
9.  `message`：在配置过程中输出消息，用于调试。
10. `if` 和 `endif`：条件判断，用于控制配置过程中的逻辑。

### 编译变量与参数

官方文档地址: [CMake | Android NDK | Android Developers](https://developer.android.com/ndk/guides/cmake?hl=zh-cn)

-   CMake 工具链参数：

| **编译参数**                             | **说明**                                                                        |
| ------------------------------------ | ----------------------------------------------------------------------------- |
| **ANDROID_PLATFORM**                 | **指定目标Android平台的名称，如android-18指定Android 4.3(API级别18)**                        |
| ANDROID_STL                          | 指定CMake应使用的STL，默认c++_static                                                   |
| ANDROID_PIE                          | 指定是否使用位置独立的可执行文件(PIE)。Android动态链接器在Android 4.1(API级别16)及更高级别上支持PIE，可设置为On、OFF |
| ANDROID_CPP_FEATURES                 | 指定CMake编译原生库时需使用的特定C++功能，可设置为rtti(运行时类型信息)、exceptions(C++异常)                  |
| ANDROID_ALLOW_UNDEFINED_SYMBOLS      | 指定CMake在构建原生库时，如果遇到未定义的引用，是否会引发未定义的符号错误。默认FALSE                               |
| ANDROID_ARM_NEON                     | 指定CMake是否应构建支持NEON的原生库。API级别为23或更高级别时，默认值为true，否则为false                       |
| ANDROID_DISABLE_FORMAT_STRING_CHECKS | 指定是否在编译源代码时保护格式字符串。启用保护后，如果在printf样式函数中使用非常量格式字符串，则编译器会引发错误。默认false           |

-   Android进行交叉编译构建参数：

| **编译参数**                       | **说明**                                                         |
| ------------------------------ | -------------------------------------------------------------- |
| **ANDROID_ABI**                | **目标ABI，可设置为armeabi-v7a、arm64-v8a、x86、x86_64，默认armeabi**       |
| **ANDROID_NDK**                | **安装的NDK根目录的绝对路径**                                             |
| **CMAKE_TOOLCHAIN_FILE**       | **进行交叉编译的android.toolchain.cmake文件的路径，默认在$NDK/build/cmake/目录** |
| ANDROID_TOOLCHAIN              | CMake使用的编译器工具链，默认为clang                                        |
| CMAKE_BUILD_TYPE               | 配置构建类型，可设置为Release、Debug                                       |
| ANDROID_NATIVE_API_LEVEL       | CMake进行编译的Android API级别                                        |
| CMAKE_LIBRARY_OUTPUT_DIRECTORY | 构建LIBRARY目标文件之后，CMake存放这些文件的位置                                 |

> 一般情况下，只需要配置`ANDROID_ABI`、`ANDROID_NDK`、`CMAKE_TOOLCHAIN_FILE`、`ANDROID_PLATFORM`四个变量即可。
>
> - **ANDROID_ABI是CPU架构;**
> - **ANDROID_NDK是NDK的根目录;**
> - **CMAKE_TOOLCHAIN_FILE是工具链文件;**
> - **ANDROID_PLATFORM是支持的最低Android平台;**

编写编译脚本（示例）：

```sh
 #/bin/bash
 
 export ANDROID_NDK=/opt/env/android-ndk-r14b
 
 rm -r build
 mkdir build && cd build 
 
 cmake -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake \
     -DANDROID_ABI="armeabi-v7a" \
     -DANDROID_NDK=$ANDROID_NDK \
     -DANDROID_PLATFORM=android-22 \
     ..
 
 make && make install
 
 cd ..
```



## 补充一： 如何将C/C++日志信息输出到Logcat

由于C/C++中的`print("日志信息");`、`std::cout << "日志信息" << std::endl;`不会输出到Android的Logcat中，所以我们要想看到C/C++中的日志信息则需要借助NDK提供的log库进行输出。上述CMakeLists.txt中已经链接了log库。
```cmake
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)
```
* 在C/C++中就可以使用`__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__);`输出日志信息了，但是为了方便使用还需要简单封装一下。
```C++
#ifndef LOGUTIL_H
#define LOGUTIL_H

#include<android/log.h>

#define  LOG_TAG "NativeLog"

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

#endif //LOGUTIL_H
```
* 有了上面封装的`LogUtil.h`，现在就可以在C/C++中打印日志了。
```C++
#include "LogUtil.h" //导入日志工具

...
/** 这样C/C++中日志就会输出到Android Studio的Logcat中 */
LOGI("普通日志");
LOGD("调试日志");
LOGE("异常日志");
...
```



## 补充二：JNI中常用函数

- JNI访问调用对象

| 方法名         | 作用                                 |
| -------------- | ------------------------------------ |
| GetObjectClass | 获取调用对象的类，我们称其为target   |
| FindClass      | 根据类名获取某个类，我们称其为target |
| IsInstanceOf   | 判断一个类是否为某个类型             |
| IsSameObject   | 是否指向同一个对象                   |



- JNI访问java成员变量的值

| 方法名      | 作用                                                       |
| ----------- | ---------------------------------------------------------- |
| GetFieldId  | 根据变量名获取target中成员变量的ID                         |
| GetIntField | 根据变量ID获取int变量的值，对应的还有byte，boolean，long等 |
| SetIntField | 修改int变量的值，对应的还有byte，boolean，long等           |



- JNI访问java静态变量的值

| 方法名            | 作用                                                         |
| ----------------- | ------------------------------------------------------------ |
| GetStaticFieldId  | 根据变量名获取target中静态变量的ID                           |
| GetStaticIntField | 根据变量ID获取int静态变量的值，对应的还有byte，boolean，long等 |
| SetStaticIntField | 修改int静态变量的值，对应的还有byte，boolean，long等         |



- JNI访问java成员方法

| 方法名         | 作用                                                   |
| -------------- | ------------------------------------------------------ |
| GetMethodID    | 根据方法名获取target中成员方法的ID                     |
| CallVoidMethod | 执行无返回值成员方法                                   |
| CallIntMethod  | 执行int返回值成员方法，对应的还有byte，boolean，long等 |



- JNI访问java静态方法

| 方法名               | 作用                                                   |
| -------------------- | ------------------------------------------------------ |
| GetStaticMethodID    | 根据方法名获取target中静态方法的ID                     |
| CallStaticVoidMethod | 执行无返回值静态方法                                   |
| CallStaticIntMethod  | 执行int返回值静态方法，对应的还有byte，boolean，long等 |



- JNI访问java构造方法

| 方法名      | 作用                                                       |
| ----------- | ---------------------------------------------------------- |
| GetMethodID | 根据方法名获取target中构造方法的ID，注意，方法名传`<init>` |
| NewObject   | 创建对象                                                   |



- JNI创建引用

| 方法名           | 作用                                       |
| ---------------- | ------------------------------------------ |
| NewGlobalRef     | 创建全局引用                               |
| NewWeakGlobalRef | 创建弱全局引用                             |
| NewLocalRef      | 创建局部引用                               |
| DeleteGlobalRef  | 释放全局对象，引用不主动释放会导致内存泄漏 |
| DeleteLocalRef   | 释放局部对象，引用不主动释放会导致内存泄漏 |



- JNI异常处理

| 方法名            | 作用                       |
| ----------------- | -------------------------- |
| ExceptionOccurred | 判断是否有异常发生         |
| ExceptionClear    | 清除异常                   |
| Throw             | 往上(java层)抛出异常       |
| ThrowNew          | 往上(java层)抛出自定义异常 |



> Tips：NDK涉及的内容较多，做为Android开发者还需要掌握C/C++、CMake编译等。而且一般都会使用到第三方的C++库（如：FFmpeg），因此如何使用CMake将第三方的C++工程编译成Android使用的`.so`、`.a`库也是一件不简单的事情。

## 总结

通过本文的学习，你应该对 Android NDK 的安装、基础使用、CMake 配置以及高级技巧有了全面的了解。NDK 是一个强大的工具，可以帮助你在性能关键的应用中充分发挥 C 和 C++ 的优势。希望本文能帮助你在 NDK 开发的道路上不断进步，从入门到精通。
