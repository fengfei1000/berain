package plugins;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import play.Logger;
import play.PlayPlugin;

import com.googlecode.protobuf.netty.NettyRpcServer;

import fengfei.berain.server.protobuf.BerainProto.BerainService;
import fengfei.berain.server.protobuf.BerainServiceImpl;

public class BerainServerInitPlugIn extends PlayPlugin {
	@Override
	public void onApplicationStart() {

		BasicConfigurator.configure();

		final NettyRpcServer server = new NettyRpcServer(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		server.registerBlockingService(BerainService
				.newReflectiveBlockingService(new BerainServiceImpl(server
						.getChannelGroup())));
		// Logger.trace("Berain Server started for port 18023.");

		new Thread() {
			public void run() {
				server.serve(new InetSocketAddress(18023));
			};
		}.start();
	}

}
