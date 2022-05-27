/**
 * 
 */
 
  $( document ).ready(function() {
		    if($('#PublicationModal').length){
		    	var pureId = $("#pureid").val();
		    	$('#editpure').modal({
		    		  show: true
		    		});
		    	$('#editpure').find('#information').remove();
		    	$('.modal-title').text("Notification");
		    	$('#editpure').find('.modal-body').text("This record already exists! " + pureId);
				
				$('#editpure').find('#confirm').css({
					"display" : "block"
				});
				
				$('#editpure').find('#confirm').prop("href", "/publications/"+pureId);
				
		    }
		    
		    
		    
		});
	 
	 function success() {
		 
	   	 if(document.getElementById("inputPureid").value==="") { 
	               document.getElementById('submitPureid').disabled = true; 
	           } else { 
	               document.getElementById('submitPureid').disabled = false;
	           }
	       }
	 
	 function DisplayProgressMessage(ctl, msg) {
		 setTimeout(function () {
			 $(ctl).prop("disabled", true);
		     $(ctl).text(msg);
		     $(".submit-progress").css({ display: 'block'});
		 },30);
	    return true;
	    }
	 