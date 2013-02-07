package fengfei.berain.server.protobuf;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

import fengfei.berain.server.protobuf.BerainProto.BerainEntryResponse;
import fengfei.berain.server.protobuf.BerainProto.BerainService;
import fengfei.berain.server.protobuf.BerainProto.BoolResponse;
import fengfei.berain.server.protobuf.BerainProto.CopyRequest;
import fengfei.berain.server.protobuf.BerainProto.EntryRequest;
import fengfei.berain.server.protobuf.BerainProto.LoginRequest;
import fengfei.berain.server.protobuf.BerainProto.LoginResponse;
import fengfei.berain.server.protobuf.BerainProto.StringResponse;
import fengfei.berain.server.protobuf.BerainProto.WatchableRequest;

public class BerainServiceImpl implements BerainService.BlockingInterface {

	@Override
	public LoginResponse login(RpcController controller, LoginRequest request)
			throws ServiceException {
 
		return null;
	}

	@Override
	public BoolResponse update(RpcController controller, EntryRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoolResponse create(RpcController controller, EntryRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoolResponse delete(RpcController controller, EntryRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoolResponse copy(RpcController controller, CopyRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BerainEntryResponse nextChildren(RpcController controller,
			EntryRequest request) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringResponse get(RpcController controller, EntryRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BerainEntryResponse getFull(RpcController controller,
			EntryRequest request) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoolResponse exists(RpcController controller, EntryRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoolResponse addWatchable(RpcController controller,
			WatchableRequest request) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoolResponse removeWatchable(RpcController controller,
			WatchableRequest request) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoolResponse removeAllListener(RpcController controller,
			EntryRequest request) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
