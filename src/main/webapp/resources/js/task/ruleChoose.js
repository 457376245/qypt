var ruleChoose={};

var chooseArrsy=[];

layui.use(['form'],function(){
	
	var form = layui.form;
	
	form.on('checkbox(ruleCode)', function(data){
		/*  console.log(data.elem); //得到checkbox原始DOM对象
		  console.log(data.elem.checked); //是否被选中，true或者false
		  console.log(data.value); //复选框value值，也可以通过data.elem.value得到
		  console.log(data.othis); //得到美化后的DOM对象
		 */		  
		var isChecked=data.elem.checked;
		
		if(isChecked){
			chooseArrsy.push(data.value);
		}else{
			var isIn=jQuery.inArray(data.value,chooseArrsy);
			
			if(isIn!=-1){
				chooseArrsy.splice(jQuery.inArray(data.value,chooseArrsy),1);
			}
		}
	});  
	
	ruleChoose.saveInfo=function(){
		window.parent.ruleMode.chooseRuleBack(chooseArrsy);
	}
	
});

