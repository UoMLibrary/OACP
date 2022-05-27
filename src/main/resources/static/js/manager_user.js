/**
 * 
 */
 
 $(document).ready(function(){
		
		var table = $("#publicationTable").DataTable({
			dom: '<"top"Blip>rt<"bottom"lp><"clear">',
			processing: true,
			serverSide: true,
			ajax: {
				url : "/users/list"
			},
			columns : [
				{"data" : "userName"},
				{"data" : "fullName"},
				{"data" : "role"},
				{"data" : null,
					defaultContent: '<i class="fas fa-pen-alt" title="edit"></i>',
					orderable : false
				},
				{"data" : null,
					defaultContent: '<i class="fas fa-trash-alt" title="delete"/>',
					orderable : false
				}]
		});
		$('#publicationTable tbody').on('click','.fa-pen-alt',function() {
			var data = table.row($(this).parents('tr')).data();
			  $.ajax({
					url : '/users/'+data.userId,
					type : 'get'
				}).done(function(data) {
					
					 $('#editUsermodal').modal({
							show : true
						})
						$('#editUsermodal').find('#information').remove();
						$('#editUsermodal').find('.modal-body').html(data);
						
					});
		});
		
		$('#editUsermodal').find("#submitUser").bind("click", function(){
			alert("test");
		});
		
		$('#publicationTable tbody').on('click','.fa-trash-alt',function() {
			
			var data = table.row($(this).parents('tr')).data();
 			$('#deleteUser').modal({
				show : true
			})
			$('#deleteUser').find('#information').remove();
			$('#deleteUser').find('.modal-title').text("Notification");
			
			$('#deleteUser').find('.modal-body').text("Are you sure you want to delete user: " + data.fullName);
			
			$('#deleteUser').find('#confirm').css({
				"display" : "block"
			});
			
			$('#deleteUser').find('#confirm').click(function(){
				$.ajax({
			        type: "GET",
			        url: "/user/delete/"+data.userId,
			        success: function(data){
			        	
			           if(data=="delete"){
			        	   table.ajax.reload();
			        	   $('#deleteUser').modal('hide')
			        	   $('.toast').toast('show');
							  
							$('.toast-title').text("Notification!");
							$('.toast-body').text("Delete User successfully!");
			        	   
			           }
			        }
			    });
			});
			  
		});
		
		  $("#addUser").submit(function(e) {

			    e.preventDefault(); // avoid to execute the actual submit of the form.

			    var userName = $('#userName').val();
			    var fullName = $('#fullName').val();
			    var role = $('#role').val();
			    element = document.getElementById('userAlert');
			    if(userName && fullName && role){
			    	 $.ajax({
					        type: "POST",
					        url: "/users/addUsers",
					        data: $("#addUser").serialize(), // serializes the form's elements.
					        success: function(data){
					        	console.log("add user:" + data);
					           if(data=="exist"){
					        	   element.classList.add("alert-warning");
					        	   element.classList.remove("alert-info");
					        	   element.innerHTML = userName + " already exist!";
					        	   element.setAttribute('style', 'display:block');
					        	  
					           }
					           if(data=="sucess"){
					        	   element.setAttribute('style', 'display:none');
					        	   $('.toast').toast('show');
									  
									$('.toast-title').text("Notification!");
									$('.toast-body').text("Created User successfully!");
									
					        	   table.ajax.reload();
					        	   document.getElementById('addUser').reset();
					           }
					        }
					    });
			    }
			    
			});
		  
			$("#resetUser").click(function(e){
				 e.preventDefault();
				 document.getElementById('userAlert').classList.remove("alert-warning");
				 document.getElementById('userAlert').classList.remove("alert-info");
				 document.getElementById('userAlert').setAttribute('style', 'display:none');
				document.getElementById('addUser').reset();
				 
			});
		
	});