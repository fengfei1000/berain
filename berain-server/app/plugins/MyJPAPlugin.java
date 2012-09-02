package plugins;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.db.DB;
import play.db.jpa.JPAPlugin;

public class MyJPAPlugin extends JPAPlugin {

	@Override
	public void onApplicationStart() {
		final String dataSource = Play.configuration
				.getProperty("hibernate.connection.datasource");
		if (!StringUtils.isEmpty(dataSource) || DB.datasource != null) {
			super.onApplicationStart();
		}
	}

}
