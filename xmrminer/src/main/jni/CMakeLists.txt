cmake_minimum_required(VERSION 3.6)
project (CryptoNight)

include_directories(
	crypto/
)

set (HEADER_FILES
	crypto/c_blake256.h
	crypto/c_groestl.h
	crypto/c_jh.h
	crypto/c_keccak.h
	crypto/c_skein.h
	crypto/hash.h
	crypto/oaes_lib.h

	crypto/groestl_tables.h
	crypto/hash-ops.h
	crypto/int-util.h
	crypto/oaes_config.h
	crypto/skein_port.h
	crypto/sph_types.h

	compat/compat.h
)

set (SOURCE_FILES
	cryptonight.c
    cryptonight_jni.c
	crypto/aesb.c
	crypto/c_blake256.c
	crypto/c_groestl.c
	crypto/c_jh.c
	crypto/c_keccak.c
	crypto/c_skein.c
	crypto/hash.c
	crypto/oaes_lib.c
)

add_definitions(-D_CRT_SECURE_NO_WARNINGS)

add_library(CryptoNight SHARED
	${HEADER_FILES}
	${SOURCE_FILES})