//load the initial contents
$("document").ready(function() {
	loadContent(0);
	countPages();
});


//load the company data in a table
function loadContent(pageIndex){
	$.ajax({
      		type: "GET",
      		url: "/SecondMarket/loadcompanies.htm?pageIndex=" + pageIndex,
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
            	alert('loadContentError');
	 		}
    	});
}


//count the amount of pages
function countPages(){
	$.ajax({
      		type: "GET",
      		url: "/SecondMarket/countpages.htm",
      		dataType: "text",
      		success: function(data) {
      			$("#page_count").val(data); 
      			generatePages(1);
      		},
    	});
}


//generate the clickable pagers
function generatePages(selected){
	var pages = $("#page_count").val();
	
	if (pages <= 5) {
		var pagers = "<div id='paginator'>";
		for (i = 1; i <= Number(pages); i++) {
			if (i == 1){
				pagers += "<a href='#' class='pagor selected'>" + i + "</a>";
			} else {
				pagers += "<a href='#' class='pagor'>" + i + "</a>";
			}
		}
		pagers += "<div style='clear:both;'></div></div>";
	
		$("#content").after(pagers);
		$(".pagor").click(function() {
			var index = $(".pagor").index(this);
			loadContent(index);
			$(".pagor").removeClass("selected");
			$(this).addClass("selected");
		});		
	} else {
		if (selected < 5) {
			// Draw the first 5 then have ... link to last
			var pagers = "<div id='paginator'>";
			for (i = 1; i <= 5; i++) {
				if (i == selected) {
					pagers += "<a href='#' class='pagor selected'>" + i + "</a>";
				} else {
					pagers += "<a href='#' class='pagor'>" + i + "</a>";
				}				
			}
			pagers += "<div style='float:left;padding-left:6px;padding-right:6px;'>...</div><a href='#' class='pagor'>" + Number(pages) + "</a><div style='clear:both;'></div></div>";
			
			$("#paginator").remove();
			$("#content").after(pagers);
			$(".pagor").click(function() {
				updatePage(this);
			});
		} else if (selected > (Number(pages) - 4)) {
			// Draw ... link to first then have the last 5
			var pagers = "<div id='paginator'><a href='#' class='pagor'>1</a><div style='float:left;padding-left:6px;padding-right:6px;'>...</div>";
			for (i = (Number(pages) - 4); i <= Number(pages); i++) {
				if (i == selected) {
					pagers += "<a href='#' class='pagor selected'>" + i + "</a>";
				} else {
					pagers += "<a href='#' class='pagor'>" + i + "</a>";
				}				
			}			
			pagers += "<div style='clear:both;'></div></div>";
			
			$("#paginator").remove();
			$("#content").after(pagers);
			$(".pagor").click(function() {
				updatePage(this);
			});		
		} else {
			// Draw the number 1 element, then draw ... 2 before and two after and ... link to last
			var pagers = "<div id='paginator'><a href='#' class='pagor'>1</a><div style='float:left;padding-left:6px;padding-right:6px;'>...</div>";
			for (i = (Number(selected) - 2); i <= (Number(selected) + 2); i++) {
				if (i == selected) {
					pagers += "<a href='#' class='pagor selected'>" + i + "</a>";
				} else {
					pagers += "<a href='#' class='pagor'>" + i + "</a>";
				}
			}
			pagers += "<div style='float:left;padding-left:6px;padding-right:6px;'>...</div><a href='#' class='pagor'>" + pages + "</a><div style='clear:both;'></div></div>";
			
			$("#paginator").remove();
			$("#content").after(pagers);
			$(".pagor").click(function() {
				updatePage(this);
			});			
		}
	}
}


//update the pagers
function updatePage(elem) {
	// Retrieve the number stored and position elements based on that number
	var selected = $(elem).text();

	// First update content
	loadContent(selected - 1);
	
	// Then update links
	generatePages(selected);
}

