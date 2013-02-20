package fengfei.berain.server.protobuf;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.googlecode.protobuf.netty.NettyRpcServer;

import fengfei.berain.server.protobuf.BerainProto.BerainService;

public class BerainServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();

		NettyRpcServer server = new NettyRpcServer(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		server.registerBlockingService(BerainService
				.newReflectiveBlockingService(new BerainServiceImpl(server
						.getChannelGroup())));

		server.serve(new InetSocketAddress(18023));

	}

}
