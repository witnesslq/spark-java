package com.zi.search.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 过滤器
 * 
 * @author 刘星
 *
 */
public class FilterUtilsBackup {
	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
	private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	private static final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符
	private static final String question_body = "question_body";// 特殊格式
	private static final String regEx_question_body = "question_body_html";// 特殊格式
	private static final String answer_analysis = "answer_analysis";// 特殊格式
	private static final String know_analysis = "know_analysis";// 特殊格式
	private static final String regex_symbol = "[{}:\"]"; // 特殊符号
	private static final String regex_nbsp = "&nbsp;";// 空格
	private static final String regex_line = "\\\\n";// 反斜杠

	/**
	 * TODO 该过滤中会产生大量的nnn和tttt问题还不明
	 * 1.过滤js 2.过滤css 3.过滤html 4.过滤空格回车 5.过滤特殊格式 6.过滤特殊字符
	 * 
	 * @param original
	 * @return
	 */
	public static String filter(String original) {
		if (null != original && !"".equals(original)) {
			Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			Matcher m_script = p_script.matcher(original);
			original = m_script.replaceAll(""); // 过滤script标签

			Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			Matcher m_style = p_style.matcher(original);
			original = m_style.replaceAll(""); // 过滤style标签

			Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			Matcher m_html = p_html.matcher(original);
			original = m_html.replaceAll(""); // 过滤html标签

			Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
			Matcher m_space = p_space.matcher(original);
			original = m_space.replaceAll(""); // 过滤空格回车标签

			original = original
					.replaceAll(regex_nbsp, "")
					.replaceAll(regex_line, "")
					.replaceAll(regEx_question_body, "")
					.replaceAll(question_body, "")
					.replaceAll(answer_analysis, "")
					.replaceAll(know_analysis, "")
					.replaceAll("null", "")
					.replaceAll("NULL", "")
					;// 过滤特殊格式

			Pattern p = Pattern.compile(regex_symbol);
			Matcher m_symbol = p.matcher(original);
			original = m_symbol.replaceAll(""); // 过滤特殊符号
			return original;
		} else {
			return "";
		}
	}

	public static void main(String[] args) {
		String str = "{\"question_body_html:\"<table><tbody><tr><td><div>珊瑚礁hello当所处环境恶化时，失去了共生藻类的珊瑚虫会因为死亡而导致珊瑚礁逐渐“白化”，失去其鲜艳的色彩，那里生物多样性也锐减。这体现了生态工程什么原理（　 　 ）<table name=  optionsTable   cellpadding=  0   cellspacing=  0   width=  100%  ><tr><td width=  100%  >A．系统的结构决定功能原理</td></tr><tr><td width=  100%  >B．整体性原理</td></tr><tr><td width=  100%  >C．系统整体性原理</td></tr><tr><td width=  100%  >D．协调与平衡原理</td></tr></table></div></td></tr></tbody></table><script type='text/javascript' defer='defer'>window.addEventListener('load',function(){var imgArr =document.getElementsByTagName('img');if(imgArr.length >= 1){for(var i= 0;i < imgArr.length ;i++){var img = imgArr[i];var w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;if(w<=0 || w=='NaN'){w=305;};if(img.width > w){img.setAttribute('width','100%');}img.setAttribute('max-width','100%');}} var tableArr = document.getElementsByTagName('TABLE');if(tableArr.length > 0){var tb = tableArr[0];               var text = tb.style.cssText + ' line-height:150%;-webkit-text-size-adjust:none;';tb.setAttribute('style',text);}    }) </script> ,  answer_analysis  :  <table style=  WORD-BREAK: break-all;   border=  0   cellspacing=  0   width=  100%  ><tr style=  margin-top: 2em  ><td><span style=  font-family: '应用字体 Regular', '应用字体';  >试题难度：</span><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/a83224b4a1a72da821ecd21e.png   /><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/a83224b4a1a72da821ecd21e.png   /><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/a83224b4a1a72da821ecd21e.png   /><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/5d588f0279a556191bf9e96e.png   /><img style=  height:1em   src=  http://efd-p.image.alimmdn.com/qb/10/5d588f0279a556191bf9e96e.png   /></td></tr><tr ><td style=  padding-top:1em;padding-bottom:1em;  ><p><span style=  font-family: '应用字体 Regular', '应用字体';  color:grey;  >解答：</span></p></td></tr> </table>试题分析：系统各组分之间要有适当比例关系，这样才能顺利完成能量、物质、信息的转换和流通，并且实现总体大于各部分之和的效果，为生态工程的系统整体性原理，题意体现的就是就是该原理，故C正确。</div><table style=  WORD-BREAK: break-all   border=  0   cellspacing=  0   width=  100%  ><tr><td style=  padding-top:1em  > <p><span style=  font-family: '应用字体 Regular', '应用字体'; color: grey;  >考点：</span></p></td></tr><tr><td style=  padding-top:1em  ><span style=  padding: 0.3em 1em 0.35em;line-height:2em; color:#000000;border-radius:1em     >生态工程的基本原理</span></td></tr><tr><td style=  padding-top:1em  ><span style=  padding: 0.3em 1em 0.35em;line-height:2em; color:#000000;border-radius:1em     >生态工程的实例和发展前景</span></td></tr></table><script type='text/javascript' defer='defer'>window.addEventListener('load',function(){var imgArr =document.getElementsByTagName('img');if(imgArr.length >= 1){for(var i= 0;i < imgArr.length ;i++){var img = imgArr[i];var w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;if(w<=0 || w=='NaN'){w=305;};if(img.width > w){img.setAttribute('width','100%');}img.setAttribute('max-width','100%');}} var tableArr = document.getElementsByTagName('TABLE');if(tableArr.length > 0){var tb = tableArr[0];               var text = tb.style.cssText + ' line-height:150%;-webkit-text-size-adjust:none;';tb.setAttribute('style',text);}    }) </script> ,  question_body :   , know_analysis : { 99100477 :  生态工程的基本原理 , 99100478 :  生态工程的实例和发展前景 }}";
		// String str = "定义新运算．a\\otimes b=a ^{2} -b，如3\\otimes （-2）=3 ^{2}
		// --2=9-2=7，计算下列各式． （1）（-2）\\otimes 3      （2）5\\otimes （-4）
		// （3）（-3）\\otimes （-1）．";
		System.out.println(filter(str));
	}
}
