﻿/**
path:路径
where：分页处理，跳转的形式
*/
function  gotopage(path,where){
       var page=document.Form2.pageNO; //当前页
       
       if(where=="next"){ //下一页
     
          page.value=document.Form2.nextpageNO.value; 
      
       }else if(where=="prev"){ //上一页
     
          page.value=document.Form2.prevpageNO.value;
       }else if(where=="end"){ //最后一页
     
          page.value=document.Form2.sumPage.value;
       }else if(where=="start"){ //首页
          page.value=1;
       }else if(where=="go"){ //跳转到第几页
         var theForm = document.Form2;   
         if(Trim(theForm.goPage.value)=="")
	     {
		     alert("请输入页数"); 
		     theForm.goPage.focus();   
		     return false;
	     }
	     if(!checkNumber(theForm.goPage))
	     {
		     alert("请输入正确页数(数字)"); 
		     theForm.goPage.focus();     
		     return false;
	     }
	     
	     var objgo=parseInt(theForm.goPage.value);
	     var objsum=parseInt(theForm.sumPage.value);
	     if(objgo<=0||objgo>objsum){
	         alert("不存在此页，请重新输入页数"); 
		     theForm.goPage.focus();     
		     return false; 
	     }
         
         page.value=theForm.goPage.value;                
      } 
     
      document.Form1.pageNO.value=document.Form2.pageNO.value;     //表示当前页，这个参数也就是整个分页核心参数
      //alert(document.Form1.pageNO.value);
      document.Form1.pageSize.value=document.Form2.pageSize.value; //
      
      Pub.submitActionWithForm('Form2',path,'Form1');       
  }
  
  function gotoquery(path){
      document.Form1.pageNO.value=1;
      document.Form1.pageSize.value==10;
      Pub.submitActionWithForm('Form2',path,'Form1'); 
  }