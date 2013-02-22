package fengfei.berain.server.protobuf;

import java.util.List;
import java.util.Map;

import models.BerainResult;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;

import play.Invoker.InvocationContext;
import play.db.jpa.JPAPlugin;
import play.db.jpa.NoTransaction;
import play.db.jpa.Transactional;
import plugins.MyJPAPlugin;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

import controllers.BerainHelper;
import fengfei.berain.server.protobuf.BerainProto.BerainEntry;
import fengfei.berain.server.protobuf.BerainProto.BerainEntryResponse;
import fengfei.berain.server.protobuf.BerainProto.BerainEntrysResponse;
import fengfei.berain.server.protobuf.BerainProto.BerainService;
import fengfei.berain.server.protobuf.BerainProto.BoolResponse;
import fengfei.berain.server.protobuf.BerainProto.CopyRequest;
import fengfei.berain.server.protobuf.BerainProto.EntryRequest;
import fengfei.berain.server.protobuf.BerainProto.LoginRequest;
import fengfei.berain.server.protobuf.BerainProto.LoginResponse;
import fengfei.berain.server.protobuf.BerainProto.StatusResult;
import fengfei.berain.server.protobuf.BerainProto.StringResponse;
import fengfei.berain.server.protobuf.BerainProto.WatchableRequest;

public class BerainServiceImpl implements BerainService.BlockingInterface {
	private ChannelGroup channelGroup;

	public BerainServiceImpl(ChannelGroup channelGroup) {
		super();
		this.channelGroup = channelGroup;
	}

	public Channel getChannel(Integer id) {
		return channelGroup.find(id);
	}

	private void startTx() {

		play.Play.plugin(MyJPAPlugin.class).beforeInvocation();

	}

	private void closeTx() {
		play.Play.plugin(MyJPAPlugin.class).afterInvocation();
	}

	public void onInvocationException(Throwable e) {
		play.Play.plugin(MyJPAPlugin.class).closeTx(true);
	}

	@Override
	public LoginResponse login(RpcController controller, LoginRequest request)
			throws ServiceException {

		return null;
	}

	@Override
	public BoolResponse update(RpcController controller, EntryRequest request)
			throws ServiceException {
		BerainResult<Boolean> rs = BerainHelper.update(request.getKey(),
				request.getValue());

		return BoolResponse.newBuilder().setSuccessed(rs.data)
				.setResult(createStatusResult(rs)).build();
	}

	private StatusResult createStatusResult(BerainResult<?> brs) {
		StatusResult ss = StatusResult.newBuilder().setCode(brs.code)
				.setMessage(brs.message).build();
		return ss;
	}

	@Override
	public BoolResponse create(RpcController controller, EntryRequest request)
			throws ServiceException {
		startTx();
		BerainResult<Boolean> rs = BerainHelper.create(request.getKey(),
				request.getValue());
		closeTx();
		return BoolResponse.newBuilder().setSuccessed(rs.data)
				.setResult(createStatusResult(rs)).build();
	}

	@Override
	public BoolResponse delete(RpcController controller, EntryRequest request)
			throws ServiceException {
		BerainResult<Boolean> rs = BerainHelper.delete(request.getKey());

		return BoolResponse.newBuilder().setSuccessed(rs.data)
				.setResult(createStatusResult(rs)).build();
	}

	@Override
	public BoolResponse copy(RpcController controller, CopyRequest request)
			throws ServiceException {
		BerainResult<Boolean> rs = BerainHelper.copy(request.getOriginalId(),
				request.getNewid());

		return BoolResponse.newBuilder().setSuccessed(rs.data)
				.setResult(createStatusResult(rs)).build();
	}

	@Override
	public BerainEntrysResponse nextChildren(RpcController controller,
			EntryRequest request) throws ServiceException {
		BerainResult<List<Map<String, String>>> rs = BerainHelper
				.nextChildren(request.getKey());
		List<Map<String, String>> vs = rs.getData();
		BerainEntrysResponse.Builder bb = BerainEntrysResponse.newBuilder();
		for (int i = 0; i < vs.size(); i++) {
			Map<String, String> map = vs.get(i);

			BerainEntry.Builder builder = BerainEntry.newBuilder();
			builder.setKey(map.get("key"));
			builder.setValue(map.get("value"));
			builder.setPath(map.get("path"));
			BerainEntry berainEntry = builder.build();
			bb.setEntrys(i, berainEntry);
		}

		return bb.setResult(createStatusResult(rs)).build();
	}

	@Override
	public StringResponse get(RpcController controller, EntryRequest request)
			throws ServiceException {
		BerainResult<String> rs = BerainHelper.get(request.getKey());

		return StringResponse.newBuilder().setValue(rs.data)
				.setResult(createStatusResult(rs)).build();
	}

	@Override
	public BerainEntryResponse getFull(RpcController controller,
			EntryRequest request) throws ServiceException {
		BerainResult<Map<String, String>> rs = BerainHelper.getFull(request
				.getKey());
		Map<String, String> map = rs.getData();
		BerainEntryResponse.Builder bb = BerainEntryResponse.newBuilder();

		BerainEntry.Builder builder = BerainEntry.newBuilder();
		builder.setKey(map.get("key"));
		builder.setValue(map.get("value"));
		builder.setPath(map.get("path"));
		BerainEntry berainEntry = builder.build();
		bb.setEntrys(berainEntry);

		return bb.setResult(createStatusResult(rs)).build();
	}

	@Override
	public BoolResponse exists(RpcController controller, EntryRequest request)
			throws ServiceException {
		BerainResult<Boolean> rs = BerainHelper.exists(request.getKey());
		return BoolResponse.newBuilder().setSuccessed(rs.data)
				.setResult(createStatusResult(rs)).build();
	}

	@Override
	public BoolResponse addWatchable(RpcController controller,
			WatchableRequest request) throws ServiceException {
		BerainResult<Boolean> rs = BerainHelper.addWatchable(String
				.valueOf(request.getClientId()), request.getId(), request
				.getEventType().getNumber());
		return BoolResponse.newBuilder().setSuccessed(rs.data)
				.setResult(createStatusResult(rs)).build();
	}

	@Override
	public BoolResponse removeWatchable(RpcController controller,
			WatchableRequest request) throws ServiceException {
		BerainResult<Boolean> rs = BerainHelper.removeWatchable(String
				.valueOf(request.getClientId()), request.getId(), request
				.getEventType().getNumber());
		return BoolResponse.newBuilder().setSuccessed(rs.data)
				.setResult(createStatusResult(rs)).build();
	}

	@Override
	public BoolResponse removeAllListener(RpcController controller,
			WatchableRequest request) throws ServiceException {
		BerainResult<Boolean> rs = BerainHelper.removeAllListener(String
				.valueOf(request.getClientId()));
		return BoolResponse.newBuilder().setSuccessed(rs.data)
				.setResult(createStatusResult(rs)).build();
	}
}
