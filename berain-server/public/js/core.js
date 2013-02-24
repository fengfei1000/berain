/**
 * 增加一些字符功能
 * 
 * @type String
 */

String.prototype = {
	toggle : function(value, other) {
		return this == value ? other : value;
	},
	test : function(regex, params) {
		return ((typeof regex == 'string') ? new RegExp(regex, params) : regex)
				.test(this);
	},

	contains : function(string, separator) {
		return (separator) ? (separator + this + separator).indexOf(separator
				+ string + separator) > -1 : this.indexOf(string) > -1;
	},

	trim : function() {
		return this.replace(/^\s+|\s+$/g, '');
	},

	clean : function() {
		return this.replace(/\s+/g, ' ').trim();
	},

	camelCase : function() {
		return this.replace(/-\D/g, function(match) {
			return match.charAt(1).toUpperCase();
		});
	},

	hyphenate : function() {
		return this.replace(/[A-Z]/g, function(match) {
			return ('-' + match.charAt(0).toLowerCase());
		});
	},

	capitalize : function() {
		return this.replace(/\b[a-z]/g, function(match) {
			return match.toUpperCase();
		});
	},

	escapeRegExp : function() {
		return this.replace(/([-.*+?^${}()|[\]\/\\])/g, '\\$1');
	},

	toInt : function(base) {
		return parseInt(this, base || 10);
	},

	toFloat : function() {
		return parseFloat(this);
	},

	hexToRgb : function(array) {
		var hex = this.match(/^#?(\w{1,2})(\w{1,2})(\w{1,2})$/);
		return (hex) ? hex.slice(1).hexToRgb(array) : null;
	},

	rgbToHex : function(array) {
		var rgb = this.match(/\d{1,3}/g);
		return (rgb) ? rgb.rgbToHex(array) : null;
	},

	stripScripts : function(option) {
		var scripts = '';
		var text = this.replace(/<script[^>]*>([\s\S]*?)<\/script>/gi,
				function() {
					scripts += arguments[1] + '\n';
					return '';
				});
		if (option === true)
			$exec(scripts);
		else if ($type(option) == 'function')
			option(scripts, text);
		return text;
	},

	substitute : function(object, regexp) {
		return this.replace(regexp || (/\\?\{([^}]+)\}/g),
				function(match, name) {
					if (match.charAt(0) == '\\')
						return match.slice(1);
					return (object[name] != undefined) ? object[name] : '';
				});
	},

	// 批量替换，比如：str.ReplaceAll([/a/g,/b/g,/c/g],["aaa","bbb","ccc"])
	replaceAll : function(A, B) {
		var C = this;
		for ( var i = 0; i < A.length; i++) {
			C = C.replace(A[i], B[i]);
		}
		;
		return C;
	},

	// 去掉字符左边的空白字符
	leftTrim : function() {
		return this.replace(/^[\t\n\r]/g, '');
	},

	// 去掉字符右边的空白字符
	rightTrim : function() {
		return this.replace(/[\t\n\r]*$/g, '');
	},

	// 返回字符的长度，一个中文算2个
	chineseLength : function() {
		return this.replace(/[^\x00-\xff]/g, "**").length;
	},

	// 判断字符串是否以指定的字符串结束
	endsWith : function(A, B) {
		var C = this.length;
		var D = A.length;
		if (D > C)
			return false;
		if (B) {
			var E = new RegExp(A + '$', 'i');
			return E.test(this);
		} else
			return (D == 0 || this.substr(C - D, D) == A);
	},
	// 判断字符串是否以指定的字符串开始
	startsWith : function(str) {
		return this.substr(0, str.length) == str;
	},
	// 字符串从哪开始多长字符去掉
	remove : function(A, B) {
		var s = '';
		if (A > 0)
			s = this.substring(0, A);
		if (A + B < this.length)
			s += this.substring(A + B, this.length);
		return s;
	}
}

Number.prototype = {

	limit : function(min, max) {
		return Math.min(max, Math.max(min, this));
	},

	round : function(precision) {
		precision = Math.pow(10, precision || 0);
		return Math.round(this * precision) / precision;
	},

	times : function(fn, bind) {
		for ( var i = 0; i < this; i++)
			fn.call(bind, i, this);
	},

	toFloat : function() {
		return parseFloat(this);
	},

	toInt : function(base) {
		return parseInt(this, base || 10);
	}

};
