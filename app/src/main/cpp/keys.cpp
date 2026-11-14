#include <jni.h>

#include "keys_config.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_voidx_nba_core_network_NativeKeyProvider_nativeApiKey(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF(API_KEY);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_voidx_nba_core_network_NativeKeyProvider_nativeBaseUrl(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF(BASE_URL);
}
