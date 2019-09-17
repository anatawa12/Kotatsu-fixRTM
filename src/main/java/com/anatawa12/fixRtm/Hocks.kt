@file:Suppress("unused") // Used with Transform

package com.anatawa12.fixRtm

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.Unpooled
import io.netty.util.ByteProcessor
import jp.ngt.ngtlib.event.TickProcessEntry
import jp.ngt.rtm.modelpack.ResourceType
import jp.ngt.rtm.modelpack.modelset.ResourceSet
import java.io.*
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.channels.GatheringByteChannel
import java.nio.channels.InterruptedByTimeoutException
import java.nio.channels.ScatteringByteChannel
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.zip.GZIPOutputStream
import java.util.zip.Inflater
import kotlin.concurrent.thread
import kotlin.math.log

object ExModelPackManager {
    var dummyMap: Map<String, ResourceSet<*>>
        get() = error("impl in gen")
        set(v) = error("impl in gen")

    @Suppress("UNCHECKED_CAST")
    val allModelSetMap: MutableMap<ResourceType<*, *>, MutableMap<String, ResourceSet<*>>>
            = jp.ngt.rtm.modelpack.ModelPackManager::class.java.getDeclaredField("allModelSetMap")
            .apply { isAccessible = true }
            .get(ModelPackManager) as MutableMap<ResourceType<*, *>, MutableMap<String, ResourceSet<*>>>
}

fun eraseNullForModelSet(inSet: ResourceSet<*>?, type: ResourceType<*, *>): ResourceSet<*> {
    if (inSet != null) return inSet
    if (type.hasSubType) {
        return ExModelPackManager.dummyMap[type.name]
                ?: (ExModelPackManager.dummyMap[type.subType])
                ?: error("ResourceType(${type.name}) and ResourceType(${type.subType}) don't have dummy ResourceSet")
    } else {
        return ExModelPackManager.dummyMap[type.name]
                ?: error("ResourceType(${type.name}) don't have dummyMap")
    }
}

private fun Any?.defaultToString(): String = if (this == null) {
    "null"
} else {
    this.javaClass.name + "@" + Integer.toHexString(System.identityHashCode(this))
}

fun preProcess(entry: TickProcessEntry?) {
}

fun postProcess(isEnd: Boolean) {
}

fun postProcess() {
}

fun preProcess() {
}
fun eraseNullForAddTickProcessEntry(addEntry: TickProcessEntry?, inEntry: TickProcessEntry?) {

    requireNotNull(inEntry) {
        "TickProcessQueue.add's first argument is null. fixRtm (made by anataqa12) found a bug! this is a bug from RTM and anatawa12 think this is good trace for fix bug."
    }
}

fun wrapWithDeflate(byteBuf: ByteBuf): ByteBuf {
    return DeflateByteBuf(byteBuf)
}

fun writeToDeflate(byteBuf: ByteBuf) {
    (byteBuf as DeflateByteBuf).writeDeflated()
}

fun readFromDeflate(byteBuf: ByteBuf): ByteBuf {
    val inf = Inflater()

    var readBuf: ByteArray? = ByteArray(byteBuf.readableBytes())
    byteBuf.readBytes(readBuf)
    inf.setInput(readBuf)
    println("received packet size: ${readBuf?.size}")
    readBuf = null

    val result = Unpooled.buffer()
    val buf = ByteArray(1024)
    while (true) {
        val len = inf.inflate(buf)
        if (len == 0) break
        result.writeBytes(buf, 0, len)
    }
    println("real packet size: ${result.readableBytes()}")
    println("real packet: $result")
    return result
}
