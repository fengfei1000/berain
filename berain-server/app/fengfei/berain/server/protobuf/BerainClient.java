package fengfei.berain.server.protobuf;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.BasicConfigurator;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import play.Play;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.netty.NettyRpcChannel;
import com.googlecode.protobuf.netty.NettyRpcClient;

import fengfei.berain.server.protobuf.BerainProto.BerainService;
import fengfei.berain.server.protobuf.BerainProto.BoolResponse;
import fengfei.berain.server.protobuf.BerainProto.EntryRequest;

public class BerainClient {

	/**
	 * @param args
	 * @throws ServiceException
	 */
	public static void main(String[] args) throws ServiceException {
		BasicConfigurator.configure();
 
		NettyRpcClient client = new NettyRpcClient(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		NettyRpcChannel channel = client.blockingConnect(new InetSocketAddress(
				"localhost", 18023));
		BerainService.BlockingInterface bs = BerainService
				.newBlockingStub(channel);
		final RpcController controller = channel.newRpcController();
		EntryRequest request = EntryRequest.newBuilder().setCt(0)
				.setKey("/berain/key1").setValue("v1").build();
		BoolResponse br=	bs.create(controller, request);
		System.out.println(br.toString());
		channel.close();
	}
}
