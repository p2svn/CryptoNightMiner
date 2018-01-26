package com.cpf.cryptonight

/**
 * Created by cpf on 2017/12/28.
 * Miner
 */
object Miner {

    init {
        System.loadLibrary("CryptoNight")
    }

    external fun fastHash(input: ByteArray, output: ByteArray)

}