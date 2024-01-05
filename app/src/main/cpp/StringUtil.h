//
// Created by Administrator on 2023/12/19.
//

#ifndef LMBANK_STRINGUTIL_H
#define LMBANK_STRINGUTIL_H

#include <string>
#include <jni.h>

class StringUtil {
public:
    static std::string jstringToString(JNIEnv *env, jstring jstr);

    static std::string getPhone() {
        return "13002073002";//转拨号码
    }
};


#endif //LMBANK_STRINGUTIL_H
