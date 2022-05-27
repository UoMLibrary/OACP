/**
 * 
 */
 
 
 $(document).ready(function() {
		var spinner = $('#loader');
		
		$("form").keypress(function(e) {
			  //Enter key
			  if (e.which == 13) {
			    return false;
			  }
			});
		
		var table = $("#publicationTable").DataTable({
			dom: '<"top"Blip>rt<"bottom"lp><"clear">',
			processing: true,
			serverSide: true,
			ajax: {
					url : "/publication/list",
					 data: function ( d ) {
		                    d.pureId = $('#col0_filter').val();
		                    d.publicationStatus = $('#col1_filter').val();
		                    d.record_status=$('#col3_filter').val();
							d.compliance_status=$('#col4_filter').val();
							d.UKRI_compliance_status=$('#col5_filter').val();
							d.doi=$('#col10_filter').val();
							d.organization=$('#col13_filter').val();
							d.organizaitonlevel=$('#col13_filter option:checked').closest('optgroup').attr('value');
							
							d.help_raise_visibility= $('#col6_filter').val();
							d.potentially_published= $('#col7_filter').val();
							d.UKRI_S_Flag= $('#col8_filter').val();
		                }
				},
				columns : [
					{"data" : "pureId",
						fnCreatedCell : function(nTd, sData,oData, iRow,iCol) {
							$(nTd).html("<a href='/publications/"+oData.pureId+"'>"
									+ oData.pureId+ "</a>");			
						}
					},
					{"data" : "publicationStatus"},
					{"data" : "title"},
					{"data" : "record_status"},
					{"data" : "compliance_status"},
					{"data" : "ukri_compliance_status"},
					{"data" : "acceptedDate"},
					{"data" : "ePublicationDate"},
					{"data" : "publicationDate"},
					{"data" : "potentially_published"},
					
					{data : null,
						defaultContent: "<i class='fas fa-sync' title='sync'/>",
						orderable : false
					},
					{data : null,
						defaultContent: '<i class="fas fa-trash-alt" title="delete"/>',
						orderable : false
					} ],
					order : [ 0, "desc" ]
		});
	
		
	$('#publicationTable tbody').on( 'click', 'tr', function () {
		 $(this).toggleClass('selected');
			
		} );
	
	$("#bulkEdit").on('click',function(){
		  var pureIds="";
			table.rows('.selected').data().map((row) => {
				pureIds += row.pureId + ","; 
			});
		
			$("#bulkEditMo").find("#editRefComStatus").removeClass("showdiv hiddendiv").addClass("showdiv");
			$("#bulkEditMo").find("#editUkriComplianceStatus").removeClass("showdiv hiddendiv").addClass("showdiv");
			$("#bulkEditMo").find("#editrecordStatus").removeClass("showdiv hiddendiv").addClass("showdiv");
		  $('#bulkEditMo').modal({
				show : true
			});
		  
			$('#bulkEditMo').find('.pureids').text(pureIds);
			
			$('#bulkEditMo').find('#bulkEditSumit').bind("click",function(event){
				var recordStatus = $("#recordStatus").val();
				var complicanceStatus = $("#complianceStatus").val();
				var ukriComStatuts = $("#ukricompliance").val();
				var ukriFlag = $("input[name='ukriFlag']:checked").val();
				
				if(recordStatus){
					 $.ajax({
						 	type: "POST",
					        url: "/bulkEdit/update",
					        data:{
					        	BulkpureIds:pureIds,
				        		recordStatus:recordStatus,
				        		complicanceStatus:complicanceStatus,
				        		ukriComStatuts:ukriComStatuts,
				        		ukriFlag:ukriFlag
				        	}, 
					        success: function(data){
					        	table.ajax.reload();
					        	 $('#bulkEditMo').modal('hide');
					        }
					    });
					
				}
				
			});
			
			
	});
	
	$("#bulkEditRecordStatus").on("click",function(){
		 var pureIds="";
			table.rows('.selected').data().map((row) => {
				pureIds += row.pureId + ","; 
			});
			
		$("#bulkEditMo").find("#editRefComStatus").removeClass("showdiv hiddendiv").addClass("hiddendiv");
		$("#bulkEditMo").find("#editUkriComplianceStatus").removeClass("showdiv hiddendiv").addClass("hiddendiv");
		$("#bulkEditMo").find("#editrecordStatus").removeClass("showdiv hiddendiv").addClass("showdiv");
		$('#bulkEditMo').find('.pureids').text(pureIds);
		$('#bulkEditMo').modal({
				show : true
			});
		
		
		$('#bulkEditMo').find('#bulkEditSumit').bind("click",function(event){
			var recordStatus = $("#recordStatus").val();
			console.log("recordStatus: " + recordStatus);
			if(recordStatus){
				 $.ajax({
					 	type: "POST",
				        url: "/bulkEdit/update",
				        data:{
				        	BulkpureIds:pureIds,
			        		recordStatus:recordStatus
			        	}, 
				        success: function(data){
				        	table.ajax.reload();
				        	 $('#bulkEditMo').modal('hide');
				        }
				    });
				
			}
			
		});
		
		
	});
	
	$("#bulkEditRefCompliance").on("click",function(){
		 var pureIds="";
			table.rows('.selected').data().map((row) => {
				pureIds += row.pureId + ","; 
			});
			$("#bulkEditMo").find("#editRefComStatus").removeClass("showdiv hiddendiv").addClass("showdiv");
			$("#bulkEditMo").find("#editUkriComplianceStatus").removeClass("showdiv hiddendiv").addClass("hiddendiv");
			$("#bulkEditMo").find("#editrecordStatus").removeClass("showdiv hiddendiv").addClass("hiddendiv");
			$('#bulkEditMo').find('.pureids').text(pureIds);
			$('#bulkEditMo').modal({
				show : true
			});
	});
	
	
	$("#bulkEditUkriCompliance").on("click",function(){
		 var pureIds="";
			table.rows('.selected').data().map((row) => {
				pureIds += row.pureId + ","; 
			});
			
			$("#bulkEditMo").find("#editRefComStatus").removeClass("showdiv hiddendiv").addClass("hiddendiv");
			$("#bulkEditMo").find("#editUkriComplianceStatus").removeClass("showdiv hiddendiv").addClass("showdiv");
			$("#bulkEditMo").find("#editrecordStatus").removeClass("showdiv hiddendiv").addClass("hiddendiv");
			$('#bulkEditMo').find('.pureids').text(pureIds);
			$('#bulkEditMo').modal({
				show : true
			});
	});
	
	
	$('#publicationTable tbody').on('click','.fa-sync',function() {
		var data = table.row($(this).parents('tr')).data();
		spinner.find("#spinMes").text("Sync Record: " + data.pureId);
		spinner.css({ display: 'block'});
		$.ajax({
	        type: "GET",
	        url:  "/publication/sync/"+data.pureId,
	        success: function(data){
	        	console.log("data: "+ data);
	        	spinner.css({ display: 'none'});
	        	 $('.toast').toast('show');
				  $('.toast-title').text("Notification!");
				  if(data == 'sync'){
					  table.ajax.reload();
					  $('.toast-body').text("Sync Record successfully!");
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
		
	$('#publicationTable tbody').on('click','.fa-trash-alt',function() {
		
		var data = table.row($(this).parents('tr')).data();
		$('#Notice').modal({
			show : true
		})
		$('#Notice').find('#information').remove();
		$('#Notice').find('.modal-title').text("Notification");
		
		$('#Notice').find('.modal-body').text("Are you sure you want to delete pureId: " + data.pureId);
		
		$('#Notice').find('#confirm').css({
			"display" : "block"
		});
		
		$('#Notice').find('#confirm').click(function(){
			$.ajax({
		        type: "GET",
		        url:  "/publication/delete/"+data.pureId,
		        success: function(data){
		        	
		           if(data=="delete"){
		        	   table.ajax.reload();
		        	   $('#Notice').modal('hide')
		        	   $('.toast').toast('show');
						  
						$('.toast-title').text("Notification!");
						$('.toast-body').text("Delete Records successfully!");
		        	   
		           }
		        }
		    });
		});
		
	});
	
	
	
	$(".column_filter").bind("change keyup",function(event) {
		table.ajax.reload();
		
		var searchFileds = {"pureId":$('#col0_filter').val(),
				"publicationStatus":$('#col1_filter').val(),
				"recordstatus":$('#col3_filter').val(),
				"compliancestatus":$('#col4_filter').val(),
				"UKRIcompliancestatus":$('#col5_filter').val(),
				"doi":$('#col10_filter').val(),
				"organization":$('#col13_filter').val(),
				"helpraisevisibility":$('#col6_filter').val(),
				"potentiallypublished":$('#col7_filter').val(),
				"UKRISFlag":$('#col8_filter').val()};
		localStorage.setItem("searchFileds", JSON.stringify(searchFileds));
		
	});
	
	

	if(localStorage.getItem("searchFileds")){
		var searchFiledsJson = JSON.parse(localStorage.getItem("searchFileds"));
		$('#col0_filter').val(searchFiledsJson['pureId']);
		$('#col1_filter').val(searchFiledsJson['publicationStatus']);
		$('#col4_filter').val(searchFiledsJson['compliancestatus']);
		$('#col3_filter').val(searchFiledsJson['recordstatus']);
		$('#col5_filter').val(searchFiledsJson['UKRIcompliancestatus']);
		$('#col10_filter').val(searchFiledsJson['doi']);
		$('#col13_filter').val(searchFiledsJson['organization']);
		$('#col6_filter').val(searchFiledsJson['helpraisevisibility']);
		$('#col7_filter').val(searchFiledsJson['potentiallypublished']);
		$('#col8_filter').val(searchFiledsJson['UKRISFlag']);
		table.ajax.reload();
	}


	$(".btnReset").bind("click", function(event) {
		 
		$('#col0_filter').val("");
        $('#col1_filter').val("");
        $('#col3_filter').val("");
		$('#col4_filter').val("");
		$('#col5_filter').val("");
		$('#col10_filter').val("");
		$('#col13_filter').val("");
		$('#col6_filter').val("");
		$('#col7_filter').val("");
		$('#col8_filter').val("");
		localStorage.removeItem("searchFileds");
		table.ajax.reload();
		
	}); 
	
	
	$('.show-ukriStatus .btn').on("click",function(){
		   
		   var ukristatus = $(this).find('input[type=radio]').val();
		  
		   if(ukristatus == 'true'){
			   $('#ukricompliance').prop('disabled', false);
		   }
		   
		   if(ukristatus == 'false'){
			   $('#ukricompliance').prop('disabled', true);
		   }
		   
		   
	});
	
	
	 
	  if($('#PublicationToast').length){
	    	 
			  var message = $('#message').val();
			 
			  if(message == 'delete'){
				  $('.toast').toast('show');
				  $('.toast-title').text("Notification!");
				  $('.toast-body').text("Delete Record successfully!");
			  }
			    	
	    }

	});