/**
 * 
 */
 
 $(document).ready(function() {
		var table = $("#auditTable").DataTable({
			dom : 'liptip',
			processing: true,
			serverSide: true,
			ajax: {
					url : "/audit/list",
					 data: function ( d ) {
		                    d.message = $('#col1_filter').val();
		                    d.level = $('#col2_filter').val();
		                    d.type = $('#col3_filter').val();
					 }
			},
			order : [ 3, "desc" ],
			columns : [
				{"data" : "level"},
				{"data" : "message"},
				{"data" : "created_by"},
				{"data" : "created_date",
					"render" : function(data) {
						return moment(data).format('DD/MM/YYYY HH:mm');}}]
		});
						
		$(".column_filter").bind("change keyup click",
				function(event) {
			
			table.ajax.reload();
			
		});
		
		$(".btnReset").bind("click", function(event) {

	        $('#col1_filter').val("");
	        $('#col3_filter').val("");
			$('#col2_filter').val("");
			
			table.ajax.reload();
		 
		});
				
		$('#auditTable tbody').on('click','tr',function() {
			var data = table.row(this).data();
			$('#auditDeatail').modal({
				show : true
			})
			$('.modal-title').text(data.message);
			if (data.updateDetail.includes("{")) {
				$('#auditDeatail')
						.find('#information')
						.html(
								library.json
										.prettyPrint(JSON
												.parse(data.updateDetail)));

			} else {
				$('#auditDeatail').find(
						'#information').html(
								data.updateDetail);
			}
		});
						

		if (!library)
			var library = {};
		
		library.json = {
				replacer : function(match, pIndent, pKey, pVal,
						pEnd) {
					var key = '<span class=json-key>';
					var val = '<span class=json-value>';
					var str = '<span class=json-string>';
					var r = pIndent || '';
					if (pKey)
						r = r + key + pKey.replace(/[": ]/g, '')
								+ '</span>: ';
					if (pVal)
						r = r + (pVal[0] == '"' ? str : val) + pVal
								+ '</span>';
					return r + (pEnd || '');
				},
				prettyPrint : function(obj) {
					var jsonLine = /^( *)("[\w]+": )?("[^"]*"|[\w.+-]*)?([,[{])?$/mg;
					return JSON.stringify(obj, null, 3).replace(
							/&/g, '&amp;')
							.replace(/\\"/g, '&quot;').replace(
									/</g, '&lt;').replace(/>/g,
									'&gt;').replace(jsonLine,
									library.json.replacer);
				}
			};
						

});