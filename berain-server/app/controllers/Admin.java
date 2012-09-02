package controllers;

import japidviews.Application.Login;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;

import play.Play;
import play.mvc.Before;
import play.mvc.Http;
import cn.bran.play.JapidController;
import cn.bran.play.JapidResult;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;

public class Admin extends JapidController {

	public final static String SESSION_USER_KEY = "user";
	public final static String COOKIE_USERNAME = "berain_user";
	public final static String COOKIE_PASSWORD = "berain_pwd";

	@Before(unless = { "login", "logout", "logon", "logoff" })
	static void checkAuthentification() {
		//
		if (session.get(SESSION_USER_KEY) == null) {
			// loginIndex();
			// System.out.println("-----------------login1-----------------");
			throw new JapidResult(new Login().render());
		}

	}

	public static void logout() {
		session.clear();
		response.removeCookie(COOKIE_USERNAME);
		response.removeCookie(COOKIE_PASSWORD);
		throw new JapidResult(new Login().render());
	}

	public static void login() {
		String u = params.get("username");
		String p = params.get("password");
		String rem = params.get("remember");
		boolean isLogin = verify(u, p, rem);
		if (isLogin) {
			Application.index();
		} else if (u == null || p == null) {
			throw new JapidResult(new Login().render());
		} else {

			flash.put("msg", "User And Password mismatch.");
			flash.put("error", "error");
			flash.put("username", u);

			throw new JapidResult(new Login().render());
		}

	}

	public static void logon() {
		boolean isLogin;
		try {
			String u = params.get("username");
			String p = params.get("password");
			String rem = params.get("remember");
			isLogin = verify(u, p, rem);
			renderText(isLogin);
		} catch (Exception e) {
			e.printStackTrace();
			renderText(false);
		}

	}

	public static void logoff() {
		try {
			session.clear();
			response.removeCookie(COOKIE_USERNAME);
			response.removeCookie(COOKIE_PASSWORD);
			renderText(true);
		} catch (Exception e) {
			renderText(false);
			e.printStackTrace();
		}
	}

	private static boolean verify(String username, String passowrd, String remember) {
		Properties properties = Play.configuration;
		String u = username;
		String p = passowrd;
		String user = properties.getProperty("admin.user");
		String pwd = properties.getProperty("admin.password");

		if (u == null) {
			Http.Cookie cookie = request.cookies.get(COOKIE_USERNAME);
			u = cookie == null ? u : cookie.value;
		}
		if (p == null) {
			Http.Cookie cookie = request.cookies.get(COOKIE_PASSWORD);
			p = cookie == null ? p : cookie.value;
		}
		if ((user.equals(u) && pwd.equals(p)) || session.get(SESSION_USER_KEY) != null) {
			session.put(SESSION_USER_KEY, true);
			if (remember != null && "1".equals(remember)) {
				response.setCookie(COOKIE_USERNAME, u, "30d");
				response.setCookie(COOKIE_PASSWORD, p, "30d");
			}
			return true;
		} else {
			return false;
		}

	}
}
