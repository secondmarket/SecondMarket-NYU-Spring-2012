var isDescending = true;
var sortByField = "fundingAmount";
var companyName = "";
var selectedCountry = "all";
var industryArray = [];
var minFunding = 0;
var maxFunding = 100000001;
var employees = -1;

//load the initial contents
$("document").ready(function() {
	loadContent(0);
	countPages();
    
});


/*
//load the company data in a table
function loadContent(pageIndex, sortByField){
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


function clearAll(){
    isDescending = true;
    sortByField = "fundingAmount";
    companyName = "";
    selectedCountry = "all";
    industryArray = [];
    minFunding = 0;
    maxFunding = 100000001;
    employees = -1;
    loadContent(0);
}


function searchByCompanyName(value){
    companyName = $.trim(value);
    loadContent(0);
}


function filterByCountry(){
    selectedCountry = $("#country").val();
    loadContent(0);
}


function filterByIndustry(){
    industryArray = [];
    $("input[name='industry']:checked").each(function ()
    {
        industryArray.push(this.value);
    });
    loadContent(0);
}


function filterByFundingRange(){
    minFunding = $("#minFunding").val();
    maxFunding = $("#maxFunding").val();
    loadContent(0);
}


function filterByEmployee(){
    employees = $("input[name='employee']:checked").val();
    loadContent(0);
}

function filterBySortingOrder(){
    if ($("input[name='sortingorder']:checked").val() == "Descending"){
        isDescending = true;
    } else {
        isDescending = false;
    }
    loadContent(0);
}


function showLoading() {
    $('#CompanyMainTable').hide();
    $('#paginator').hide();
    $("#loadingdiv").show();
}


function delayAnimation(){
    setTimeout('hideLoading()',500)
}


function hideLoading() {
    $("#loadingdiv").hide();
    $('#CompanyMainTable').fadeIn();
    $('#paginator').fadeIn();
}


function loadContent(pageIndex){
    showLoading();
	$.ajax({
      		type: "GET",
      		url: "/SecondMarket/loadcompany.htm",
            data: "pageIndex=" + pageIndex + 
                  "&sortByField=" + sortByField + 
                  "&isDescending=" + isDescending + 
                  "&selectedCountry=" + selectedCountry + 
                  "&companyName=" + companyName + 
                  "&industry=" + industryArray +
                  "&minFunding=" + minFunding +
                  "&maxFunding=" + maxFunding +
                  "&employees=" + employees,
      		dataType: "json",
      		success: function(data) {
                //alert(data);
      			var jsonData = eval("("+data+")");
      			var tablebody = '';
                
      			$.each(jsonData, function(key, val){
                    tablebody += 
                    '<div class="sm-card sm-span-16 sm-unhide last sm-mb sm-click-card" onclick="location.href=\'' + '/SecondMarket/viewcompanyinfo.htm?companyName=' + val.name + '\'"><div class="span-4 last"><a href="#"><img class="sm-badge" alt="' + val.name + '" src="/SecondMarket/getLogo.htm?companyName=' + val.name + '"></a></div><table border="0" cellspacing="0" cellpadding="0"><tr><td class="sm-b sm-card-name" width="410"><a href="#" class="sm-card-link">' + val.name + '</a></td><td rowspan="2" class="sm-r"><button class="span-4 sm-watch-toggle sm-watch-button sm-button sm-mbs" >' + val.funding + '</button></td></tr><tr><td>' + val.industry + ' Industry</td><td class="sm-tar">' + val.employees + ' Employees</td></tr><tr><td width="300">' + val.address + '</td><td width="130" class="sm-tar"><div>Since ' + val.foundedDate + '</div></td></tr></table></div>';
      			});
      			
      			$('#CompanyMainTable').html(tablebody);
                
                delayAnimation();
				
      		},
      		error: function(data){
            	alert('loadError');
	 		}
    	});
        
}



//count the amount of pages, same as counting unsorted pages amount
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
	
	if (pages > 1 && pages <= 5) {
		var pagers = "<ul>";
		for (i = 1; i <= Number(pages); i++) {
			if (i == 1){
				pagers += "<li><a class='pagor selected' style='cursor:pointer;text-decoration:none'>" + i + "</a></li>";
			} else {
				pagers += "<li><a class='pagor' style='cursor:pointer;text-decoration:none'>" + i + "</a></li>";
			}
		}
		pagers += "<li><div style='clear:both;'></div></li></ul>";
	
		$("#paginator").empty();
		$("#paginator").html(pagers);
		$(".pagor").click(function() {
			var index = $(".pagor").index(this);
			loadContent(index);
			$(".pagor").removeClass("selected");
			$(this).addClass("selected");
		});		
	} else {
		if (selected < 5) {
			// Draw the first 5 then have ... link to last
			var pagers = "<ul>";
			for (i = 1; i <= 5; i++) {
				if (i == selected) {
					pagers += "<li><a class='pagor selected' style='cursor:pointer;text-decoration:none'>" + i + "</a></li>";
				} else {
					pagers += "<li><a class='pagor' style='cursor:pointer;text-decoration:none'>" + i + "</a></li>";
				}				
			}
			pagers += "<li><a class='dot'>...</a></li><li><a class='pagor' style='cursor:pointer;text-decoration:none'>" + Number(pages) + "</a></li><li><div style='clear:both;'></div></li></ul>";
			
			$("#paginator").empty();
			$('#paginator').html(pagers);
			$(".pagor").click(function() {
				updateSortedPage(this);
			});
		} else if (selected > (Number(pages) - 4)) {
			// Draw ... link to first then have the last 5
			var pagers = "<ul><li><a class='pagor' style='cursor:pointer;text-decoration:none'>1</a></li><li><a class='dot'>...</a></li>";
			for (i = (Number(pages) - 4); i <= Number(pages); i++) {
				if (i == selected) {
					pagers += "<li><a class='pagor selected' style='cursor:pointer;text-decoration:none'>" + i + "</a></li>";
				} else {
					pagers += "<li><a class='pagor' style='cursor:pointer;text-decoration:none'>" + i + "</a></li>";
				}				
			}			
			pagers += "<li><div style='clear:both;'></div></li></ul>";
			
			$("#paginator").empty();
			$('#paginator').html(pagers);
			$(".pagor").click(function() {
				updateSortedPage(this);
			});		
		} else {
			// Draw the number 1 element, then draw ... 2 before and two after and ... link to last
			var pagers = "<ul><li><a class='pagor' style='cursor:pointer;text-decoration:none'>1</a></li><li><a class='dot'>...</a></li>";
			for (i = (Number(selected) - 2); i <= (Number(selected) + 2); i++) {
				if (i == selected) {
					pagers += "<li><a class='pagor selected' style='cursor:pointer;text-decoration:none'>" + i + "</a></li>";
				} else {
					pagers += "<li><a class='pagor' style='cursor:pointer;text-decoration:none'>" + i + "</a></li>";
				}
			}
			pagers += "<li><a class='dot' style='cursor:pointer;text-decoration:none'>...</a></li><li><a class='pagor' style='cursor:pointer;text-decoration:none'>" + pages + "</a></li><li><div style='clear:both;'></div></li></ul>";
			
			$("#paginator").empty();
			$('#paginator').html(pagers);
			$(".pagor").click(function() {
				updateSortedPage(this);
			});			
		}
	}
}


//update the pagers
function updateSortedPage(elem) {
	// Retrieve the number stored and position elements based on that number
	var selected = $(elem).text();

	// First update content
	loadContent(selected - 1);
	
	// Then update links
	generatePages(selected);
}

