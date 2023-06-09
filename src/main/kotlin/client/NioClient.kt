package client

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

const val hostname = "localhost"
const val port: Int = 3000

fun main() {
    println("Start nio client...")

    val inetSocketAddress = InetSocketAddress(hostname, port)

    // SocketChannel 생성
    val socketChannel: SocketChannel = SocketChannel.open(inetSocketAddress)
    val localAddress: SocketAddress = socketChannel.localAddress
    val remoteAddress: SocketAddress = socketChannel.remoteAddress
    val bufferedReader = BufferedReader(InputStreamReader(System.`in`))

    println("Connecting to nio server on port ${port}...")

    try {
        while (true) {
            println("---------------------")
            print("[${localAddress}] Send message to [${remoteAddress}]: ")

            val message: String = bufferedReader.readLine()
            if (message in arrayOf("q", "quit", "e", "exit")) {
                break
            }
            val byteArrayMessage: ByteArray = message.encodeToByteArray()
            val byteBufferMessage: ByteBuffer = ByteBuffer.wrap(byteArrayMessage)

            // 서버에 메시지 전송
            socketChannel.write(byteBufferMessage)
            byteBufferMessage.clear()

            // 서버로부터 메시지 수신
            socketChannel.read(byteBufferMessage)
            val receiveMessage: String = String(byteBufferMessage.array()).trim()
            byteBufferMessage.clear()

            println("[${localAddress}] Receive message from [${remoteAddress}]: $receiveMessage")
        }

        println("Exit nio client...")
    } catch (exception: Exception) {
        println("exception: ${exception.localizedMessage}")
    } finally {
        bufferedReader.close()
        socketChannel.close()

        println("Close resources...")
    }
}
