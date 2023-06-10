package server

import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

fun main() {
    println("Start nio server...")

    // 연결 정보
    val hostname = "localhost"
    val port = 3000

    // Selector 생성
    val selector: Selector = Selector.open()

    // ServerSocketChannel 생성
    // TCP 연결을 수신 대기하는 채널이다.
    val serverSocketChannel: ServerSocketChannel = ServerSocketChannel.open()
    serverSocketChannel.configureBlocking(false) // Non-Blocking
    serverSocketChannel.bind(InetSocketAddress(hostname, port))
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)

    while (true) {
        // accept, connect, read, write 이벤트에 대한 준비되어 있는 채널 수를 반환한다.
        // select() 메서드는 하나 이상의 채널이 준비될 때까지 Block 된다.
        val eventReadyChannelCount: Int = selector.select()

        println("---------------------")
        println("[${serverSocketChannel.localAddress}] Count of channels ready for the event: $eventReadyChannelCount")

        if (eventReadyChannelCount == 0) {
            continue
        }

        val selectedKeys: Set<SelectionKey> = selector.selectedKeys()
        val selectionKeyIterator: MutableIterator<SelectionKey> =
            selectedKeys.iterator() as MutableIterator<SelectionKey>

        // 준비된 채널이 존재하는 경우
        while (selectionKeyIterator.hasNext()) {

            val selectionKey: SelectionKey = selectionKeyIterator.next()

            if (selectionKey.isAcceptable) {
                registerSocketChannel(serverSocketChannel = serverSocketChannel, selector = selector)
            }

            if (selectionKey.isReadable) {
                readAndWrite(selectionKey = selectionKey)
            }

            selectionKeyIterator.remove()
        }
    }
}

private fun registerSocketChannel(serverSocketChannel: ServerSocketChannel, selector: Selector) {
    try {
        val socketChannel: SocketChannel? = serverSocketChannel.accept()

        socketChannel?.run {
            socketChannel.configureBlocking(false) // Non-Blocking
            socketChannel.register(selector, SelectionKey.OP_READ)

            println("[${socketChannel.remoteAddress}] New client connected...")
        }
    } catch (exception: Exception) {
        println("[registerSocketChannel] exception: ${exception.localizedMessage}")
    }
}

private fun readAndWrite(selectionKey: SelectionKey) {
    try {
        // Buffer 생성
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(256)
        // read 이벤트에 대한 채널
        val socketChannel: SocketChannel = selectionKey.channel() as SocketChannel
        val localAddress: SocketAddress = socketChannel.localAddress
        val remoteAddress: SocketAddress = socketChannel.remoteAddress
        // 버퍼에서 채널로 데이터 읽기
        val receiveMessageSize: Int = socketChannel.read(byteBuffer)
        val receiveMessage: String = String(byteBuffer.array()).trim()

        println("[${localAddress}] Receive message from [${remoteAddress}]: $receiveMessage")

        if (receiveMessageSize == -1) {
            socketChannel.close() // 채널 종료

            println("[${localAddress}] Not accepting [${remoteAddress}] messages anymore...")
        } else {
            byteBuffer.flip() // 버퍼를 '읽기' 모드에서 '쓰기' 모드로 전환한다.
            socketChannel.write(byteBuffer) // 클라이언트로 메시지 전송

            println("[${localAddress}] Send message to [${remoteAddress}]: $receiveMessage")
        }

        byteBuffer.clear()
    } catch (exception: Exception) {
        println("[readAndWrite] exception: ${exception.localizedMessage}")
    }
}
