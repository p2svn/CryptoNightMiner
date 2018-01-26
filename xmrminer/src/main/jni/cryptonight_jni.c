#include <jni.h>
#include "cryptonight.c"

JNIEXPORT void
JNICALL Java_com_cpf_cryptonight_Miner_fastHash(JNIEnv *env, jobject j, jbyteArray input,
                                                jbyteArray output) {

    unsigned char *inputBuffer = (unsigned char *) (*env)->GetByteArrayElements(env, input, NULL);
    unsigned char *outputBuffer = (unsigned char *) (*env)->GetByteArrayElements(env, output, NULL);

    jsize inputSize = (*env)->GetArrayLength(env, input);
//    jsize outputSize = (*env)->GetArrayLength(env, output);

    cryptonight_hash(outputBuffer, inputBuffer, inputSize);

    (*env)->ReleaseByteArrayElements(env, input, (jbyte *) inputBuffer, JNI_ABORT);
    (*env)->ReleaseByteArrayElements(env, output, (jbyte *) outputBuffer, JNI_COMMIT);
}