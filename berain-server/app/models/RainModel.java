package models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

import fengfei.berain.server.HttpClientContainer;
import fengfei.berain.server.EventType;
import fengfei.berain.server.Focus;
import fengfei.berain.server.WatchedEvent;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.GenericModel;
import play.db.jpa.JPABase;

@Entity
@Table(name = "rain_config", catalog = "", schema = "")
public class RainModel extends GenericModel {

	@Id
	@Column(name = "id", length = 32, nullable = false)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String id;

	@Column(nullable = false, updatable = false)
	@Index(name = "pid_index")
	public String pid = "0";
	@Required
	@MaxSize(value = 255, message = "key.maxsize")
	@Index(name = "key_index")
	@Column(name = "skey", length = 255, nullable = false)
	public String key;
	@Required
	@MaxSize(value = 767, message = "path.maxsize")
	@Index(name = "path_index")
	@Column(name = "path", length = 767, nullable = false, unique = true)
	public String path;

	@MaxSize(value = 255, message = "value.maxsize")
	@Column(name = "svalue", length = 255, nullable = false)
	public String value;
	@Column(name = "smd5", length = 32, nullable = true)
	public String md5;

	@Column(
			name = "create_at",
			nullable = false,
			columnDefinition = " datetime NOT NULL DEFAULT '2012-08-28 14:00:00'")
	public Date createAt;
	@Column(
			name = "update_at",
			nullable = false,
			columnDefinition = " datetime NOT NULL DEFAULT '2012-08-28 14:00:00'")
	public Date updateAt;

	@Column(nullable = false)
	public int leaf = 0;

	// @ManyToOne
	// @JoinColumn(name = "pid")
	// private RainModel parent;
	//
	// @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL },
	// mappedBy = "pid")
	// @OrderBy("id")
	// // @JoinColumn(name="pid",referencedColumnName = "id", nullable =
	// // false,insertable=false,updatable=false)
	// private List<RainModel> children = new ArrayList<RainModel>();

	//
	// public RainModel getParent() {
	// return parent;
	// }
	//
	// public void setParent(RainModel parent) {
	// this.parent = parent;
	// }
	//
	// public List<RainModel> getChildren() {
	// return children;
	// }
	//
	// public void setChildren(List<RainModel> children) {
	// this.children = children;
	// }

	public int getLeaf() {
		return leaf;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setLeaf(int leaf) {
		this.leaf = leaf;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	@Override
	public String toString() {
		return "RainModel [id=" + id + ", pid=" + pid + ", key=" + key + ", path=" + path + ", value=" + value + ", md5=" + md5 + ", createAt=" + createAt + ", updateAt=" + updateAt + ", leaf=" + leaf + "]";
	}

	@Override
	public <T extends JPABase> T save() {
		T t = super.save();
		container.addWatchedEvent(new WatchedEvent(EventType.DataChanged, path));
		container.addWatchedEvent(new WatchedEvent(EventType.ChildrenChanged, Focus
				.getParent(path)));
		return t;
	}

	@Override
	public <T extends JPABase> T merge() {
		T t = super.merge();
		container.addWatchedEvent(new WatchedEvent(EventType.DataChanged, path));
		container.addWatchedEvent(new WatchedEvent(EventType.ChildrenChanged, Focus
				.getParent(path)));
		return t;
	}

	@Override
	public boolean create() {
		boolean isUpdated = super.create();
		container.addWatchedEvent(new WatchedEvent(EventType.DataChanged, path));
		container.addWatchedEvent(new WatchedEvent(EventType.ChildrenChanged, Focus
				.getParent(path)));
		return isUpdated;
	}

	@Override
	public <T extends JPABase> T delete() {
		T t = super.delete();
		container.addWatchedEvent(new WatchedEvent(EventType.Deleted, path));
		container.addWatchedEvent(new WatchedEvent(EventType.DataChanged, path));
		container.addWatchedEvent(new WatchedEvent(EventType.ChildrenChanged, Focus
				.getParent(path)));
		return t;
	}

	@Override
	public <T extends JPABase> T refresh() {

		T t = super.refresh();
		container.addWatchedEvent(new WatchedEvent(EventType.DataChanged, path));
		container.addWatchedEvent(new WatchedEvent(EventType.ChildrenChanged, Focus
				.getParent(path)));
		return t;
	}

	private static HttpClientContainer container = HttpClientContainer.get();
}
