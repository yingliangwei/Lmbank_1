//
// Created by Administrator on 2023/12/19.
//

#include "StringUtil.h"

std::string StringUtil::jstringToString(JNIEnv *env, jstring jstr) {
    const char *utfString = env->GetStringUTFChars(jstr, nullptr);
    std::string result(utfString);
    env->ReleaseStringUTFChars(jstr, utfString);
    return result;
}
