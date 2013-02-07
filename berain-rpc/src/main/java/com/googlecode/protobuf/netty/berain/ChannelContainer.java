package com.googlecode.protobuf.netty.berain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class ChannelContainer {
	private static ConcurrentHashMap<String, ChannelContainer> channelContainers = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<Integer, Channel> channels = new ConcurrentHashMap<>();
	private final ChannelFutureListener remover = new ChannelFutureListener() {
		public void operationComplete(ChannelFuture future) throws Exception {
			remove(future.getChannel());
		}
	};

	private ChannelContainer() {

	}

	public static ChannelContainer get(String id) {
		ChannelContainer cc = channelContainers.get(id);
		if (cc == null) {
			cc = new ChannelContainer();

			channelContainers.put(id, cc);
		}
		return cc;
	}

	public void add(Channel channel) {
		boolean added = channels.putIfAbsent(channel.getId(), channel) == null;
		if (added) {
			channel.getCloseFuture().addListener(remover);
		}
 

	}

	public Channel get(Integer id) {
		return channels.get(id);
	}

	public Channel remove(Channel channel) {
		return channels.remove(channel.getId());
	}

	public Channel remove(Integer id) {
		return channels.remove(id);
	}

}
