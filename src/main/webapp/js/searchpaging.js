//load the initial contents
$("document").ready(function() {
	var searchedname = $("#searchedname").val();
	loadContent(searchedname, 0);
	countPages(searchedname);
});


//load the company data in a table
function loadContent(searchedname, pageIndex){
	$.ajax({
      		type: "GET",
      		url: "/SecondMarket/loadsearchedcompanies.htm?searchedname=" + searchedname + "&pageIndex=" + pageIndex,
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
            	alert('loadSearchedCompaniesError');
	 		}
    	});
}


//count the amount of pages
function countPages(searchedname){
	$.ajax({
      		type: "GET",
      		url: "/SecondMarket/countsearchedpages.htm?searchedname=" + searchedname,
      		dataType: "text",
      		success: function(data) {
      			$("#searchedpage_count").val(data); 
      			generatePages(searchedname, 1);
      		},
    	});
}


//generate the clickable pagers
function generatePages(searchedname, selected){
	var pages = $("#searchedpage_count").val();
	
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
		$('#paginator').html(pagers);
		$(".pagor").click(function() {
			var index = $(".pagor").index(this);
			loadContent(searchedname, index);
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
				updatePage(searchedname, this);
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
				updatePage(searchedname, this);
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
				updatePage(searchedname, this);
			});			
		}
	}
}


//update the pagers
function updatePage(searchedname, elem) {
	// Retrieve the number stored and position elements based on that number
	var selected = $(elem).text();

	// First update content
	loadContent(searchedname, selected - 1);
	
	// Then update links
	generatePages(selected);
}
