<#ftl strip_whitespace=true>
<#--
 * 门户层通用的模板都定义在此宏里面，供所有模板ftl文件使用。
 * 该模块会自动往所有模块文件里 import 进去。
 * ${contextPath} 为 应用上下文根性目录:如 /xxx
-->
<#global contextPath>${request.getContextPath()}</#global>
<#-- 往表单插入令牌 -->
<#macro csrf_token><input type="hidden" name="_al_ec_token" id="_al_ec_token" value="${Request['_al_ec_token']}"/></#macro>
<#--
* 令牌JS要放在JS头最顶处.
*-->
<#macro csrf_token_js>
	<script type="text/javascript">
	<#-- 设置全局contextPath	-->
	var contextPath = "${request.getContextPath()}";
	<!--
		function getToken(){
			var token="${Request['_al_ec_token']}";
			return token;
		}
	 //-->
	 </script>
</#macro>
<#-- 列表转字符串，并分隔 -->
<#function list2str4split pList key c>
	<#assign s=''>
	<#list pList as p>
		<#if s!=''><#assign s = s+c+p[key]><#else><#assign s = p[key]></#if>
	</#list>
	<#return s>
</#function>

<#-- 分页组件 -->
<#-- 分页组件simplePagination pageNo-当前页码  totalPages-总页数  pageBlockNum-显示页码个数  callBackFunc-回调函数执行再查询操作，包括一个入参：当着页码  -->
<#macro simplePagination pageNo totalPages pageBlockNum callBackFunc>
<#if (pageModel?exists && pageBlockNum?exists && callBackFunc?exists)>
<div class="paging" id="ec-pagination" callBack="${callBackFunc}">
	<#if (pageNo<=1)>
		<label><a class="btn-prev"><@spring.message "page.previous" /></a></label>
	</#if>
	<#if (pageNo>1)>
		<label><span id="ec-page-prevs" class="pageUpOrange" page="${pageNo-1}"><@spring.message "page.previous" /></span></label>
	</#if>
	<#if (pageBlockNum>0)>
		<label>	
		<#assign j=((pageNo-1)/pageBlockNum)?int />
		<#if (totalPages<=pageBlockNum)>
			<#assign k = totalPages>
		<#else>
			<#if (( j * pageBlockNum+pageBlockNum)<= totalPages) >
				<#assign k = pageBlockNum>
			<#else>
				<#assign k = totalPages- j*pageBlockNum>
			</#if>	
		</#if>
		<#list 1..k as i>
			<#if (pageBlockNum*j+i)==pageNo>
				<a class="pagingSelect" href="javascript:void(0);">${pageBlockNum*j+i}</a>
			<#else>
				<a id="ec-page-${pageBlockNum*j+i}" class="fontBlueB" href="#loc-page" page="${pageBlockNum*j+i}">${pageBlockNum*j+i}</a>
			</#if>
		</#list>
		</label>
	</#if>
	<#if (pageNo>=totalPages)>
		<label><span class="nextPageGray"><@spring.message "page.next" /></span></label>
	</#if>
	<#if (pageNo<totalPages)>
		<label><span id="ec-page-next" class="nextPageGrayOrange" page="${pageNo+1}"><@spring.message "page.next" /></span></label>
	</#if>
	<label class="marginTop4" id="ec-total-page" page="${totalPages}"><@spring.message "page.total" /> ${totalPages} <@spring.message "page.totalpage" /></label>
	<label class="marginTop4"><@spring.message "page.pageto" /></label>	<input id="ec-input-spec" type="text" class="inputW20H20" /><label class="marginTop4"><@spring.message "page.pagetopage" /></label>
	<a id="ec-btn-jump" href="#loc-page" class="determineBtn"><@spring.message "page.go" /></a>
</div>
<#else>
	<iuput type="hidden" value="simplePagination error pageNo or totalPages or pageBlockNum or callBackFunc is undefined"/>
</#if>
</#macro>
<#-- 分页组件modelPagination pageModel-分页封装对象对应类：PageModel使用  pageBlockNum-显示页码个数  callBackFunc-回调函数执行再查询操作，包括一个入参：当着页码  -->
<#macro modelPagination pageModel pageBlockNum callBackFunc pageTitle>
<#if (pageModel?exists && pageBlockNum?exists && callBackFunc?exists)>
<div class="ui-pagination" id="ec-pagination-${pageTitle}" callBack="${callBackFunc}" pageTitle="${pageTitle}">
	<#if (pageModel.pageNo<=1)>
		<label><a class="btn-prev"><@spring.message "page.previous" /></a></label>
	</#if>
	<#if (pageModel.pageNo>1)>
		<label><a id="ec-page-prevs" pageTitle="${pageTitle}" class="btn-prev" page="${pageModel.pageNo-1}"><@spring.message "page.previous" /></a></label>
	</#if>
	<#if (pageBlockNum>0)>
		<label>	
		<#assign j=((pageModel.pageNo-1)/pageBlockNum)?int />
		<#if (pageModel.totalPages<=pageBlockNum)>
			<#assign k = pageModel.totalPages>
		<#else>
			<#if (( j * pageBlockNum+pageBlockNum)<= pageModel.totalPages) >
				<#assign k = pageBlockNum>
			<#else>
				<#assign k = pageModel.totalPages- j*pageBlockNum>
			</#if>	
		</#if>
		<#list 1..k as i>
			<#if (pageBlockNum*j+i)==pageModel.pageNo>
				<a class="cur" href="javascript:void(0);">${pageBlockNum*j+i}</a>
			<#else>
				<a id="ec-page-${pageBlockNum*j+i}" pageTitle="${pageTitle}" class="" href="#loc-page" page="${pageBlockNum*j+i}">${pageBlockNum*j+i}</a>
			</#if>
		</#list>
		</label>
	</#if>
	<#if (pageModel.pageNo>=pageModel.totalPages)>
		<label><a class="btn-next"><@spring.message "page.next" /></a></label>
	</#if>
	<#if (pageModel.pageNo<pageModel.totalPages)>
		<label><a id="ec-page-next" pageTitle="${pageTitle}" class="btn-next" page="${pageModel.pageNo+1}"><@spring.message "page.next" /></a></label>
	</#if>
	<label class="" id="ec-total-page-${pageTitle}" pageTitle="${pageTitle}" page="${pageModel.totalPages}"><@spring.message "page.total" /> ${pageModel.totalPages} <@spring.message "page.totalpage" /></label>
	<label class=""><@spring.message "page.pageto" /></label>	<input id="ec-input-spec-${pageTitle}" pageTitle="${pageTitle}" type="text" style="width:30px;" class="" /><label class="marginTop4"><@spring.message "page.pagetopage" /></label>
	<a id="ec-btn-jump-${pageTitle}" pageTitle="${pageTitle}" href="#loc-page" class=""><@spring.message "page.go" /></a>
</div>
<#else>
	<iuput type="hidden" value="simplePagination error pageModel or pageBlockNum or callBackFunc is undefined"/>
</#if>
</#macro>
<#-- 当前位置导航功能 begin -->
<#macro NavBar curMenuNo>
<#if curMenuNo?exists>
	 <div id="ec-navbar" class="position" curMenuNo="${curMenuNo}"></div>  
</#if>
</#macro>
<#-- 当前位置导航功能  end -->