package fengfei.berain.server;

public class WatchedEvent {

	final private EventType eventType;
	private String path;

	public WatchedEvent(EventType eventType, String path) {

		this.eventType = eventType;
		this.path = path;
	}

	public WatchedEvent(int eventType, String path) {
		this.eventType = EventType.fromInt(eventType);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public EventType getEventType() {
		return eventType;
	}

	@Override
	public String toString() {
		return "WatchedEvent state:" + eventType + " path:" + path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WatchedEvent other = (WatchedEvent) obj;
		if (eventType != other.eventType)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}
