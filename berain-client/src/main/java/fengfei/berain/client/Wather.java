package fengfei.berain.client;

public abstract class Wather implements Runnable {

	private BerainEntry data;

	@Override
	public void run() {
		call(data);
	}

	public void setData(BerainEntry data) {
		this.data = data;
	}

	public abstract void call(BerainEntry data);

}
