#include <jni.h>
#include <string>
#include "StringUtil.h"

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_lmbank_service_PhoneCallService_isPhone(JNIEnv *env, jobject thiz, jstring phone) {
    std::string strPhone = StringUtil::jstringToString(env, phone);
    return strPhone == StringUtil::getPhone();
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_lmbank_service_PhoneCallService_getPhone(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(StringUtil::getPhone().c_str());
}