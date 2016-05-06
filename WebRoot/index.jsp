<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>jsonTask</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">

<!-- extjs文件导入 -->
<script src="resources/extjs/ext-all.js"></script>
<script src="resources/extjs/bootstrap.js"></script>
<script src="resources/extjs/ext-locale-zh_CN.js"></script>
<link
	href="resources/extjs/ext-theme-crisp/build/resources/ext-theme-crisp-all.css"
	rel="stylesheet"></link>
<!-- extjs文件导入结束 -->
<script src="resources/js/jquery-1.7.1.js"></script>

<script type="text/javascript">
	Ext.onReady(function() {
		//对json内容进行格式化显示
        function formatJson(jsonStr){
        	var str = "";
    		var layer = 0;
    		var flag = true;
    		for(var i = 0 ; i < jsonStr.length; i ++){
    			var c = jsonStr.charAt(i);
    			if(c == '{' || c == '[' ){
    				str +=("<font color=\"green\">"+c + "</font><br />");
    				flag = true;
    				layer ++;
    			}else if (c == '}' ||  c == ']'){
    				str += ("<br />" );
    				layer --;
    				flag = false;
    				for(var j = 0 ; j < layer*2 ; j++){
    					str += ("&emsp;");
    				}
    				str += ("<font color=\"green\">"+c+ "</font>");
    				
    			}else if( c == ':' ){
    				flag = false;
    				str += ("<b style=\"color:Fuchsia\">"+c+"</b> ");
    			}else if(c == ','){
    				flag = true;
    				str += ("<b>"+c+"</b style=\"color:Fuchsia\"><br />");
    			}else{
    				flag = false;
    				str += (c);
    			}
    			for(var j = 0 ; j < layer*2  && flag ; j++){
    				str += ("&emsp;");
    			}
    		}
    		return str;
        }
		//检查输入内容是否为空
        function checkIsNull(object){
        	if(object.trim() == ""){
        		Ext.Msg.alert("提示","请先输入内容！");
        		return true;
        	}else{
        		return false;
        	}
        }
        
        //创建一个tab面板
		var tab = new Ext.TabPanel({
			renderTo : 'panel',
			width : '80%',
			height : '100%',
			id :'myPanel',
			enableTabScroll : true,
			activeTab : 0,
			//按钮
			items : [{
				title : "格式校验",
				html : '<div style=\"height:100%;width:100%;overflow:auto;background-image:url(resources/image/image1.jpg)\"><textarea placeholder=\"请输入需要校验的json字符串\"  id=\"tab1\" style=\"width:100%;resize:none;height:400px;background:transparent\"></textarea><p id=\"p1\" style="padding:10px"></p></div>',
				buttonAlign : 'center',
				buttons : [
						{
							text : '提交校验',
							handler : function() {
								if(checkIsNull($("#tab1").val())) 	return;
								ajaxCall("jsonParser",$("#tab1").val(),"validate",function(msg){
									if(msg 	=="true"){
										 $("#p1").html("<font color=\"green\">格式正确！</font>");
									}else{
										 $("#p1").html("<font color=\"red\">"+msg+"</font>");
									}
									
								});
							},
						}, {
							text : '清空内容',
							handler : function() {
								$("#tab1").val("");
								$("#p1").html("");
							}
						} ],
			},{
				title : "着色显示",
				html : '<div style=\"height:550px;width:100%;overflow:auto;background-image:url(resources/image/image5.jpg)\"><textarea placeholder=\"请输入json字符串\" id=\"tab2\" style=\"width:100%;resize:none;height:250px;background:transparent\"></textarea><div id="tab22" style="height:300px;width:100%;"></div></div>',
				buttonAlign : 'center',//居中
				buttons : [ {
					text : '提交内容',
					handler : function() {
						if(checkIsNull($("#tab2").val()))	return;
						ajaxCall("jsonParser",$("#tab2").val(),"display",function(msg){
							if(msg == "false"){
								Ext.Msg.alert("提示","请先确保json字符串格式的正确性！");
						//		formatJson($("#tab2"));
							}else{
								$("#tab22").html(formatJson(msg));
							}
						});
					}},{
					text : '清空内容',
					handler : function() {
						$("#tab2").val("");
						$("#tab22").html("");
					}
				} ]
			},{
				title : "压缩转义",
				html : '<div style=\"height:700px;width:100%;background-image:url(resources/image/image6.jpg);overflow:auto;\"><textarea placeholder=\"请输入json字符串\" id=\"tab3\" style=\"width:100%;height:300px;resize:none;background:transparent\;padding:10px"></textarea><textarea id="tab32" style=\";width:100%;resize:none;background:transparent;height :250px;padding:20px\"></textarea></div>',
				buttonAlign : 'center',//居中
				buttons : [
						{
							text : '压缩',
							handler : function() {
								ajaxCall("jsonParser",$("#tab3").val(),"compress",function(msg){
									if(msg == "false"){
										Ext.Msg.alert("提示","请先确保json字符串格式的正确性！");
									}else{
										$("#tab32").html(msg);
									}
								});
							}
						},
						{
							text : '转义',
							handler : function() {
								ajaxCall("jsonParser",$("#tab3").val(),"escape",function(msg){
									if(msg == "false"){
										Ext.Msg.alert("提示","请先确保json字符串格式的正确性！");
									}else{
										$("#tab32").html(msg);
									}
								});
							}
						},
						{	
							text : '压缩并转义',
							handler : function() {
								ajaxCall("jsonParser",$("#tab3").val(),"escapeAndCompress",function(msg){
									if(msg == "false"){
										Ext.Msg.alert("提示","请先确保json字符串格式的正确性！");
									}else{
										$("#tab32").html(msg);
									}
								});
							}
						},
						{
							text : '去除转义',
							handler : function() {
								ajaxCall("jsonParser",$("#tab3").val(),"removeEscape",function(msg){
									if(msg == "false"){
										Ext.Msg.alert("提示","请先确保json字符串格式的正确性！");
									}else{
										$("#tab32").html(msg);
									}
								});
							}
						},{
							text : '清空内容',
							handler : function(){
								$("#tab3").val("");
								$("#tab32").html("");
							}
						} ],
			},
			{
				title : "json-xml互转",
				html : "<div style=\"height:100%;width:100%;background-image:url(resources/image/image3.jpg)\"><textarea placeholder=\"请输入json字符串\" id=\"tab41\" style=\"margin:40 10 0 20 ;resize:none;width:430px;height:90%;background-image:url(resources/image/image31.jpg)\"></textarea><textarea placeholder=\"请输入xml字符串\" id=\"tab42\" style=\"margin:40 20 0 15 ;width:430;resize:none;height:90%;background-image:url(resources/image/image32.jpg)\"></textarea></div>",
				buttonAlign : 'center',//居中
				buttons : [ {
					text : 'json转xml',
					handler : function(){
						if(checkIsNull(	$("#tab41").val())) return;
						ajaxCall("jsonParser",$("#tab41").val(),"jsonToXml", function(msg){
								if(msg == "false"){
									Ext.Msg.alert("提示","请先确保json字符串格式的正确性！");
								}else{
									$("#tab42").val(msg);
								}
						});
					}
				},{
					text : '清空json',
					handler : function(){
							$("#tab41").val("");
					}
				} ,{
					text : '清空xml',
					handler : function(){
						$("#tab42").val("");
					}
				},{
					text : 'xml转json',
					handler : function(){
						if(checkIsNull(	$("#tab42").val())) return;
						ajaxCall("jsonParser",$("#tab42").val(),"xmlToJson", function(msg){
								if(msg == "false"){
									Ext.Msg.alert("提示","请先确保json字符串格式的正确性！");
								}else{
									$("#tab41").val(msg);
								}
						});
					}
				}  ],
			},
			{
				title : "在线编辑",
				html : '<div style=\"height:6000px;width:100%;background-image:url(resources/image/image4.jpg)\"><textarea placeholder=\"请输入json字符串\" id=\"tab51\" style=\"margin:40 10 0 20 ;resize:none;width:430px;height:500px;background-image:url(resources/image/image41.jpg)\"></textarea><div  id=\"tab52\" style=\"margin:40 20 0 15 ;width:430px;height:500px;float:right;border-color:yellow;border-style:groove ;background-image:url(resources/image/image42.jpg);overflow:auto\"></div></div>',
				buttonAlign : 'center',//居中
				buttons : [ {
					text : 'json生成树',
					handler : function(){
						if(checkIsNull(	$("#tab51").val())) return;
						ajaxCall("jsonParser",$("#tab51").val(),"jsonToTree", function(msg){
								if(msg == "false"){
									Ext.Msg.alert("提示","请先确保json字符串格式的正确性！");
								}else{
									$("#tab52").html(msg);
									$("#tab52 ul").each(function () {
										this.style.display = "none";
										/* var ulContent = $(element).html(); */
									});
									
									addclick();
									adddel();
									//添加添加按钮相关样式及其添加事件	
									$("span[class^='add']").each(function(){
										$(this).html("<img title=\"添加子节点\" src=\"resources/image/fileadd.gif\">");
										$(this).addClass("hand");
										$(this).css("position", "relative");
										$(this).css("top","3px");
									});
									$("span[class^='add']}").click(function(){
										$("<li class=\"normal\"><input class=\"key\" style=\"width:50px\"  value=\"key\"><b>:</b><input class=\"value\" style=\"width:50px;text-align:center\" value=\"value\"><span class=\"del \"></span></li>").insertBefore(this);
										adddel();
									});
									$("input").hover(function(){
										$(this).css("background-color","Coral");
									},function(){
										$(this).css("background","transparent");
									});
								}
						});
					}
				},{
					text : '清空内容',
					handler : function(){
						$("#tab51").val("");
						$("#tab52").html("");
					}
				} ,{
					text : '树生成json',
					handler : function(){
						if(checkIsNull(	$("#tab52").html())) return;
						var jsonStr = "";
						$("#tab52").children().each(function(){
							if($(this).hasClass("firstObject")){
								jsonStr = recursiveTreeToJsonObject(this,"");
							}else if($(this).hasClass("firstArray")){
								jsonStr = recursiveTreeToJsonArray(this,"");
							}
						});
						$("#tab51").val(jsonStr.substring(0,jsonStr.length - 1));
					}
				}],
			} ],
			});
		tab.doLayout();
	});
	//注意传入的是一个html节点对象，递归生成jsonObject对象
	function recursiveTreeToJsonObject(element,jsonStr){
		jsonStr +="{";
		$(element.children).each(function(){
			if(this.className == "normal"){
				jsonStr += "\""+($(this).children("input"))[0].value+"\":";
				var value = ($(this).children("input ")[1]).value;
				if(isNumOrBoolean){
					jsonStr += value + ",";
				}else{
					jsonStr += "\""+value+"\",";
				}
			}else if(this.className == "object"){
				jsonStr += "\""+($(this).children("input ")[0]).value+"\":" ;
				jsonStr = recursiveTreeToJsonObject($(this).children("ul")[0],jsonStr) ;
			}else if(this.className == "array"){
				jsonStr += "\""+$($(this).children("input ")[0]).val()+"\":" ;
				jsonStr = recursiveTreeToJsonArray($(this).children("ul")[0],jsonStr) ;
			}
		});
		//首尾操作，考虑到递归时应用，为兼容则添加该判断
		if(jsonStr.charAt(jsonStr.length - 1) == ","){
			jsonStr = jsonStr.substring(0,jsonStr.length - 1) + "},";//内容结束时删除最后多余的逗号并添加右括号
		}else{
			jsonStr +="}";
		}
		return jsonStr;
	}
	//递归生成jsonArray数组
	function recursiveTreeToJsonArray(element,jsonStr){
		jsonStr += "[";
		$(element.children).each(function(){
			if(this.className == "normal"){
				var value = $(this).children("input ")[1].value;
				if(isNumOrBoolean){
					jsonStr += value + ",";
				}else{
					jsonStr += "\""+value+"\",";
				}
			}else if(this.className == "object"){
				jsonStr = recursiveTreeToJsonObject($(this).children("ul")[0],jsonStr) ;
			}else if(this.className == "array"){
				jsonStr = recursiveTreeToJsonArray($(this).children("ul")[0],jsonStr) ;
			}
		});
		//首尾操作，考虑到递归时应用，为兼容则添加该判断
		if(jsonStr.charAt(jsonStr.length - 1) == ","){
			jsonStr = jsonStr.substring(0,jsonStr.length - 1) + "],";//内容结束时删除最后多余的逗号并添加右括号
		}else{
			jsonStr +="]";
		}
		return jsonStr;
	}
	//判断是否为数字/布尔值/空值
	function isNumOrBoolean(str){
		var re = new RegExp(/^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$/);
		if(str.match(re) != null || str == "true" || str == "false" || str  == "null"){
			return true;
		}else{
			return false;
		}
	}
	//添加点击事件
	function addclick(){
		$("span[class^=parentNode]").unbind('click').click(function(){
			 var ul = $(this).siblings("ul");
			 if(ul.css("display") == "none"){
				 $(this).html("[-]"+$(this).html().substring(3));
				 ul.show(300);
			 }else{
				 $(this).html("[+]"+$(this).html().substring(3));
				 ul.hide(300);
			 }
		});
	}
	//添加删除事件
	function adddel(){
		//添加删除按钮相关样式及其删除事件	
		$("span[class^='del']").each(function(){
			$(this).html("<img title=\"删除节点(包括所有子节点)\" class=\"delete\" src=\"resources/image/bt_del.gif\">        <img class=\"edit\"title=\"复制节点(包括所有子节点)\" src=\"resources/image/bt_edit.gif\">");
			$(this).addClass("hand");
			$(this).css("position", "relative");
			$(this).css("top","3px");
			$(this).children(".delete").click(function(){
				$(this).parent().parent()[0].remove();
			});
			$(this).children(".edit").click(function(){
				var str = $($(this).parent().parent()[0]).html();
				var clazz = $($(this).parent().parent()[0]).attr("class");
				$("<li class=\""+clazz+"\">"+str + "<li>").insertAfter($(this).parent().parent()[0]);
				adddel();
				addclick();
			});
		});
	}
	//发送异步请求
	function ajaxCall(url,value,type,fn){
		$.ajax({
			type:'POST',
			url: url,
			data : {
				jsonStr : value,
				type : type
			},
			success: fn
		});
	}
</script>
<style type="text/css">
.hand {
	cursor: pointer;
}
#tab42 ul, li {
	 list-style:none; 
	margin:0px;
}
/* #tab52 input{
	background:transparent;
	border:0;
} */
.add{
	display :block
}
</style>
</head>

<body
	style="background-image:url(resources/image/main.jpg);background-position:center; background-repeat:repeat-y">
	<div id="panel" style="position:relative;margin-left:200px">
		<div id="loadMask"
			style="position:absolute;width:160px;height:50px;margin-left:350;margin-top: 300"></div>
	</div>
</body>
</html>
