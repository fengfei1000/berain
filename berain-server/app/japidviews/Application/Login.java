package japidviews.Application;

import java.util.*;
import java.io.*;
import cn.bran.japid.tags.Each;
import static play.templates.JavaExtensions.*;
import static cn.bran.play.JapidPlayAdapter.*;
import static play.data.validation.Validation.*;
import play.data.validation.Validation;
import play.mvc.Scope.*;
import models.*;
import play.data.validation.Error;
import controllers.*;
import play.mvc.Http.*;
//
// NOTE: This file was generated from: japidviews/Application/Login.html
// Change to this file will be lost next time the template file is compiled.
//

public class Login extends japidviews._layouts.Layout
{
	public static final String sourceTemplate = "japidviews/Application/Login.html";
	{
		putHeader("Content-Type", "text/html; charset=utf-8");
		setContentType("text/html; charset=utf-8");
	}

	// - add implicit fields with Play

	final Request request = Request.current();
	final Response response = Response.current();
	final Session session = Session.current();
	final RenderArgs renderArgs = RenderArgs.current();
	final Params params = Params.current();
	final Validation validation = Validation.current();
	final cn.bran.play.FieldErrors errors = new cn.bran.play.FieldErrors(
			validation);
	final play.Play _play = new play.Play();

	// - end of implicit fields with Play 

	public Login() {
		super(null);
	}

	public Login(StringBuilder out) {
		super(out);
	}

	/* based on https://github.com/branaway/Japid/issues/12
	 */
	public static final String[] argNames = new String[] {/* args of the template*/};
	public static final String[] argTypes = new String[] {/* arg types of the template*/};
	public static final Object[] argDefaults = new Object[] {};
	public static java.lang.reflect.Method renderMethod = getRenderMethod(japidviews.Application.Login.class);

	{
		setRenderMethod(renderMethod);
		setArgNames(argNames);
		setArgTypes(argTypes);
		setArgDefaults(argDefaults);
		setSourceTemplate(sourceTemplate);
	}

	////// end of named args stuff

	public cn.bran.japid.template.RenderResult render() {
		long t = -1;
		try {
			super.layout();
		} catch (RuntimeException e) {
			super.handleException(e);
		}
		return new cn.bran.japid.template.RenderResultPartial(getHeaders(),
				getOut(), t, actionRunners, sourceTemplate);
	}

	@Override
	protected void doLayout() {
		beginDoLayout(sourceTemplate);
		//------
		p("<div class=\"container\">\n" +
				"	<div class=\" control-group error\">\n" +
				"		<span class=\"help-inline\">");// line 1
		p(flash.get("msg"));// line 4
		p("</span>\n" +
				"	</div>\n" +
				"\n" +
				"\n" +
				"	<form action=\"/login\" id=\"form2\" method=\"post\">\n" +
				"\n" +
				"		<p class=\"control-group ");// line 4
		p(flash("error"));// line 10
		p("\">\n"
				+
				"			<label for=\"username\">User Name</label> <input id=\"username\" name=\"username\" type=\"text\" value=\"");// line 10
		p(flash(" username"));// line 11
		p("\" placeholder=\"User Name\" />\n" +
				"		</p>\n" +
				"		<p class=\"control-group ");// line 11
		p(flash("error"));// line 13
		p("\">\n"
				+
				"			<label for=\"password\">Password</label> <input id=\"password\" name=\"password\" type=\"text\" placeholder=\"Password\" />\n"
				+
				"		</p>\n"
				+
				"		<p class=\"control-group \">\n"
				+
				"			<label for=\"remember\"><input id=\"remember\" name=\"remember\" type=\"checkbox\" value=\"1\" placeholder=\"Remember\" />&nbsp;&nbsp; Is remember?</label> \n"
				+
				"		</p>\n" +
				"		<p>\n" +
				"			<input type=\"submit\" value=\"Login\" class=\"btn\" />\n" +
				"		</p>\n" +
				"	</form>\n" +
				"</div>\n");// line 13

		endDoLayout(sourceTemplate);
	}

	@Override
	protected void title() {
		p("Login Page");
		;
	}
}
