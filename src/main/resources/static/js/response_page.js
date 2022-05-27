/**
 * 
 */
 
 $(document).ready(function() {
		
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
				   $('#Ukricompliance').show(500);
			   }
			   if(ukristatus == '0'){
				   $('#Ukricompliance').hide(500)
			   }
		})
		
		/* add new note */
		$("#addNoteBtn").click(function() {

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
										+"<input style=' border: none;' name='notelist' value='"+noteText+"'></input></li>");
						$("#noteText").val("");
					} else {
						$("#NoteAlert").css({
							"display" : "block"
						});
					}
				});

		/* delete notes on page show */
		$("#NoteList").on("click","i.delete_icon",function(e) {
							$(this).closest('li').remove();
							if ($("#NoteList").find("li").length == 0) {
								$("#NoteList")
										.append(
												"<li class='list-group-item' id='noNote'>No Funders have been added</li>");
							}
						});

		/* end of add note */
					
	})