package fengfei.berain.test;

import java.util.List;

import fengfei.berain.client.BerainClient;
import fengfei.berain.client.BerainEntry;
import fengfei.berain.client.EventType;
import fengfei.berain.client.Wather;

public class Example {

	public static void main(String[] args) throws Exception {
		BerainClient client = new BerainClient("http://127.0.0.1:8021/",
				"admin", "passowrd");
		client.start();
		// client.login();
		client.addWatchable("/berain/w1", EventType.DataChanged, new Wather() {

			@Override
			public void call(BerainEntry data) {
				System.out.println("DataChanged:================ " + data);

			}
		});
		client.addWatchable("/berain/w1", EventType.ChildrenChanged,
				new Wather() {

					@Override
					public void call(BerainEntry data) {
						System.out.println("ChildrenChanged:================ "
								+ data);

					}
				});

		List<BerainEntry> s = client.nextChildren("/berain");
		System.out.println(s);
	}
}
