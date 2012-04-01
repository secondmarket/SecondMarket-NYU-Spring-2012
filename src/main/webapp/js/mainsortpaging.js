var isDescending = false;

//load the initial contents
function loadSortedRecords(sortByField){
	if ( isDescending == true){
		isDescending = false;
	} else {
		isDescending = true;
	}
	loadSortedContent(0, sortByField);
	countSortedPages(sortByField);
}

//load the company data in a table
function loadSortedContent(pageIndex, sortByField){
	$.ajax({
      		type: "GET",
      		url: "/SecondMarket/loadsortedcompanies.htm?pageIndex=" + pageIndex + "&sortByField=" + sortByField + "&isDescending=" + isDescending,
      		dataType: "json",
      		success: function(data) {
      			var jsonData = eval("("+data+")");
      			var tablebody = '';
      			$.each(jsonData, function(key, val){
      				tablebody += '<tr><td width="20%" style="text-align: center">' + 
					'<a href=\"/SecondMarket/viewcompanyinfo.htm?companyName=' + val.name + '\" target=\"_blank\">' + val.name + '</a>' +
      				'</td><td width="20%" style="text-align: center">' + val.location + 
      				'</td><td width="20%" style="text-align: center">' + val.country + 
					'</td><td width="20%" style="text-align: center">' + val.funding + 
					'</td><td width="20%" style="text-align: center">' + val.industry + '</td></tr>';
      			});
      			
      			$('#companyTable').html(tablebody);
				
      		},
      		error: function(data){
            	alert('loadSortedCompaniesError');
	 		}
    	});
}


//count the amount of pages, same as counting unsorted pages amount
function countSortedPages(sortByField){
	$.ajax({
      		type: "GET",
      		url: "/SecondMarket/countpages.htm",
      		dataType: "text",
      		success: function(data) {
      			$("#page_count").val(data); 
      			generateSortedPages(1, sortByField);
      		},
    	});
}


//generate the clickable pagers
function generateSortedPages(selected, sortByField){
	var pages = $("#page_count").val();
	
	if (pages <= 5) {
		var pagers = "<ul>";
		for (i = 1; i <= Number(pages); i++) {
			if (i == 1){
				pagers += "<li><a href='#' class='pagor selected'>" + i + "</a></li>";
			} else {
				pagers += "<li><a href='#' class='pagor'>" + i + "</a></li>";
			}
		}
		pagers += "<li><div style='clear:both;'></div></li></ul>";
	
		$("#paginator").empty();
		$("#paginator").html(pagers);
		$(".pagor").click(function() {
			var index = $(".pagor").index(this);
			loadSortedContent(index, sortByField);
			$(".pagor").removeClass("selected");
			$(this).addClass("selected");
		});		
	} else {
		if (selected < 5) {
			// Draw the first 5 then have ... link to last
			var pagers = "<ul>";
			for (i = 1; i <= 5; i++) {
				if (i == selected) {
					pagers += "<li><a href='#' class='pagor selected'>" + i + "</a></li>";
				} else {
					pagers += "<li><a href='#' class='pagor'>" + i + "</a></li>";
				}				
			}
			pagers += "<li><a class='dot'>...</a></li><li><a href='#' class='pagor'>" + Number(pages) + "</a></li><li><div style='clear:both;'></div></li></ul>";
			
			$("#paginator").empty();
			$('#paginator').html(pagers);
			$(".pagor").click(function() {
				updateSortedPage(this, sortByField);
			});
		} else if (selected > (Number(pages) - 4)) {
			// Draw ... link to first then have the last 5
			var pagers = "<ul><li><a href='#' class='pagor'>1</a></li><li><a class='dot'>...</a></li>";
			for (i = (Number(pages) - 4); i <= Number(pages); i++) {
				if (i == selected) {
					pagers += "<li><a href='#' class='pagor selected'>" + i + "</a></li>";
				} else {
					pagers += "<li><a href='#' class='pagor'>" + i + "</a></li>";
				}				
			}			
			pagers += "<li><div style='clear:both;'></div></li></ul>";
			
			$("#paginator").empty();
			$('#paginator').html(pagers);
			$(".pagor").click(function() {
				updateSortedPage(this, sortByField);
			});		
		} else {
			// Draw the number 1 element, then draw ... 2 before and two after and ... link to last
			var pagers = "<ul><li><a href='#' class='pagor'>1</a></li><li><a class='dot'>...</a></li>";
			for (i = (Number(selected) - 2); i <= (Number(selected) + 2); i++) {
				if (i == selected) {
					pagers += "<li><a href='#' class='pagor selected'>" + i + "</a></li>";
				} else {
					pagers += "<li><a href='#' class='pagor'>" + i + "</a></li>";
				}
			}
			pagers += "<li><a class='dot'>...</a></li><li><a href='#' class='pagor'>" + pages + "</a></li><li><div style='clear:both;'></div></li></ul>";
			
			$("#paginator").empty();
			$('#paginator').html(pagers);
			$(".pagor").click(function() {
				updateSortedPage(this, sortByField);
			});			
		}
	}
}


//update the pagers
function updateSortedPage(elem, sortByField) {
	// Retrieve the number stored and position elements based on that number
	var selected = $(elem).text();

	// First update content
	loadSortedContent(selected - 1, sortByField);
	
	// Then update links
	generateSortedPages(selected, sortByField);
}

