/**
 * 
 */
 
 $(document).ready(function() {
		
	if(localStorage.getItem("sync")){
		console.log("synd: " + localStorage.getItem("sync"))
		 $('.toast').toast('show');
		 $('.toast-title').text("Notification!");
		 $('.toast-body').text(localStorage.getItem("sync"));
		 
		 localStorage.removeItem("sync");
	}
		
	$("#savePublication").click(function(){
		  $.ajax({
		        url: '/publication/update',
		        type: 'post',
		        data: $('#publicationForm').serialize()
		    }).done(function(response){
		    	$('.toast').toast('show');
				 $('.toast-title').text("Notification!"); 
		    	
		    	 if(response.indexOf("PotentiallyFalse")>-1){
		    		 document.getElementsByClassName('potentialli-published')[0].setAttribute('style', 'display:none');
				  }
		    	 
		    	 if(response.indexOf("update")>-1){
					$('.toast-body').text("This Record was updated successfully!");
				  }
		    	 
				  if(response.indexOf("updatenodata")>-1){
					  $('.toast-body').text("No update information was inputed!");
				  } 
		    	});
	});
		
		
		var ukriFlag = $("#ukriFlag").val();
		if(ukriFlag=='false'){
			  $("#ukristatusSelect").prop('disabled', 'disabled');
		}
		
		//getting note from note controller
		var pureId= $("#pureId").text();
		
		var spinner = $('#loader');

		$('#Scholarcy').click(function(e) {
			var name = $("#FileUrlval").val();
			if(name != ""){
				spinner.find("#spinMes").text("Please wait while processing Data...");
				spinner.css({ display: 'block'});
				$.ajax({
					url : '/scholarcydata?name=' + name,
					method : 'post'
				}).done(
						function(data) {
							spinner.css({ display: 'none'});
							$('#Scholarcy_result').modal({
					    		  show: true
				    		});
							
							$('#Scholarcy_result').find('#information').remove();
							$('#Scholarcy_result').find(
									'.modal-body').html(
									data);
						});
			}else{
				$('.toast').toast('show');
				$('.toast-title').text("File URL is empty!");
				$('.toast-body').text("Please input File URL");
			}
			

					
		});
		
		$("#synPure").click(function(){
			spinner.find("#spinMes").text("Sync Record: " + pureId);
			spinner.css({ display: 'block'});
			$.ajax({
		        type: "GET",
		        url:  "/publication/sync/"+pureId,
		        success: function(data){
		        	spinner.css({ display: 'none'});
		        	 $('.toast').toast('show');
					  $('.toast-title').text("Notification!");
					  if(data == 'sync'){
						  location.reload();
						  localStorage.setItem("sync","Synced Record successfully!")
						  
					  }
					  if(data == 'syncnopureID'){
						  $('.toast-body').text("This PureId is not found from Pure APIS!");
					  }
					  if(data == 'syncuptodate'){
						  $('.toast-body').text("This PureId is up to Date!");
					  } 
		        }
			});
			  
		});
		
		//If Pure, external or transformative agreemnt is chosen,
		//grey out Gateway Depositor field
		
		var depositvalue = $( "#depositRoute option:selected" ).val();
		if(depositvalue=='Pure' || depositvalue=='External' || depositvalue =='Transformative agreemnt'){
			$("#GatewayDepositor").prop('disabled', true);
		}
		
		$("#depositRoute").change(function() {
			depositvalue = $( "#depositRoute option:selected" ).val();
			if(depositvalue=='Pure' || depositvalue=='External' || depositvalue =='Transformative agreement'){
				$("#GatewayDepositor").prop('disabled', true);
			}else{
				$("#GatewayDepositor").prop('disabled', false);
			}
		});
		
		$('.show-ukriStatus .btn').on("click",function(){
			   
			   var ukristatus = $(this).find('input[type=radio]').val();
			   if(ukristatus == '1'){
				   $("#ukristatusSelect").removeAttr("disabled");
			   }
			   if(ukristatus == '0'){
				   $("#ukristatusSelect").prop('disabled', 'disabled');
			   }
		})
		
			
		/* add new note */
		$("#addNoteBtn").click(function(event) {
					event.preventDefault();
					var noteText = $("#noteText").val();
					if (noteText != "") {
						$("#NoteAlert").css({
							"display" : "none"
						});
						
						
						if ($("#noNote").length > 0) {
							$("#noNote").remove();
						}
						
						$("#NoteList").append(
								"<li class='list-group-item'><i class='fas fa-times delete_icon'></i>  "
										+"<input style='border: none;' name='notelist' value='"+noteText+"'></input></li>");
						
						$("#noteText").val("");
					} else {
						$("#NoteAlert").css({
							"display" : "block"
						});
					}
				});
		
		/* delete previous notes from db */
		$("#NoteList").on("click","i.delete_note",function(e) {
			var noteid = $(this).find("input").val();
			$.ajax({
				url: "/publication/deletenote/"+noteid,
				type: "POST"
			}).done( function(response){
				$('.toast').toast('show');
				$('.toast-title').text("Delete Notes!");
				$('.toast-body').text(response);
			});
			$(this).closest('li').remove();
			
		});
		/* end of add note */
		
		
		  if($('#PublicationToast').length){
			  
			  var message = $('#message').val();
			 
			  if(message == 'delete'){
				  $('.toast').toast('hide');
			  }else{
				  $('.toast').toast('show');
				  $('.toast-title').text("Notification!");
				  if(message == 'create'){
					  $('.toast-body').text("Create new Record successfully!");
				  }
				  if(message == 'sync'){
					  $('.toast-body').text("Sync Record successfully!");
				  }
				  if(message == 'syncnopureID'){
					  $('.toast-body').text("This PureId is not found from Pure APIS!");
				  }
				  if(message == 'syncuptodate'){
					  $('.toast-body').text("This PureId is up to Date!");
				  } 
			  }
			  
		    }
	
		$("#deleteRecord").click(function(){
			$('#delete_Record').modal({
	    		  show: true
	    		});
			$('#delete_Record').find('#information').remove();
			$('.modal-title').text("Notification");
			$('#delete_Record').find('.modal-body').text("Are you sure you want to delete pureId: " + pureId);
			
			$('#delete_Record').find('#confirm').css({
				"display" : "block"
			});
			
			/* $('#delete_Record').find('#confirm').prop("href", "/publication/delete/"+ pureId); */
			
			$('#delete_Record').find('#confirm').click(function(){
				$.ajax({
			        type: "GET",
			        url:  "/publication/delete/"+ pureId,
			        success: function(data){
			        	$('#delete_Record').modal("hide");
			           if(data=="delete"){
			        	   
			        	 $('#Notice').modal('hide')
							window.location.href = "/publications/all";   
			           }
			        }
			    });
			});
			
		});
		

	});