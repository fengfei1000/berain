package japidviews._layouts;
import java.util.*;
import java.io.*;
import cn.bran.japid.tags.Each;
import controllers.Admin;
import static play.templates.JavaExtensions.*;
import static cn.bran.play.JapidPlayAdapter.*;
import static play.data.validation.Validation.*;
import japidviews._layouts.*;
import play.i18n.Messages;
import play.data.validation.Validation;
import play.mvc.Scope.*;
import models.*;
import play.data.validation.Error;
import play.i18n.Lang;
import play.mvc.Http.*;
import controllers.*;
//
// NOTE: This file was generated from: japidviews/_layouts/Layout.html
// Change to this file will be lost next time the template file is compiled.
//
@cn.bran.play.NoEnhance
public abstract class Layout extends cn.bran.play.JapidTemplateBase
{
	public static final String sourceTemplate = "japidviews/_layouts/Layout.html";
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
	final cn.bran.play.FieldErrors errors = new cn.bran.play.FieldErrors(validation);
	final play.Play _play = new play.Play(); 

// - end of implicit fields with Play 


	public Layout() {
		super(null);
	}
	public Layout(StringBuilder out) {
		super(out);
	}
	@Override public void layout() {
		beginDoLayout(sourceTemplate);		p("<!DOCTYPE html>\n" + 
"<html lang=\"en\">\n" + 
"<head>\n" + 
"<meta charset=\"utf-8\">\n" + 
"<title>");// line 1
		title();p("</title> ");// line 5
		p("\n" + 
"<link rel=\"stylesheet\" media=\"screen\" href=\"");// line 5
		p(lookupStatic("/public/bootstrap/css/bootstrap.min.css"));// line 6
		p("\">\n" + 
"<style type=\"text/css\">\n" + 
"body {\n" + 
"	padding-top: 70px;\n" + 
"}\n" + 
"</style>\n" + 
"\n" + 
"<link rel=\"stylesheet\" media=\"screen\" href=\"");// line 6
		p(lookupStatic("/public/bootstrap/css/bootstrap-responsive.min.css"));// line 13
		p("\">\n" + 
"<link rel=\"stylesheet\" media=\"screen\" href=\"");// line 13
		p(lookupStatic("/public/css/ui-darkness/jquery-ui-1.8.23.custom.css"));// line 14
		p("\">\n" + 
"<link rel=\"stylesheet\" media=\"screen\" href=\"");// line 14
		p(lookupStatic("/public/css/jquery.gritter.css"));// line 15
		p("\">\n" + 
"<script src=\"");// line 15
		p(lookupStatic("/public/js/jquery-1.8.0.js"));// line 16
		p("\" type=\"text/javascript\"></script>\n" + 
"<script src=\"");// line 16
		p(lookupStatic("/public/bootstrap/js/bootstrap.min.js"));// line 17
		p("\" type=\"text/javascript\"></script>\n" + 
"<script src=\"");// line 17
		p(lookupStatic("/public/js/jquery-ui-1.8.23.custom.min.js"));// line 18
		p("\" type=\"text/javascript\"></script>\n" + 
"<script src=\"");// line 18
		p(lookupStatic("/public/js/jquery.hotkeys.js"));// line 19
		p("\" type=\"text/javascript\"></script>\n" + 
"<script src=\"");// line 19
		p(lookupStatic("/public/js/jquery.gritter.js"));// line 20
		p("\" type=\"text/javascript\"></script>\n" + 
"<script src=\"");// line 20
		p(lookupStatic("/public/js/jquery.cookie.js"));// line 21
		p("\" type=\"text/javascript\"></script>\n" + 
"\n" + 
"<script src=\"");// line 21
		p(lookupStatic("/public/jstree/jquery.jstree.js"));// line 23
		p("\" type=\"text/javascript\"></script>\n" + 
"<style type=\"text/css\">\n" + 
".footer {\n" + 
"	padding: 10px 0;\n" + 
"	margin-top: 10px;\n" + 
"	border-top: 1px solid #e5e5e5;\n" + 
"	background-color: #f5f5f5;\n" + 
"	width: auto;\n" + 
"	z-index: 1000;\n" + 
"}\n" + 
"\n" + 
".footer p {\n" + 
"	margin-bottom: 0;\n" + 
"	color: #777;\n" + 
"}\n" + 
"\n" + 
".footer-links {\n" + 
"	margin: 10px 0;\n" + 
"}\n" + 
"\n" + 
".footer-links li {\n" + 
"	display: inline;\n" + 
"	margin-right: 10px;\n" + 
"}\n" + 
"\n" + 
".containerh {\n" + 
"	height: 700px;\n" + 
"}\n" + 
"\n" + 
".treecontainer {\n" + 
"	overflow-y: auto;\n" + 
"	height: 500px;\n" + 
"}\n" + 
"</style>\n" + 
"<!--[if lt IE 9]>\n" + 
"      <script src=\"");// line 23
		p(lookupStatic("/public/js/html5shiv.js"));// line 58
		p("\"></script>\n" + 
"<![endif]-->\n" + 
"\n");// line 58
		css();p("\n" + 
"</head>\n" + 
"\n" + 
"<body>\n" + 
"\n" + 
"	<div class=\"navbar navbar-inverse navbar-fixed-top\">\n" + 
"		<div class=\"navbar-inner\">\n" + 
"			<div class=\"container-fluid\">\n" + 
"				<a class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\"> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span>\n" + 
"				</a> <a class=\"brand\" href=\"#\">Berain </a>\n" + 
"				<div class=\"nav-collapse collapse\">\n" + 
"					<div class=\"navbar-text pull-right\">\n" + 
"						");// line 61
		if (session.get(Admin.SESSION_USER_KEY) != null) {// line 73
		p("\n" + 
"						<form class=\"navbar-form\" action=\"/logout\" id=\"logoutForm\">\n" + 
"							<a href=\"#\" class=\"navbar-link\" id=\"logout\">Logout</a>\n" + 
"						</form>\n" + 
"						");// line 73
		}// line 77
		p("\n" + 
"					</div>\n" + 
"\n" + 
"				</div>\n" + 
"\n" + 
"			</div>\n" + 
"		</div>\n" + 
"	</div>\n" + 
"\n" + 
"	<div class=\"container-fluid containerh\">");// line 77
		doLayout();// line 86
		p("</div>\n" + 
"\n" + 
"	<div id=\"dialog\"></div>\n" + 
"\n" + 
"\n" + 
"\n" + 
"\n" + 
"	<footer class=\"footer\">\n" + 
"		<div class=\"container\">\n" + 
"			<p class=\"pull-right\">\n" + 
"				<a href=\"#\">Back to top</a>\n" + 
"			</p>\n" + 
"			<h5>\n" + 
"				&copy; Copyright 2012 by FengFei <a href=\"https://github.com/fengfei1000\" target=\"_blank\">Github</a>\n" + 
"			</h5>\n" + 
"			<h5>\n" + 
"				Licensed under the <a href=\"http://www.apache.org/licenses/LICENSE-2.0\" target=\"_blank\">Apache License, Version 2.0.</a>\n" + 
"			</h5>\n" + 
"\n" + 
"		</div>\n" + 
"	</footer>\n" + 
"\n" + 
"\n" + 
"</body>\n" + 
"</html>\n");// line 86
				endDoLayout(sourceTemplate);	}
	 protected void title() {};
	 protected void css() {};

	protected abstract void doLayout();
}