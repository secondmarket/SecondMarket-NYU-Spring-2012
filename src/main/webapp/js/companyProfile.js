function generateGoogleMap(companyName){
	$.ajax({
      		type: "GET",
			url: '/SecondMarket/getOfficesByCompanyName.htm',
			data: "companyName=" + companyName,
      		dataType: "json",
      		success: function(data) {
                var jsonData = eval("("+data+")");
                if (jsonData.offices[0].latitude==0.0 && jsonData.offices[0].longitude==0.0){
                    return;
                } else if(jsonData.offices[0].latitude==null || jsonData.offices[0].longitude==null){
                    return;
                } else {
                    generateDragableMap(data);
                }
      		},
      		error: function(data){
            	alert('getOffices error');
	 		}
    	});
	}
    

function generateDragableMap(data){
    var jsonData = eval("("+data+")");
    //alert(jsonData.offices[0].latitude);
    var DragableMap = null; 
    var level = 8; 
    var options;
    var centerLatlng = new google.maps.LatLng(jsonData.offices[0].latitude, jsonData.offices[0].longitude); 
    //var bounds = new google.maps.LatLngBounds();

    options = {   
      zoom: level, 
      center: centerLatlng, 
      mapTypeId: google.maps.MapTypeId.ROADMAP, 
      mapTypeControl: true 
    }; 
    
    DragableMap = new google.maps.Map(document.getElementById("googleMapDiv"), options); 
	
    for (var i = 0; i < jsonData.offices.length; i++) {
    	var myLatLng = new google.maps.LatLng(jsonData.offices[i].latitude, jsonData.offices[i].longitude);
    	//bounds.extend(myLatLng);
    	var marker = new google.maps.Marker({
        	position: myLatLng,
        	map: DragableMap
    	});

    	//DragableMap.fitBounds(bounds);
    	//DragableMap.setCenter(bounds.getCenter(), DragableMap.getBoundsZoomLevel( bounds ) );

    	// var j = i + 1;
    	// marker.setTitle(j.toString());
    	attachInfoWindow(marker, i, DragableMap, jsonData.offices[i].city);
    }
  }
  

function attachInfoWindow(marker, number, DragableMap, address) {
    var infowindow = new google.maps.InfoWindow({ 
      content: address,
    });
    google.maps.event.addListener(marker, 'click', function() {
      infowindow.open(DragableMap,marker);
    });

  }