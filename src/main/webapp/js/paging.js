var isDescending = true;
var sortByField = "fundingAmount";

//load the initial contents
$("document").ready(function() {
	loadSortedContent(0, sortByField);
	countSortedPages(sortByField);
});


/*
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
*/


function loadSortedContent(pageIndex, sortByField){
	$.ajax({
      		type: "GET",
      		url: "/SecondMarket/loadsortedcompanies.htm?pageIndex=" + pageIndex + "&sortByField=" + sortByField + "&isDescending=" + isDescending,
      		dataType: "json",
      		success: function(data) {
                //alert(data);
      			var jsonData = eval("("+data+")");
      			var tablebody = '';
                
      			$.each(jsonData, function(key, val){
                    tablebody += 
                    '<div class="sm-card sm-span-16 sm-unhide last sm-mb sm-click-card" onClick="location.href=\'' + '/SecondMarket/viewcompanyinfo.htm?companyName=' + val.name + '\'"><div class="span-2 last"><a href="#"><img class="sm-badge" alt="' + val.name + 
                    '" src="https://dbr2dggbe4ycd.cloudfront.net/company/facebook_50.png"></a></div><table border="0" cellspacing="0" cellpadding="0"><tr><td class="sm-b sm-card-name" width="410"><a href="#" class="sm-card-link">' + val.name + '</a></td><td width="130" class="sm-tar"><div>Since ' + val.foundedDate + '</div></td></tr><tr><td>' + val.industry + ' Industry</td><td class="sm-tar">' + val.employees + ' Employees</td></tr><tr><td width="300">' + val.address + '</td><td rowspan="2" class="sm-r"><form action="#" method="post" class="sm-watch-unwatch-form sm-mts"><input type="hidden" class="sm-watch-slug" name="slug" value="twitter" /><button type="submit" rel="twitter" class="span-4 sm-watch-toggle sm-watch-button sm-button sm-mbs" hoveralt="' + val.funding + '" clickalt="' + val.funding + '" changealt="' + val.funding + '"watchalt="' + val.funding + '">' + val.funding + '</button></form></td></tr></table></div>';
                
                    
      			});
      			
      			$('#CompanyMainTable').html(tablebody);
                
				
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

