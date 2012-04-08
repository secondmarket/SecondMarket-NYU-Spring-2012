
$(function(){var funding=(function(){var fundingVals=[];$("#minFunding > option").each(function(){fundingVals.push(this.value);});return{getVals:function(key){return fundingVals[key];},getLength:function(){return fundingVals.length;}};}());var thisPage=$("#companyFilterForm").attr("action");$("#companyFilterForm").submit(function(e){e.preventDefault();data=$(this,e).serialize();if($('INPUT[name=industry]').length>0){industryParams=data.match(/industry=([^&]*)/gi);if(industryParams==null||industryParams.length==0){data=data+"&industry=";}else if($('INPUT[name=industry]').length==industryParams.length){data=data.replace(/&industry=[^&]+/g,'');}}
if($('INPUT[name=unknown_funding]:checked').length>0){data=data;}
if($('input[name=minWatchers]:checked').val()=='all'){data=data.replace(/&minWatchers=[^&]+/g,'');}
if($('#minFunding option:selected').val()==funding.getVals(0)){data=data.replace(/&minFunding=[^&]+/g,'');}
if($('#maxFunding option:selected').val()==funding.getVals(funding.getLength()-1)){data=data.replace(/&maxFunding=[^&]+/g,'');}
if($('#country').val()=='all'){data=data.replace(/&country=[^&]+/g,'');}
if($("#stateFilterWrapper")){if($('#country').length>0){if($('#country').val()=='US'){$("#stateFilterWrapper").show()}else{$("#stateFilterWrapper").hide()
data=data.replace(/&state=[^&]+/g,'')}}}
location.href=thisPage+'#!'+data;});$("#companyFilterForm INPUT[type=checkbox], #companyFilterForm INPUT[type=radio], #companyFilterForm SELECT").change(function(el){$("#companyFilterForm").trigger('submit');});$('#companyFilterClear').click(function(e){e.preventDefault();if(location.hash!=''&&location.hash!='#!'){location.href=thisPage+'#!';}else{$(window).trigger('hashchange');}});$('#sortBy').change(function(e){query=decodeURI($.param.fragment().substring(1));search=query.match(/search=([^&]*)/i);type=query.match(/type=([^&]*)/i);var hash='';if(search){hash+='search='+search[1];if(type)hash+='&';}
if(type)hash+='type='+type[1];if(hash.length)hash+='&';if($(this,e).val()!='name')hash+='sort='+$(this,e).val();location.href=thisPage+'#!'+hash;});$('#sm-main-content').delegate('.sm-request-company','click',function(e){requestNewCompany($(this,e),e);});$(window).bind('hashchange',function(e){query=decodeURI($.param.fragment().substring(1));search=query.match(/search=([^&]*)/i);type=query.match(/type=([^&]*)/i);industry=query.match(/industry=([^&]*)/gi);country=query.match(/country=([^&]*)/gi);watchers=query.match(/watchers=([^&]*)/i);sortType=query.match(/sort=([^&]*)/i);state=query.match(/state=([^&]*)/i);if(search){$('#companyFilterSearchString').val(URLDecode(search[1]));}else{$('#companyFilterSearchString').val("");}
if(type){$('#companyFilterType').val(type[1]);}else{$('#companyFilterType').val("name");}
if(industry&&industry.length!=$('input[name=industry]').length){$('input[name=industry]').removeAttr('checked');for(i=0;i<industry.length;i++){ind=industry[i].split('=');$('INPUT[value="'+unescape(ind[1]).replace(/\+/g,' ')+'"]').attr('checked','checked');}}else{$('input[name=industry]').attr('checked','checked');}
if(watchers){$('INPUT[name=watchers]').each(function(el){if($(this,el).val()==watchers[1]){$(this,el).attr('checked','checked');}})}else{$('#filter_number_of_watchers_all').attr('checked','checked');}
if(country){country=country[0].split('=');$('#country').val(country[1]);}else{$('#country').val('all');}
if($("#stateFilterWrapper")){if($('#country').length>0){if($('#country').val()=='US'){$("#stateFilterWrapper").show()}else{$("#stateFilterWrapper").hide()}}}
if(state){state=state[0].split('=');$('#state').val(state[1]);}else{$('#state').val('all');}
if(sortType&&sortType[1]=='industry'){$('#sortBy').val('industry');}else{$('#sortBy').val('name');}});$('FIELDSET.sm-expand-menu div A').click(function(e){e.preventDefault();if($(this,e).parent().hasClass('open')){$(this,e).parent().removeClass('open').addClass('closed');}else{$(this,e).parent().removeClass('closed').addClass('open');}
$(this,e).closest('FIELDSET').find('UL').slideToggle(500);});$('.sm-select-all').click(function(e){e.preventDefault();container=$(this,e).parent().parent();$('INPUT',container).attr('checked','checked');$('INPUT:first',container).trigger('change');});$('.sm-select-none').click(function(e){e.preventDefault();container=$(this,e).parent().parent();$('INPUT',container).removeAttr('checked');$('INPUT:first',container).trigger('change');});$('.sm-only').click(function(e){e.preventDefault();selected=$(this,e).parent().parent();container=selected.parent();$('INPUT',container).removeAttr('checked');$('INPUT',selected).attr('checked','checked').trigger('change');});$('<div id="funding-txt-container"></div>').appendTo('#funding-container');$('<span id="minFundingTxt" class="sm-pls sm-l"></span>').appendTo('#funding-txt-container');$('<span id="maxFundingTxt" class="sm-prs sm-r"></span>').appendTo('#funding-txt-container');$('<div class="sm-slide-container"><div id="funding-slider" class="slider sm-c"></div></div>').appendTo('#funding-container');$('.slider').slider({range:true,values:[0,funding.getLength()-1],min:0,max:funding.getLength()-1,slide:function(event,ui){if(ui.values[0]==ui.values[1])
return false;newMinValue=funding.getVals(ui.values[0]);newMaxValue=funding.getVals(ui.values[1]);$('#minFunding').val(newMinValue);$('#maxFunding').val(newMaxValue);$('#minFundingTxt').text($('#minFunding option:selected').text());$('#maxFundingTxt').text($('#maxFunding option:selected').text());},change:function(event,ui){$("#companyFilterForm").trigger('submit');}});$('#minFundingTxt').text($('#minFunding option:selected').text());$('#maxFundingTxt').text($('#maxFunding option:selected').text());});