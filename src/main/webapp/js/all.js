$(document).ready(function () {
    $('BODY').delegate('.jsGroupedCheckboxListGroupCheckbox', 'click', function (e) {
        hgFunctions.groupedCheckboxList.groupCheckboxClicked(e);
    });
    $('BODY').delegate('.jsGroupedCheckboxListCheckbox', 'click', function (e) {
        hgFunctions.groupedCheckboxList.checkboxClicked(e);
    });
    $('BODY').delegate('.jsIgnoreClick', 'click', function (e) {
        e.preventDefault();
    });
    $('BODY').ajaxSuccess(function (event, XMLHttpRequest, ajaxOptions) {
        hgFunctions.util.addTipsy();
    });
    $('BODY').delegate('.jsTrackEvent', 'click', function (e) {
        hgFunctions.analytics.trackClick(e);
    });
});
$(function () {
    $('BODY').ajaxError(function (event, XMLHttpRequest, ajaxOptions, thrownError) {
        if (!displayAjaxError || suppressAllErrors) {
            if (!displayAjaxError) {
                displayAjaxError = true;
            }
        } else {
            if (thrownError == 'Bad Request') {
                $('.bbq-loading').hide();
                purrMessage("Please do not enter invalid characters.", 1500);
            } else if (thrownError.indexOf('Invalid JSON:') >= 0 && thrownError.indexOf('migrated') >= 0) {
                window.location.href = window.location.href;
            } else {
                if (thrownError.indexOf('Unauthorized') >= 0) {
                    suppressAllErrors = true;
                    purrMessage('You must be logged in to view the content you requested. Please <a href="' + contextPath + 'login">log in here</a>.');
                } else {
                    purrMessage(_MESSAGE["default-ajax-error"], 1500);
                }
            }
        }
    });
    if ($.browser.msie && parseInt($.browser.version) < 7) {
        $("#sm-navigation li").hover(function () {
            $(this).addClass("sf");
        }, function () {
            $(this).removeClass("sf");
        });
    }
    if (typeof $().tipsy != 'undefined') {
        $('.sm-tipsy a').tipsy({
            gravity: 's',
            fade: false
        });
    }
    addTimeago();
    $('FORM').submit(function (e) {
        $('BUTTON:not(.sm-allow-double-submit), INPUT[type=submit]:not(.sm-allow-double-submit)', $(this, e)).attr('disabled', 'disabled');
    });
    if ($("#searchBar").length != 0) {
        $("#searchBar").attr("value", _MESSAGE["default-search-text"]);
        $("#searchBar").focus(function () {
            $(this).addClass("active");
            if ($(this).attr("value") == _MESSAGE["default-search-text"]) {
                $(this).attr("value", "");
            }
        });
        $("#searchBar").blur(function () {
            $(this).removeClass("active");
            if ($(this).attr("value") == "") {
                $(this).attr("value", _MESSAGE["default-search-text"]);
            }
        });
        $('#searchBar').autocomplete({
            source: function (request, response) {
                $.ajax({
                    url: _URL["search-autocomplete"],
                    dataType: "json",
                    data: {
                        q: request.term.toLowerCase()
                    },
                    success: function (jsonResponse) {
                        response($.map(jsonResponse, function (item) {
                            return item;
                        }));
                    },
                    error: function (er) {
                        displayAjaxError = false;
                    }
                })
            },
            select: function (event, ui) {
                if (ui.item) {
                    window.location = ui.item.url;
                }
            },
            delay: 80,
            minLength: 1
        }).data("autocomplete")._renderItem = function (ul, item) {
            ul.css('zIndex', 6);
            return $("<li></li>").data("item.autocomplete", item).append('<a class="sm-result clearfix"><img src="' + item.image + '" />' + item.label + '<br />' + titleCaps(item.type) + '</a>').appendTo(ul);
        };
    }
    $(".sm-search").submit(function (e) {
        e.preventDefault();
        search = ($('#searchBar').val() == _MESSAGE["default-search-text"]) ? "" : $('#searchBar').val();
        location.href = $(this, e).attr('action') + '#!search=' + search;
    });
    hashlink = $("A.hashlink, .hashlink A");
    if (hashlink.length) {
        hashlink.each(function (el) {
            $(this, el).attr('href', $(this, el).attr('href').replace("?", "#!"));
        });
    }
    attachCardClicks();
    $(".print-button").click(function (p) {
        p.preventDefault();
        window.print();
    });
    $(".back-button").click(function (e) {
        e.preventDefault();
        if (e.currentTarget != undefined && e.currentTarget.getAttribute("href")) {
            window.location = e.currentTarget.getAttribute("href");
        } else {
            history.go(-1);
        }
    });
    $('.close-window').click(function (e) {
        window.close();
    });
    $('#errorBox .close').click(function (e) {
        e.preventDefault();
        errors.close();
    });
    errors = $("#errorBox").overlay({
        api: true,
        mask: {
            color: '#000',
            loadSpeed: 200,
            opacity: 0.42
        },
        closeOnClick: true,
        fixed: false,
        load: false
    });
    $('.lightbox-link').click(function (el) {
        el.preventDefault();
        link = getContextualAjaxUrl($(this, el).attr('href'));
        $.getJSON(link, function (data) {
            message(data.html.toString(), "error");
            trackAjaxPage(link);
        });
    });
    $('A[href^="(http|https)"]').click(function (el) {
        sameDomain = window.location.hostname;
        url = $(this, el).attr('href').toLowerCase();
        if (url.indexOf("localhost") < 0 && url.indexOf("ent1.qa.prv") < 0 && url.indexOf(sameDomain) < 0) {
            if ($(this, el).attr("target") != '_blank') {
                $(this, el).attr("target", "_blank");
            }
            recordOutboundLink($(this, el).attr('href'), "Outbound Link", $(this, el).attr('href'));
            return true;
        }
    });
    $('.ajaxLightbox').click(function (event) {
        event.preventDefault();
        if (typeof $(this).attr('disabled') != 'undefined' && $(this).attr('disabled') == 'disabled') {
            return;
        }
        var href = $(this).attr('href');
        href = getContextualAjaxUrl(href);
        $.getJSON(href, function (data) {
            smLightbox = $.fn.makeOverlay('lightBox');
            smLightbox.getOverlay().find('.select-content').html(data.html[0].toString());
            smLightbox.load();
            if (typeof data.scripts != "undefined" && data.scripts.length > 0) {
                for (i = 0; i < data.scripts.length; i++) {
                    $.include(data.scripts[i]);
                }
            }
        });
    });
    $('.ajaxLightboxHtml').live('click', function (event) {
        event.preventDefault();
        if (typeof $(this).attr('disabled') != 'undefined' && $(this).attr('disabled') == 'disabled') {
            return;
        }
        var href = $(this).attr('href');
        href = getContextualAjaxUrl(href);
        $.get(href, function (html) {
            smLightbox = $.fn.makeOverlay('lightBox');
            smLightbox.getOverlay().find('.select-content').html(html);
            smLightbox.load();
        });
    });
    $('.ajaxLightboxClose').live('click', function (e) {
        e.preventDefault();
        smLightbox.close();
    });
    $('.ajaxHtmlLightbox').click(function (event) {
        event.preventDefault();
        if (typeof $(this).attr('disabled') != 'undefined' && $(this).attr('disabled') == 'disabled') {
            return;
        }
        var href = $(this).attr('href');
        href = getContextualAjaxUrl(href);
        $.get(href, function (data) {
            smLightbox = $.fn.makeOverlay('lightBox');
            smLightbox.getOverlay().find('.select-content').html(data.toString());
            smLightbox.load();
        });
    });
    breakOutOfIframe();
    $('.sm-datepicker').datepicker();
    if (!jQuery.support.placeholder) {
        $('.sm-datepicker').change(function (e) {
            $(this, e).removeClass('hasPlaceholder');
        });
    }
    $('.sm-cal-icon').click(function (el) {
        $(this, el).parent().find('INPUT').focus();
    });
    $('.sm-readonly-edit-link').click(function (el) {
        var id = $(this).attr('rel');
        $('#' + id).removeAttr("readonly");
        $('#' + id).removeClass("sm-inactive-input");
        $('#' + id).val("");
        $('#' + id).focus();
        $('#' + id + "_edit_link").hide('slow', function () {});
    });
    $('.expander').expander();
});
document.write('<style type="text/css">.sm-pre-timestamp{visibility:hidden}.sm-option-details-link{display:inline}.sm-option-details{display:none}</style>');

function breakOutOfIframe() {
    if (top !== self && (typeof allowIframe === 'undefined') && (top.location.href.strContains("secondmarket.com") || top.location.href.strContains("192.168.1") || top.location.href.strContains("localhost"))) {
        top.location.href = document.location.href;
        document.write('<style type="text/css">BODY{display:none}</style>');
    }
}
var errors = new Object();

function message(msg, type, width) {
    if (countProperties(triggers) > 0) {
        triggers.close();
    }
    if (typeof width == 'undefined') {
        width = 500;
    }
    $("#errorBox").css("width", width)
    $("#errorBox .error-content").html(msg);
    errors.load();
}

function updateAlert(id, value) {
    parent = $('#' + id).parent().parent();
    subAlert = $('#' + id + ' .sm-alert');
    mainAlert = $('#' + id + ' .sm-bubble')
    bRemoveAlert = (value != null && value.length > 0 && value != 0 && value != '0') ? false : true;
    if (subAlert.length > 0) {
        if (bRemoveAlert) {
            subAlert.hide().remove();
        } else {
            subAlert.html('(' + value + ')');
        }
        if (parent.parent().attr('id') == 'sm-navigation') {
            updateAlert(parent.attr('id'), parent.html() - value);
        }
        updateAlert(id.replace('nav_', 'tab_'), value);
    } else if (mainAlert.length > 0) {
        if (bRemoveAlert) {
            mainAlert.css('top', 0).hide().remove();
        } else {
            mainAlert.html(value);
        }
    }
}

function urlToHash(url) {
    slashPos = url.lastIndexOf("/");
    return url.substring(0, slashPos) + "#!" + url.substring(slashPos + 1);
}
String.prototype.strContains = function (substring) {
    return (this.toLowerCase().indexOf(substring.toLowerCase()) >= 0);
}
var _aQS = new Object();

function getParam(key) {
    if (typeof _aQS._set == 'undefined') {
        qs = window.location.search.substring(1);
        aqs = qs.split("&");
        for (i = 0; i < aqs.length; i++) {
            temp = aqs[i].split("=");
            _aQS[temp[0]] = (typeof temp[1] == 'undefined') ? "" : temp[1];
        }
        _aQS['_set'] = true;
    }
    if (typeof _aQS[key] != 'undefined') {
        return _aQS[key];
    } else {
        return false;
    }
}
var triggers = new Object();

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

function countProperties(obj) {
    var count = 0;
    for (var prop in obj) {
        if (obj.hasOwnProperty(prop)) {
            ++count;
        }
    }
    return count;
}

function printObject(obj, level) {
    str = '';
    if (typeof level == 'undefined') {
        level = 0;
    }
    if (typeof indent == "undefined") {
        indent = new Array();
    }
    indent[level] = "";
    for (i = 0; i < level; i++) {
        indent[level] += "   ";
    }
    for (var prop in obj) {
        if (level < 1) {
            if (typeof obj[prop] == "array" || typeof obj[prop] == "object") {
                str += indent[level] + prop + ': {' + "\n" + printObject(obj[prop], (level + 1)) + "}\n";
            } else {
                str += indent[level] + prop + ': ' + obj[prop] + ",\n";
            }
        }
    }
    return str;
}

function objLength(obj) {
    var i = 0;
    for (var prop in obj) {
        i++;
    }
    return i;
}

function getContextualAjaxUrl(originalUrl) {
    if (originalUrl.substring(0, 4).toLowerCase() == 'http') {
        startPos = 10 + originalUrl.substring(10).indexOf('/');
        tempUrl = originalUrl.substring(startPos);
    } else {
        tempUrl = originalUrl;
    }
    cpLength = contextPath.length;
    if (tempUrl.substring(0, cpLength) == contextPath) {
        //returnUrl = contextPath + "m/" + tempUrl.substring(cpLength)
        returnUrl = contextPath + "/" + tempUrl.substring(cpLength)
    } else {
        //returnUrl = "/m" + tempUrl;
        returnUrl = "/" + tempUrl;
    }
    return returnUrl;
}

function disallowZ() {
    var disableZ = new Array("iphone", "ipod", "series60", "symbian", "android", "windows ce", "blackberry", "palm", "msie 6");
    var uagent = navigator.userAgent.toLowerCase();
    for (i = 0; i < disableZ.length; i++) {
        if (uagent.indexOf(disableZ[i]) >= 0) {
            return true;
        }
    }
    return false;
}

function trackAjaxPage(url) {
    //realUrl = url.replace("/m/", "/");
    _gaq.push(['_trackPageview', realUrl]);
}

function trackEvent(url) {
    gurl = "/tracking/" + url;
    _gaq.push(['_trackPageview', gurl]);
}

function recordOutboundLink(link, category, action) {
    _gaq.push(['_trackEvent', category, action]);
}

function isNumeric(input) {
    return (input - 0) == input && input.length > 0;
}

function isNumericPositive(input) {
    return (input - 0) == input && input.length > 0 && input >= 0;
}

function formatCurrency(num) {
    num = isNaN(num) || num === '' || num === null ? 0.00 : num;
    return roundNumber(parseFloat(num), 2).toFixed(2);
}

function formatDisplayNumber(nStr) {
    x = nStr.toString().split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
}

function roundNumber(numberToBeRounded, precision) {
    var result = Math.round(numberToBeRounded * Math.pow(10, precision)) / Math.pow(10, precision);
    return result;
}

function getDollarDisplay(value) {
    return '$' + formatDisplayNumber(formatCurrency(value));
}

function addTimeago() {
    $(".sm-pre-timestamp").timeago();
    $(".sm-pre-timestamp").addClass('sm-timestamp').removeClass("sm-pre-timestamp");
}

function purrMessage(message, removeSeconds) {
    notice = '<div class="error sm-purr">' + message + '</div>';
    if (typeof removeSeconds == 'undefined') {
        $(notice).purr({
            isSticky: true
        });
    } else {
        $(notice).purr({
            removeTimer: removeSeconds
        });
    }
}

function attachCardClicks() {
    $('.sm-card-link').closest('.sm-card').each(function (e) {
        if (!$(this, e).hasClass('sm-click-card')) {
            $(this, e).addClass('sm-click-card').addClass('sm-new-click');
        }
    });
    $('.sm-new-click').click(function (e) {
        click = e.target.tagName.toUpperCase();
        if (click == 'TD' || click == 'TR' || click == 'TABLE' || click == 'DIV') {
            card = $(this, e);
            href = $('.sm-card-link', card).attr('href');
            if (typeof href != 'undefined' && href.length > 0) window.location = href;
        }
    }).removeClass('sm-new-click');
    $('.sm-option-details-link:not(.click-added)').click(function (e) {
        tBody = $(this, e).closest('tbody');
        toggleElements = tBody.find('.sm-option-details');
        toggleElements.toggle();
        alttext = $(this, e).attr('alttext');
        currentText = $(this, e).html();
        $(this, e).attr('alttext', currentText);
        $(this, e).html(alttext);
    }).addClass('click-added');
}

function requestNewCompany(requestCompanyLink, e) {
    e.preventDefault();
    href = requestCompanyLink.attr('href');
    rel = requestCompanyLink.attr('rel');
    container = $.fn.makeOverlay(rel);
    $.get(getContextualAjaxUrl(href), function (response) {
        container.getOverlay().find('.select-content').html(response);
        $('.sm-cancel, .close').click(function (el) {
            el.preventDefault();
            container.close();
            requestCompanyLink = null;
        });
        addRequestNewCompanySubmitHandler(container.getOverlay().find('.select-content'));
        container.load();
    });
}

function addRequestNewCompanySubmitHandler(container) {
    $('FORM', $(container)).submit(function (e) {
        postedData = $(this, e).serialize();
        e.preventDefault();
        $.post(getContextualAjaxUrl($(this, e).getAction()), postedData, function (data) {
            if (typeof data.json != 'undefined' && data.json.code.toLowerCase() == 'success') {
                $(container).html(data.json.message);
            } else {
                $(container).html(data);
                addRequestNewCompanySubmitHandler(container);
            }
        });
    });
}

/*
function SMLogger(message){if(window.location.href.toLowerCase().indexOf('www.')<0&&typeof console!='undefined'&&typeof console.log!='undefined'){console.log(message);}}
jQuery.support.placeholder=false;test=document.createElement('input');if('placeholder'in test){jQuery.support.placeholder=true;}
$(function(){if(!$.support.placeholder){var active=document.activeElement;$('INPUT:text[placeholder]').focus(function(){if($(this).attr('placeholder')!=''&&($(this).val()==""||$(this).val()==$(this).attr('placeholder'))){$(this).val('').removeClass('hasPlaceholder');}}).blur(function(){if($(this).attr('placeholder')!=''&&($(this).val()==''||$(this).val()==$(this).attr('placeholder'))){$(this).val($(this).attr('placeholder')).addClass('hasPlaceholder');}});$('INPUT:text[placeholder]').blur();$(active).focus();$('form').submit(function(){$(this).find('.hasPlaceholder').each(function(){$(this).val('');});});}});breakOutOfIframe();function URLDecode(psEncodeString)
{var lsRegExp=/\+/g;return unescape(String(psEncodeString).replace(lsRegExp," "));}
var IE6=false;var _URL=new Array();var displayAjaxError=true;var suppressAllErrors=false;_URL["validation"]=getContextualAjaxUrl(contextPath+"backend/validation/");_URL["search-proxy"]=getContextualAjaxUrl(contextPath+"search-proxy/");_URL["search-autocomplete"]=getContextualAjaxUrl(contextPath+"search-autocomplete/");_URL["company-assets"]=getContextualAjaxUrl(contextPath+"company/[issuerSmid]/assets");_URL["organization-autocomplete"]=getContextualAjaxUrl(contextPath+"organization-autocomplete");var _MESSAGE=new Array();_MESSAGE["default-search-text"]="Search SecondMarket...";_MESSAGE["default-ajax-error"]="The attempted action failed due to an error on the server";_MESSAGE["no-assets"]="We are unable to accept bids, holdings and listings for this issuer at this time. Please try another issuer";function addressFormToggleState(countrySelect){var addressFormContainer=countrySelect.closest(".addressFormContainer");var inputSelectWrapper=$('.addressFormStateSelectWrapper',addressFormContainer);var inputSelect=$('SELECT',inputSelectWrapper);var inputTextWrapper=$('.addressFormStateTextWrapper',addressFormContainer);var inputText=$('INPUT',inputTextWrapper);if(countrySelect.val()!=undefined&&countrySelect.val()=='US'){inputTextWrapper.hide();inputSelectWrapper.show();inputText.val('');inputText.disable();inputSelect.enable();}else{inputSelectWrapper.hide();inputTextWrapper.show();inputSelect.val('');inputSelect.disable();inputText.enable();}}
*/

function addressFormToggleOnLoad() {
    $('.addressFormContainer').each(function (i, addressFormContainer) {
        var countrySelect = $('.addressFormCountrySelectField', addressFormContainer);
        addressFormToggleState(countrySelect);
    });
}
$(function () {
    addressFormToggleOnLoad();
    $('.addressFormCountrySelectField').change(function () {
        var countrySelect = $(this);
        addressFormToggleState(countrySelect);
    });
});
var hgFunctions = function () {
        var constants = {
            AJAX_PREFIX: 'm/'
        };
        return {
            analytics: {
                trackEvent: function (url) {
                    gurl = '/tracking/' + url;
                    _gaq.push(['_trackPageview', gurl]);
                },
                trackClick: function (e) {
                    var element = $(e.target);
                    var event = element.attr('event');
                    if (event == undefined) {
                        event = element.parent().attr('event');
                    }
                    if (event != undefined) {
                        hgFunctions.analytics.trackEvent(event);
                    }
                }
            },
            util: {
                getIdFromEvent: function (event) {
                    return hgFunctions.util.getIdFromElement($(event.target));
                },
                getIdFromElement: function (element) {
                    var clickedTargetID = element.attr('id');
                    if (clickedTargetID == undefined) {
                        clickedTargetID = element.parent().attr('id');
                    }
                    clickedTargetIDName = clickedTargetID.split('-', 1) + '-';
                    targetID = clickedTargetID.replace(clickedTargetIDName, '');
                    return targetID;
                },
                getAjaxLinkFromEvent: function (event) {
                    var link = $(event.target);
                    var href = link.attr('href');
                    var url = hgFunctions.util.prependAjaxPrefixToUrl(href);
                    return url;
                },
                prependAjaxPrefixToUrl: function (originalUrl) {
                    if (originalUrl.substring(0, 4).toLowerCase() == 'http') {
                        startPos = 10 + originalUrl.substring(10).indexOf('/');
                        tempUrl = originalUrl.substring(startPos);
                    } else {
                        tempUrl = originalUrl;
                    }
                    cpLength = contextPath.length;
                    returnUrl = contextPath + constants.AJAX_PREFIX + tempUrl.substring(cpLength);
                    return returnUrl;
                },
                addTipsy: function () {
                    $('.js-needs-tipsy-s:not(".js-has-tipsy")').tipsy({
                        'gravity': 's'
                    });
                    $('.js-needs-tipsy-s').addClass('js-has-tipsy').removeClass('js-needs-tipsy-s');
                    $('.js-needs-tipsy:not(".js-has-tipsy")').tipsy({
                        title: function () {
                            var rel = $(this).attr('rel');
                            if (rel != undefined) {
                                hgFunctions.analytics.trackEvent(rel);
                            }
                            return $(this).attr('original-title');
                        }
                    });
                    $('.js-needs-tipsy').addClass('js-has-tipsy').removeClass('js-needs-tipsy');
                }
            },
            groupedCheckboxList: {
                groupCheckboxClicked: function (e) {
                    var id = hgFunctions.util.getIdFromEvent(e);
                    var checked = $(e.target).attr('checked');
                    if (checked) {
                        $('.jsGroupedCheckboxListCheckbox-' + id).attr('checked', 'checked');
                    } else {
                        $('.jsGroupedCheckboxListCheckbox-' + id).removeAttr('checked');
                    }
                },
                checkboxClicked: function (e) {
                    var id = hgFunctions.util.getIdFromEvent(e);
                    var groupId = id.split('-')[0];
                    hgFunctions.groupedCheckboxList.updateGroupCheckbox(groupId);
                },
                updateGroupCheckbox: function (groupId) {
                    var totalInGroup = $('.jsGroupedCheckboxListCheckbox-' + groupId).length;
                    var selectedInGroup = 0;
                    $('.jsGroupedCheckboxListCheckbox-' + groupId).each(function (index, value) {
                        if ($(value).attr('checked')) {
                            selectedInGroup++;
                        }
                    });
                    if (selectedInGroup == totalInGroup) {
                        $('#jsGroupedCheckboxListGroupCheckbox-' + groupId).attr('checked', 'checked');
                    } else {
                        $('#jsGroupedCheckboxListGroupCheckbox-' + groupId).removeAttr('checked');
                    }
                },
                updateAllGroupCheckboxes: function () {
                    $('.jsGroupedCheckboxListGroupCheckbox').each(function (index, value) {
                        var groupId = hgFunctions.util.getIdFromElement($(value));
                        hgFunctions.groupedCheckboxList.updateGroupCheckbox(groupId);
                    });
                }
            }
        }
    }();
$.fn.extend({
    getAction: function () {
        return $(this).attr('action');
    },
    getId: function () {
        return $(this).attr('id');
    },
    hasId: function (id) {
        return id ? $(this).getId() == id : false;
    },
    disable: function () {
        return $(this).attr('disabled', 'disabled');
    },
    enable: function () {
        return $(this).removeAttr('disabled');
    },
    getHref: function () {
        return $(this).attr('href');
    },
    nextTd: function () {
        return $(this).closest('td').next();
    },
    prevTd: function () {
        return $(this).closest('td').prev();
    },
    makeOverlay: function (id, options) {
        var container;
        var defaultOptions = {
            containerClassName: 'sm-tal sm-overlay',
            onBeforeClose: function (event) {
                if (typeof event.originalEvent != 'undefined' && typeof event.originalEvent.preventDefault != 'undefined') {
                    event.originalEvent.preventDefault();
                }
            },
            onClose: function (event) {},
            onBeforeLoad: function (event) {},
            onLoad: function (event) {}
        };
        if (typeof options === 'undefined') {
            options = defaultOptions;
        } else {
            options = $.extend(defaultOptions, options);
        }
        if ($('#' + id).length == 0) {
            container = $('<div/>', {
                id: id,
                className: options.containerClassName,
                style: 'display:none'
            }).append($('<span/>', {
                className: 'close close-overlay'
            })).append($('<div/>', {
                className: 'select-content'
            })).appendTo('BODY');
        } else {
            container = $('#' + id);
        }
        var overlay = container.overlay($.extend({
            api: true,
            top: 100,
            mask: {
                color: '#000',
                loadSpeed: 200,
                opacity: 0.42
            },
            closeOnClick: true,
            load: false,
            fixed: false
        }, options));
        container.delegate('.sm-cancel', 'click', function (e) {
            e.preventDefault();
            overlay.close();
        });
        return overlay;
    },
    selectAll: function (checkboxes, options) {
        if (typeof options === 'undefined') {
            options = {
                onCheck: function (element) {},
                onUnCheck: function (element) {}
            };
        }
        $(this).delegate(checkboxes, 'change', function (e) {
            if ($(this, e).val() == 'all') {
                allCheckboxes = $(this, e).closest('fieldset').find(':checkbox');
                allCheckboxes.attr('checked', $(this, e).attr('checked'));
                if ($(this, e).is(':checked')) {
                    options.onCheck(allCheckboxes);
                } else {
                    options.onUnCheck(allCheckboxes);
                }
            } else {
                if ($(this, e).closest('fieldset').find('INPUT[value=all]').is(':checked') && !$(this, e).is(':checked')) {
                    $(this).closest('fieldset').find('INPUT[value=all]').attr('checked', false);
                }
                if ($(this, e).is(':checked')) {
                    var flag = true;
                    $(this, e).closest('fieldset').find('INPUT[value!=all]').each(function (f) {
                        if (!$(this, f).is(':checked')) {
                            flag = false;
                        }
                    });
                    $(this, e).closest('fieldset').find('INPUT[value=all]').attr('checked', flag);
                }
                if ($(this, e).is(':checked')) {
                    options.onCheck($(this, e));
                } else {
                    options.onUnCheck($(this, e));
                }
            }
        });
    },
    getOverlayContent: function (overlay) {
        if (typeof overlay != 'undefined' && typeof overlay.getOverlay() != 'undefined') {
            return overlay.getOverlay().find('.select-content');
        }
        return null;
    },
    expander: function (container, expandedClass, collapsedClass) {
        $(this).each(function (el) {
            $(this, el).addExpandElement(container, expandedClass, collapsedClass);
        });
    },
    addExpandElement: function (container, expandedClass, collapsedClass) {
        var header = $(this);
        var expander, collapser;
        var expand, collapse;
        var getHeaderText = function () {
                returnText = initialHeaderText;
                if (typeof header.attr('alttext') !== "undefined" && header.attr('alttext') != undefined && header.attr('alttext') != '') {
                    returnText = header.attr('alttext');
                    header.attr('alttext', header.text());
                }
                return returnText;
            }
        if (typeof header != 'undefined') {
            var initialHeaderText = header.html();
            header.css('cursor', 'pointer');
            if (typeof container === 'undefined') {
                container = $('~ .expander-content', header);
            }
            if ((typeof expandedClass == 'undefined') || (typeof collapsedClass == 'undefined')) {
                expander = '&#x25BA;&nbsp;';
                collapser = '&#x25BC;&nbsp;';
            } else {
                expander = '<span class="sm-l "' + collapsedClass + '></span>';
                collapser = '<span class="sm-l "' + expandedClass + '></span>';
            }
            expand = function () {
                header.html(collapser + getHeaderText());
                container.show();
            };
            collapse = function () {
                header.html(expander + getHeaderText());
                container.hide();
            };
            if (container.css('display') == 'none' || container.hasClass('hide') || container.hasClass('js-hide')) {
                header.html(expander + initialHeaderText);
                header.toggle(expand, collapse);
            } else {
                header.html(collapser + initialHeaderText);
                header.toggle(collapse, expand);
            }
        }
    }
});
if (jQuery.browser.mozilla || jQuery.browser.opera) {
    document.removeEventListener("DOMContentLoaded", jQuery.ready, false);
    document.addEventListener("DOMContentLoaded", function () {
        jQuery.ready();
    }, false);
}
jQuery.event.remove(window, "load", jQuery.ready);
jQuery.event.add(window, "load", function () {
    jQuery.ready();
});
jQuery.extend({
    includeStates: {},
    include: function (url, callback, dependency) {
        if (typeof callback != 'function' && !dependency) {
            dependency = callback;
            callback = null;
        }
        url = url.replace('\n', '');
        jQuery.includeStates[url] = false;
        var script = document.createElement('script');
        script.type = 'text/javascript';
        script.onload = function () {
            jQuery.includeStates[url] = true;
            if (callback) callback.call(script);
        };
        script.onreadystatechange = function () {
            if (this.readyState != "complete" && this.readyState != "loaded") return;
            jQuery.includeStates[url] = true;
            if (callback) callback.call(script);
        };
        script.src = url;
        if (dependency) {
            if (dependency.constructor != Array) dependency = [dependency];
            setTimeout(function () {
                var valid = true;
                $.each(dependency, function (k, v) {
                    if (!v()) {
                        valid = false;
                        return false;
                    }
                })
                if (valid) document.getElementsByTagName('head')[0].appendChild(script);
                else setTimeout(arguments.callee, 10);
            }, 10);
        } else document.getElementsByTagName('head')[0].appendChild(script);
        return function () {
            return jQuery.includeStates[url];
        }
    },
    readyOld: jQuery.ready,
    ready: function () {
        if (jQuery.isReady) return;
        imReady = true;
        $.each(jQuery.includeStates, function (url, state) {
            if (!state) return imReady = false;
        });
        if (imReady) {
            jQuery.readyOld.apply(jQuery, arguments);
        } else {
            setTimeout(arguments.callee, 10);
        }
    }
});
(function ($) {
    $.purr = function (notice, options) {
        notice = $(notice);
        if (!options.isSticky) {
            notice.addClass('not-sticky');
        };
        var cont = document.getElementById('purr-container');
        if (!cont) {
            cont = '<div id="purr-container"></div>';
        }
        cont = $(cont);
        $('body').append(cont);
        notify();

        function notify() {
            var close = document.createElement('a');
            $(close).attr({
                className: 'close',
                href: '#close',
                innerHTML: 'Close'
            }).appendTo(notice).click(function () {
                removeNotice();
                return false;
            });
            notice.appendTo(cont).hide();
            if (jQuery.browser.msie && options.usingTransparentPNG) {
                notice.show();
            } else {
                notice.fadeIn(options.fadeInSpeed);
            }
            if (!options.isSticky) {
                var topSpotInt = setInterval(function () {
                    if (notice.prevAll('.not-sticky').length == 0) {
                        clearInterval(topSpotInt);
                        setTimeout(function () {
                            removeNotice();
                        }, options.removeTimer);
                    }
                }, 200);
            }
        }

        function removeNotice() {
            if (jQuery.browser.msie && options.usingTransparentPNG) {
                notice.css({
                    opacity: 0
                }).animate({
                    height: '0px'
                }, {
                    duration: options.fadeOutSpeed,
                    complete: function () {
                        notice.remove();
                    }
                });
            } else {
                notice.animate({
                    opacity: '0'
                }, {
                    duration: options.fadeOutSpeed,
                    complete: function () {
                        notice.animate({
                            height: '0px'
                        }, {
                            duration: options.fadeOutSpeed,
                            complete: function () {
                                notice.remove();
                            }
                        });
                    }
                });
            }
        };
    };
    $.fn.purr = function (options) {
        options = options || {};
        options.fadeInSpeed = options.fadeInSpeed || 500;
        options.fadeOutSpeed = options.fadeOutSpeed || 500;
        options.removeTimer = options.removeTimer || 4000;
        options.isSticky = options.isSticky || false;
        options.usingTransparentPNG = options.usingTransparentPNG || false;
        this.each(function () {
            new $.purr(this, options);
        });
        return this;
    };
})(jQuery);
(function ($) {
    $.timeago = function (timestamp) {
        if (timestamp instanceof Date) {
            return inWords(timestamp);
        } else if (typeof timestamp === "string") {
            return inWords($.timeago.parse(timestamp));
        } else {
            return inWords($.timeago.datetime(timestamp));
        }
    };
    var $t = $.timeago;
    $.extend($.timeago, {
        settings: {
            refreshMillis: 60000,
            allowFuture: false,
            strings: {
                prefixAgo: null,
                prefixFromNow: null,
                suffixAgo: "ago",
                suffixFromNow: "from now",
                seconds: "less than a minute",
                minute: "about a minute",
                minutes: "%d minutes",
                hour: "about an hour",
                hours: "about %d hours",
                day: "a day",
                days: "%d days",
                month: "about a month",
                months: "%d months",
                year: "about a year",
                years: "%d years",
                numbers: []
            }
        },
        inWords: function (distanceMillis) {
            var $l = this.settings.strings;
            var prefix = $l.prefixAgo;
            var suffix = $l.suffixAgo;
            if (this.settings.allowFuture) {
                if (distanceMillis < 0) {
                    prefix = $l.prefixFromNow;
                    suffix = $l.suffixFromNow;
                }
                distanceMillis = Math.abs(distanceMillis);
            }
            var seconds = distanceMillis / 1000;
            var minutes = seconds / 60;
            var hours = minutes / 60;
            var days = hours / 24;
            var years = days / 365;

            function substitute(stringOrFunction, number) {
                var string = $.isFunction(stringOrFunction) ? stringOrFunction(number, distanceMillis) : stringOrFunction;
                var value = ($l.numbers && $l.numbers[number]) || number;
                return string.replace(/%d/i, value);
            }
            var words = seconds < 45 && substitute($l.seconds, Math.round(seconds)) || seconds < 90 && substitute($l.minute, 1) || minutes < 45 && substitute($l.minutes, Math.round(minutes)) || minutes < 90 && substitute($l.hour, 1) || hours < 24 && substitute($l.hours, Math.round(hours)) || hours < 48 && substitute($l.day, 1) || days < 30 && substitute($l.days, Math.floor(days)) || days < 60 && substitute($l.month, 1) || days < 365 && substitute($l.months, Math.floor(days / 30)) || years < 2 && substitute($l.year, 1) || substitute($l.years, Math.floor(years));
            return $.trim([prefix, words, suffix].join(" "));
        },
        parse: function (iso8601) {
            var s = $.trim(iso8601);
            s = s.replace(/\.\d\d\d+/, "");
            s = s.replace(/-/, "/").replace(/-/, "/");
            s = s.replace(/T/, " ").replace(/Z/, " UTC");
            s = s.replace(/([\+\-]\d\d)\:?(\d\d)/, " $1$2");
            return new Date(s);
        },
        datetime: function (elem) {
            var isTime = $(elem).get(0).tagName.toLowerCase() === "time";
            var iso8601 = isTime ? $(elem).attr("datetime") : $(elem).attr("title");
            return $t.parse(iso8601);
        }
    });
    $.fn.timeago = function () {
        var self = this;
        self.each(refresh);
        var $s = $t.settings;
        if ($s.refreshMillis > 0) {
            setInterval(function () {
                self.each(refresh);
            }, $s.refreshMillis);
        }
        return self;
    };

    function refresh() {
        var data = prepareData(this);
        if (!isNaN(data.datetime)) {
            $(this).text(inWords(data.datetime));
        }
        return this;
    }

    function prepareData(element) {
        element = $(element);
        if (!element.data("timeago")) {
            element.data("timeago", {
                datetime: $t.datetime(element)
            });
            var text = $.trim(element.text());
            if (text.length > 0) {
                element.attr("title", text);
            }
        }
        return element.data("timeago");
    }

    function inWords(date) {
        return $t.inWords(distance(date));
    }

    function distance(date) {
        return (new Date().getTime() - date.getTime());
    }
    document.createElement("abbr");
    document.createElement("time");
}(jQuery));
(function ($) {
    $.fn.tipsy = function (options) {
        options = $.extend({}, $.fn.tipsy.defaults, options);
        return this.each(function () {
            var opts = $.fn.tipsy.elementOptions(this, options);
            $(this).hover(function () {
                $.data(this, 'cancel.tipsy', true);
                var tip = $.data(this, 'active.tipsy');
                if (!tip) {
                    tip = $('<div class="tipsy"><div class="tipsy-inner"/></div>');
                    tip.css({
                        position: 'absolute',
                        zIndex: 100000
                    });
                    $.data(this, 'active.tipsy', tip);
                }
                if ($(this).attr('title') || typeof ($(this).attr('original-title')) != 'string') {
                    $(this).attr('original-title', $(this).attr('title') || '').removeAttr('title');
                }
                var title;
                if (typeof opts.title == 'string') {
                    title = $(this).attr(opts.title == 'title' ? 'original-title' : opts.title);
                } else if (typeof opts.title == 'function') {
                    title = opts.title.call(this);
                }
                tip.find('.tipsy-inner')[opts.html ? 'html' : 'text'](title || opts.fallback);
                var pos = $.extend({}, $(this).offset(), {
                    width: this.offsetWidth,
                    height: this.offsetHeight
                });
                tip.get(0).className = 'tipsy';
                tip.remove().css({
                    top: 0,
                    left: 0,
                    visibility: 'hidden',
                    display: 'block'
                }).appendTo(document.body);
                var actualWidth = tip[0].offsetWidth,
                    actualHeight = tip[0].offsetHeight;
                var gravity = (typeof opts.gravity == 'function') ? opts.gravity.call(this) : opts.gravity;
                switch (gravity.charAt(0)) {
                case 'n':
                    tip.css({
                        top: pos.top + pos.height,
                        left: pos.left + pos.width / 2 - actualWidth / 2
                    }).addClass('tipsy-north');
                    break;
                case 's':
                    tip.css({
                        top: pos.top - actualHeight,
                        left: pos.left + pos.width / 2 - actualWidth / 2
                    }).addClass('tipsy-south');
                    break;
                case 'e':
                    tip.css({
                        top: pos.top + pos.height / 2 - actualHeight / 2,
                        left: pos.left - actualWidth
                    }).addClass('tipsy-east');
                    break;
                case 'w':
                    tip.css({
                        top: pos.top + pos.height / 2 - actualHeight / 2,
                        left: pos.left + pos.width
                    }).addClass('tipsy-west');
                    break;
                }
                if (opts.fade) {
                    tip.css({
                        opacity: 0,
                        display: 'block',
                        visibility: 'visible'
                    }).animate({
                        opacity: 0.8
                    });
                } else {
                    tip.css({
                        visibility: 'visible'
                    });
                }
            }, function () {
                $.data(this, 'cancel.tipsy', false);
                var self = this;
                setTimeout(function () {
                    if ($.data(this, 'cancel.tipsy')) return;
                    var tip = $.data(self, 'active.tipsy');
                    if (opts.fade) {
                        tip.stop().fadeOut(function () {
                            $(this).remove();
                        });
                    } else {
                        tip.remove();
                    }
                }, 100);
            });
        });
    };
    $.fn.tipsy.elementOptions = function (ele, options) {
        return $.metadata ? $.extend({}, options, $(ele).metadata()) : options;
    };
    $.fn.tipsy.defaults = {
        fade: false,
        fallback: '',
        gravity: 'n',
        html: false,
        title: 'title'
    };
    $.fn.tipsy.autoNS = function () {
        return $(this).offset().top > ($(document).scrollTop() + $(window).height() / 2) ? 's' : 'n';
    };
    $.fn.tipsy.autoWE = function () {
        return $(this).offset().left > ($(document).scrollLeft() + $(window).width() / 2) ? 'e' : 'w';
    };
})(jQuery);
$(function () {
    $('.js-needs-tipsy-s').tipsy({
        'gravity': 's'
    });
});
(function (a) {
    var r = a.fn.domManip,
        d = "_tmplitem",
        q = /^[^<]*(<[\w\W]+>)[^>]*$|\{\{\! /,
        b = {},
        f = {},
        e, p = {
            key: 0,
            data: {}
        },
        i = 0,
        c = 0,
        l = [];

    function g(g, d, h, e) {
        var c = {
            data: e || (e === 0 || e === false) ? e : d ? d.data : {},
            _wrap: d ? d._wrap : null,
            tmpl: null,
            parent: d || null,
            nodes: [],
            calls: u,
            nest: w,
            wrap: x,
            html: v,
            update: t
        };
        g && a.extend(c, g, {
            nodes: [],
            parent: d
        });
        if (h) {
            c.tmpl = h;
            c._ctnt = c._ctnt || c.tmpl(a, c);
            c.key = ++i;
            (l.length ? f : b)[i] = c
        }
        return c
    }
    a.each({
        appendTo: "append",
        prependTo: "prepend",
        insertBefore: "before",
        insertAfter: "after",
        replaceAll: "replaceWith"
    }, function (f, d) {
        a.fn[f] = function (n) {
            var g = [],
                i = a(n),
                k, h, m, l, j = this.length === 1 && this[0].parentNode;
            e = b || {};
            if (j && j.nodeType === 11 && j.childNodes.length === 1 && i.length === 1) {
                i[d](this[0]);
                g = this
            } else {
                for (h = 0, m = i.length; h < m; h++) {
                    c = h;
                    k = (h > 0 ? this.clone(true) : this).get();
                    a(i[h])[d](k);
                    g = g.concat(k)
                }
                c = 0;
                g = this.pushStack(g, f, i.selector)
            }
            l = e;
            e = null;
            a.tmpl.complete(l);
            return g
        }
    });
    a.fn.extend({
        tmpl: function (d, c, b) {
            return a.tmpl(this[0], d, c, b)
        },
        tmplItem: function () {
            return a.tmplItem(this[0])
        },
        template: function (b) {
            return a.template(b, this[0])
        },
        domManip: function (d, m, k) {
            if (d[0] && a.isArray(d[0])) {
                var g = a.makeArray(arguments),
                    h = d[0],
                    j = h.length,
                    i = 0,
                    f;
                while (i < j && !(f = a.data(h[i++], "tmplItem")));
                if (f && c) g[2] = function (b) {
                    a.tmpl.afterManip(this, b, k)
                };
                r.apply(this, g)
            } else r.apply(this, arguments);
            c = 0;
            !e && a.tmpl.complete(b);
            return this
        }
    });
    a.extend({
        tmpl: function (d, h, e, c) {
            var i, k = !c;
            if (k) {
                c = p;
                d = a.template[d] || a.template(null, d);
                f = {}
            } else if (!d) {
                d = c.tmpl;
                b[c.key] = c;
                c.nodes = [];
                c.wrapped && n(c, c.wrapped);
                return a(j(c, null, c.tmpl(a, c)))
            }
            if (!d) return [];
            if (typeof h === "function") h = h.call(c || {});
            e && e.wrapped && n(e, e.wrapped);
            i = a.isArray(h) ? a.map(h, function (a) {
                return a ? g(e, c, d, a) : null
            }) : [g(e, c, d, h)];
            return k ? a(j(c, null, i)) : i
        },
        tmplItem: function (b) {
            var c;
            if (b instanceof a) b = b[0];
            while (b && b.nodeType === 1 && !(c = a.data(b, "tmplItem")) && (b = b.parentNode));
            return c || p
        },
        template: function (c, b) {
            if (b) {
                if (typeof b === "string") b = o(b);
                else if (b instanceof a) b = b[0] || {};
                if (b.nodeType) b = a.data(b, "tmpl") || a.data(b, "tmpl", o(b.innerHTML));
                return typeof c === "string" ? (a.template[c] = b) : b
            }
            return c ? typeof c !== "string" ? a.template(null, c) : a.template[c] || a.template(null, q.test(c) ? c : a(c)) : null
        },
        encode: function (a) {
            return ("" + a).split("<").join("&lt;").split(">").join("&gt;").split('"').join("&#34;").split("'").join("&#39;")
        }
    });
    a.extend(a.tmpl, {
        tag: {
            tmpl: {
                _default: {
                    $2: "null"
                },
                open: "if($notnull_1){__=__.concat($item.nest($1,$2));}"
            },
            wrap: {
                _default: {
                    $2: "null"
                },
                open: "$item.calls(__,$1,$2);__=[];",
                close: "call=$item.calls();__=call._.concat($item.wrap(call,__));"
            },
            each: {
                _default: {
                    $2: "$index, $value"
                },
                open: "if($notnull_1){$.each($1a,function($2){with(this){",
                close: "}});}"
            },
            "if": {
                open: "if(($notnull_1) && $1a){",
                close: "}"
            },
            "else": {
                _default: {
                    $1: "true"
                },
                open: "}else if(($notnull_1) && $1a){"
            },
            html: {
                open: "if($notnull_1){__.push($1a);}"
            },
            "=": {
                _default: {
                    $1: "$data"
                },
                open: "if($notnull_1){__.push($.encode($1a));}"
            },
            "!": {
                open: ""
            }
        },
        complete: function () {
            b = {}
        },
        afterManip: function (f, b, d) {
            var e = b.nodeType === 11 ? a.makeArray(b.childNodes) : b.nodeType === 1 ? [b] : [];
            d.call(f, b);
            m(e);
            c++
        }
    });

    function j(e, g, f) {
        var b, c = f ? a.map(f, function (a) {
            return typeof a === "string" ? e.key ? a.replace(/(<\w+)(?=[\s>])(?![^>]*_tmplitem)([^>]*)/g, "$1 " + d + '="' + e.key + '" $2') : a : j(a, e, a._ctnt)
        }) : e;
        if (g) return c;
        c = c.join("");
        c.replace(/^\s*([^<\s][^<]*)?(<[\w\W]+>)([^>]*[^>\s])?\s*$/, function (f, c, e, d) {
            b = a(e).get();
            m(b);
            if (c) b = k(c).concat(b);
            if (d) b = b.concat(k(d))
        });
        return b ? b : k(c)
    }
    function k(c) {
        var b = document.createElement("div");
        b.innerHTML = c;
        return a.makeArray(b.childNodes)
    }
    function o(b) {
        return new Function("jQuery", "$item", "var $=jQuery,call,__=[],$data=$item.data;with($data){__.push('" + a.trim(b).replace(/([\\'])/g, "\\$1").replace(/[\r\t\n]/g, " ").replace(/\$\{([^\}]*)\}/g, "{{= $1}}").replace(/\{\{(\/?)(\w+|.)(?:\(((?:[^\}]|\}(?!\}))*?)?\))?(?:\s+(.*?)?)?(\(((?:[^\}]|\}(?!\}))*?)\))?\s*\}\}/g, function (m, l, k, g, b, c, d) {
            var j = a.tmpl.tag[k],
                i, e, f;
            if (!j) throw "Unknown template tag: " + k;
            i = j._default || [];
            if (c && !/\w$/.test(b)) {
                b += c;
                c = ""
            }
            if (b) {
                b = h(b);
                d = d ? "," + h(d) + ")" : c ? ")" : "";
                e = c ? b.indexOf(".") > -1 ? b + h(c) : "(" + b + ").call($item" + d : b;
                f = c ? e : "(typeof(" + b + ")==='function'?(" + b + ").call($item):(" + b + "))"
            } else f = e = i.$1 || "null";
            g = h(g);
            return "');" + j[l ? "close" : "open"].split("$notnull_1").join(b ? "typeof(" + b + ")!=='undefined' && (" + b + ")!=null" : "true").split("$1a").join(f).split("$1").join(e).split("$2").join(g || i.$2 || "") + "__.push('"
        }) + "');}return __;")
    }
    function n(c, b) {
        c._wrap = j(c, true, a.isArray(b) ? b : [q.test(b) ? b : a(b).html()]).join("")
    }
    function h(a) {
        return a ? a.replace(/\\'/g, "'").replace(/\\\\/g, "\\") : null
    }
    function s(b) {
        var a = document.createElement("div");
        a.appendChild(b.cloneNode(true));
        return a.innerHTML
    }
    function m(o) {
        var n = "_" + c,
            k, j, l = {},
            e, p, h;
        for (e = 0, p = o.length; e < p; e++) {
            if ((k = o[e]).nodeType !== 1) continue;
            j = k.getElementsByTagName("*");
            for (h = j.length - 1; h >= 0; h--) m(j[h]);
            m(k)
        }
        function m(j) {
            var p, h = j,
                k, e, m;
            if (m = j.getAttribute(d)) {
                while (h.parentNode && (h = h.parentNode).nodeType === 1 && !(p = h.getAttribute(d)));
                if (p !== m) {
                    h = h.parentNode ? h.nodeType === 11 ? 0 : h.getAttribute(d) || 0 : 0;
                    if (!(e = b[m])) {
                        e = f[m];
                        e = g(e, b[h] || f[h]);
                        e.key = ++i;
                        b[i] = e
                    }
                    c && o(m)
                }
                j.removeAttribute(d)
            } else if (c && (e = a.data(j, "tmplItem"))) {
                o(e.key);
                b[e.key] = e;
                h = a.data(j.parentNode, "tmplItem");
                h = h ? h.key : 0
            }
            if (e) {
                k = e;
                while (k && k.key != h) {
                    k.nodes.push(j);
                    k = k.parent
                }
                delete e._ctnt;
                delete e._wrap;
                a.data(j, "tmplItem", e)
            }
            function o(a) {
                a = a + n;
                e = l[a] = l[a] || g(e, b[e.parent.key + n] || e.parent)
            }
        }
    }
    function u(a, d, c, b) {
        if (!a) return l.pop();
        l.push({
            _: a,
            tmpl: d,
            item: this,
            data: c,
            options: b
        })
    }
    function w(d, c, b) {
        return a.tmpl(a.template(d), c, b, this)
    }
    function x(b, d) {
        var c = b.options || {};
        c.wrapped = d;
        return a.tmpl(a.template(b.tmpl), b.data, c, b.item)
    }
    function v(d, c) {
        var b = this._wrap;
        return a.map(a(a.isArray(b) ? b.join("") : b).filter(d || "*"), function (a) {
            return c ? a.innerText || a.textContent : a.outerHTML || s(a)
        })
    }
    function t() {
        var b = this.nodes;
        a.tmpl(null, null, null, this).insertBefore(b[0]);
        a(b).remove()
    }
})(jQuery);
(function (c) {
    function p(d, b, a) {
        var e = this,
            l = d.add(this),
            h = d.find(a.tabs),
            i = b.jquery ? b : d.children(b),
            j;
        h.length || (h = d.children());
        i.length || (i = d.parent().find(b));
        i.length || (i = c(b));
        c.extend(this, {
            click: function (f, g) {
                var k = h.eq(f);
                if (typeof f == "string" && f.replace("#", "")) {
                    k = h.filter("[href*=" + f.replace("#", "") + "]");
                    f = Math.max(h.index(k), 0)
                }
                if (a.rotate) {
                    var n = h.length - 1;
                    if (f < 0) return e.click(n, g);
                    if (f > n) return e.click(0, g)
                }
                if (!k.length) {
                    if (j >= 0) return e;
                    f = a.initialIndex;
                    k = h.eq(f)
                }
                if (f === j) return e;
                g = g || c.Event();
                g.type = "onBeforeClick";
                l.trigger(g, [f]);
                if (!g.isDefaultPrevented()) {
                    o[a.effect].call(e, f, function () {
                        g.type = "onClick";
                        l.trigger(g, [f])
                    });
                    j = f;
                    h.removeClass(a.current);
                    k.addClass(a.current);
                    return e
                }
            },
            getConf: function () {
                return a
            },
            getTabs: function () {
                return h
            },
            getPanes: function () {
                return i
            },
            getCurrentPane: function () {
                return i.eq(j)
            },
            getCurrentTab: function () {
                return h.eq(j)
            },
            getIndex: function () {
                return j
            },
            next: function () {
                return e.click(j + 1)
            },
            prev: function () {
                return e.click(j - 1)
            },
            destroy: function () {
                h.unbind(a.event).removeClass(a.current);
                i.find("a[href^=#]").unbind("click.T");
                return e
            }
        });
        c.each("onBeforeClick,onClick".split(","), function (f, g) {
            c.isFunction(a[g]) && c(e).bind(g, a[g]);
            e[g] = function (k) {
                k && c(e).bind(g, k);
                return e
            }
        });
        if (a.history && c.fn.history) {
            c.tools.history.init(h);
            a.event = "history"
        }
        h.each(function (f) {
            c(this).bind(a.event, function (g) {
                e.click(f, g);
                return g.preventDefault()
            })
        });
        i.find("a[href^=#]").bind("click.T", function (f) {
            e.click(c(this).attr("href"), f)
        });
        if (location.hash && a.tabs == "a" && d.find("[href=" + location.hash + "]").length) e.click(location.hash);
        else if (a.initialIndex === 0 || a.initialIndex > 0) e.click(a.initialIndex)
    }
    c.tools = c.tools || {
        version: "1.2.5"
    };
    c.tools.tabs = {
        conf: {
            tabs: "a",
            current: "current",
            onBeforeClick: null,
            onClick: null,
            effect: "default",
            initialIndex: 0,
            event: "click",
            rotate: false,
            history: false
        },
        addEffect: function (d, b) {
            o[d] = b
        }
    };
    var o = {
        "default": function (d, b) {
            this.getPanes().hide().eq(d).show();
            b.call()
        },
        fade: function (d, b) {
            var a = this.getConf(),
                e = a.fadeOutSpeed,
                l = this.getPanes();
            e ? l.fadeOut(e) : l.hide();
            l.eq(d).fadeIn(a.fadeInSpeed, b)
        },
        slide: function (d, b) {
            this.getPanes().slideUp(200);
            this.getPanes().eq(d).slideDown(400, b)
        },
        ajax: function (d, b) {
            this.getPanes().eq(0).load(this.getTabs().eq(d).attr("href"), b)
        }
    },
        m;
    c.tools.tabs.addEffect("horizontal", function (d, b) {
        m || (m = this.getPanes().eq(0).width());
        this.getCurrentPane().animate({
            width: 0
        }, function () {
            c(this).hide()
        });
        this.getPanes().eq(d).animate({
            width: m
        }, function () {
            c(this).show();
            b.call()
        })
    });
    c.fn.tabs = function (d, b) {
        var a = this.data("tabs");
        if (a) {
            a.destroy();
            this.removeData("tabs")
        }
        if (c.isFunction(b)) b = {
            onBeforeClick: b
        };
        b = c.extend({}, c.tools.tabs.conf, b);
        this.each(function () {
            a = new p(c(this), d, b);
            c(this).data("tabs", a)
        });
        return b.api ? a : this
    }
})(jQuery);
(function (c) {
    function p(g, a) {
        function m(f) {
            var e = c(f);
            return e.length < 2 ? e : g.parent().find(f)
        }
        var b = this,
            i = g.add(this),
            d = g.data("tabs"),
            h, j = true,
            n = m(a.next).click(function () {
                d.next()
            }),
            k = m(a.prev).click(function () {
                d.prev()
            });
        c.extend(b, {
            getTabs: function () {
                return d
            },
            getConf: function () {
                return a
            },
            play: function () {
                if (h) return b;
                var f = c.Event("onBeforePlay");
                i.trigger(f);
                if (f.isDefaultPrevented()) return b;
                h = setInterval(d.next, a.interval);
                j = false;
                i.trigger("onPlay");
                return b
            },
            pause: function () {
                if (!h) return b;
                var f = c.Event("onBeforePause");
                i.trigger(f);
                if (f.isDefaultPrevented()) return b;
                h = clearInterval(h);
                i.trigger("onPause");
                return b
            },
            stop: function () {
                b.pause();
                j = true
            }
        });
        c.each("onBeforePlay,onPlay,onBeforePause,onPause".split(","), function (f, e) {
            c.isFunction(a[e]) && c(b).bind(e, a[e]);
            b[e] = function (q) {
                return c(b).bind(e, q)
            }
        });
        a.autopause && d.getTabs().add(n).add(k).add(d.getPanes()).hover(b.pause, function () {
            j || b.play()
        });
        a.autoplay && b.play();
        a.clickable && d.getPanes().click(function () {
            d.next()
        });
        if (!d.getConf().rotate) {
            var l = a.disabledClass;
            d.getIndex() || k.addClass(l);
            d.onBeforeClick(function (f, e) {
                k.toggleClass(l, !e);
                n.toggleClass(l, e == d.getTabs().length - 1)
            })
        }
    }
    var o;
    o = c.tools.tabs.slideshow = {
        conf: {
            next: ".forward",
            prev: ".backward",
            disabledClass: "disabled",
            autoplay: false,
            autopause: true,
            interval: 3E3,
            clickable: true,
            api: false
        }
    };
    c.fn.slideshow = function (g) {
        var a = this.data("slideshow");
        if (a) return a;
        g = c.extend({}, o.conf, g);
        this.each(function () {
            a = new p(c(this), g);
            c(this).data("slideshow", a)
        });
        return g.api ? a : this
    }
})(jQuery);
(function (f) {
    function p(a, b, c) {
        var h = c.relative ? a.position().top : a.offset().top,
            d = c.relative ? a.position().left : a.offset().left,
            i = c.position[0];
        h -= b.outerHeight() - c.offset[0];
        d += a.outerWidth() + c.offset[1];
        if (/iPad/i.test(navigator.userAgent)) h -= f(window).scrollTop();
        var j = b.outerHeight() + a.outerHeight();
        if (i == "center") h += j / 2;
        if (i == "bottom") h += j;
        i = c.position[1];
        a = b.outerWidth() + a.outerWidth();
        if (i == "center") d -= a / 2;
        if (i == "left") d -= a;
        return {
            top: h,
            left: d
        }
    }
    function u(a, b) {
        var c = this,
            h = a.add(c),
            d, i = 0,
            j = 0,
            m = a.attr("title"),
            q = a.attr("data-tooltip"),
            r = o[b.effect],
            l, s = a.is(":input"),
            v = s && a.is(":checkbox, :radio, select, :button, :submit"),
            t = a.attr("type"),
            k = b.events[t] || b.events[s ? v ? "widget" : "input" : "def"];
        if (!r) throw 'Nonexistent effect "' + b.effect + '"';
        k = k.split(/,\s*/);
        if (k.length != 2) throw "Tooltip: bad events configuration for " + t;
        a.bind(k[0], function (e) {
            clearTimeout(i);
            if (b.predelay) j = setTimeout(function () {
                c.show(e)
            }, b.predelay);
            else c.show(e)
        }).bind(k[1], function (e) {
            clearTimeout(j);
            if (b.delay) i = setTimeout(function () {
                c.hide(e)
            }, b.delay);
            else c.hide(e)
        });
        if (m && b.cancelDefault) {
            a.removeAttr("title");
            a.data("title", m)
        }
        f.extend(c, {
            show: function (e) {
                if (!d) {
                    if (q) d = f(q);
                    else if (b.tip) d = f(b.tip).eq(0);
                    else if (m) d = f(b.layout).addClass(b.tipClass).appendTo(document.body).hide().append(m);
                    else {
                        d = a.next();
                        d.length || (d = a.parent().next())
                    }
                    if (!d.length) throw "Cannot find tooltip for " + a;
                }
                if (c.isShown()) return c;
                d.stop(true, true);
                var g = p(a, d, b);
                b.tip && d.html(a.data("title"));
                e = e || f.Event();
                e.type = "onBeforeShow";
                h.trigger(e, [g]);
                if (e.isDefaultPrevented()) return c;
                g = p(a, d, b);
                d.css({
                    position: "absolute",
                    top: g.top,
                    left: g.left
                });
                l = true;
                r[0].call(c, function () {
                    e.type = "onShow";
                    l = "full";
                    h.trigger(e)
                });
                g = b.events.tooltip.split(/,\s*/);
                if (!d.data("__set")) {
                    d.bind(g[0], function () {
                        clearTimeout(i);
                        clearTimeout(j)
                    });
                    g[1] && !a.is("input:not(:checkbox, :radio), textarea") && d.bind(g[1], function (n) {
                        n.relatedTarget != a[0] && a.trigger(k[1].split(" ")[0])
                    });
                    d.data("__set", true)
                }
                return c
            },
            hide: function (e) {
                if (!d || !c.isShown()) return c;
                e = e || f.Event();
                e.type = "onBeforeHide";
                h.trigger(e);
                if (!e.isDefaultPrevented()) {
                    l = false;
                    o[b.effect][1].call(c, function () {
                        e.type = "onHide";
                        h.trigger(e)
                    });
                    return c
                }
            },
            isShown: function (e) {
                return e ? l == "full" : l
            },
            getConf: function () {
                return b
            },
            getTip: function () {
                return d
            },
            getTrigger: function () {
                return a
            }
        });
        f.each("onHide,onBeforeShow,onShow,onBeforeHide".split(","), function (e, g) {
            f.isFunction(b[g]) && f(c).bind(g, b[g]);
            c[g] = function (n) {
                n && f(c).bind(g, n);
                return c
            }
        })
    }
    f.tools = f.tools || {
        version: "1.2.5"
    };
    f.tools.tooltip = {
        conf: {
            effect: "toggle",
            fadeOutSpeed: "fast",
            predelay: 0,
            delay: 30,
            opacity: 1,
            tip: 0,
            position: ["top", "center"],
            offset: [0, 0],
            relative: false,
            cancelDefault: true,
            events: {
                def: "mouseenter,mouseleave",
                input: "focus,blur",
                widget: "focus mouseenter,blur mouseleave",
                tooltip: "mouseenter,mouseleave"
            },
            layout: "<div/>",
            tipClass: "tooltip"
        },
        addEffect: function (a, b, c) {
            o[a] = [b, c]
        }
    };
    var o = {
        toggle: [function (a) {
            var b = this.getConf(),
                c = this.getTip();
            b = b.opacity;
            b < 1 && c.css({
                opacity: b
            });
            c.show();
            a.call()
        }, function (a) {
            this.getTip().hide();
            a.call()
        }],
        fade: [function (a) {
            var b = this.getConf();
            this.getTip().fadeTo(b.fadeInSpeed, b.opacity, a)
        }, function (a) {
            this.getTip().fadeOut(this.getConf().fadeOutSpeed, a)
        }]
    };
    f.fn.tooltip = function (a) {
        var b = this.data("tooltip");
        if (b) return b;
        a = f.extend(true, {}, f.tools.tooltip.conf, a);
        if (typeof a.position == "string") a.position = a.position.split(/,?\s/);
        this.each(function () {
            b = new u(f(this), a);
            f(this).data("tooltip", b)
        });
        return a.api ? b : this
    }
})(jQuery);
(function (d) {
    var i = d.tools.tooltip;
    d.extend(i.conf, {
        direction: "up",
        bounce: false,
        slideOffset: 10,
        slideInSpeed: 200,
        slideOutSpeed: 200,
        slideFade: !d.browser.msie
    });
    var e = {
        up: ["-", "top"],
        down: ["+", "top"],
        left: ["-", "left"],
        right: ["+", "left"]
    };
    i.addEffect("slide", function (g) {
        var a = this.getConf(),
            f = this.getTip(),
            b = a.slideFade ? {
                opacity: a.opacity
            } : {},
            c = e[a.direction] || e.up;
        b[c[1]] = c[0] + "=" + a.slideOffset;
        a.slideFade && f.css({
            opacity: 0
        });
        f.show().animate(b, a.slideInSpeed, g)
    }, function (g) {
        var a = this.getConf(),
            f = a.slideOffset,
            b = a.slideFade ? {
                opacity: 0
            } : {},
            c = e[a.direction] || e.up,
            h = "" + c[0];
        if (a.bounce) h = h == "+" ? "-" : "+";
        b[c[1]] = h + "=" + f;
        this.getTip().animate(b, a.slideOutSpeed, function () {
            d(this).hide();
            g.call()
        })
    })
})(jQuery);
(function (g) {
    function j(a) {
        var c = g(window),
            d = c.width() + c.scrollLeft(),
            h = c.height() + c.scrollTop();
        return [a.offset().top <= c.scrollTop(), d <= a.offset().left + a.width(), h <= a.offset().top + a.height(), c.scrollLeft() >= a.offset().left]
    }
    function k(a) {
        for (var c = a.length; c--;) if (a[c]) return false;
        return true
    }
    var i = g.tools.tooltip;
    i.dynamic = {
        conf: {
            classNames: "top right bottom left"
        }
    };
    g.fn.dynamic = function (a) {
        if (typeof a == "number") a = {
            speed: a
        };
        a = g.extend({}, i.dynamic.conf, a);
        var c = a.classNames.split(/\s/),
            d;
        this.each(function () {
            var h = g(this).tooltip().onBeforeShow(function (e, f) {
                e = this.getTip();
                var b = this.getConf();
                d || (d = [b.position[0], b.position[1], b.offset[0], b.offset[1], g.extend({}, b)]);
                g.extend(b, d[4]);
                b.position = [d[0], d[1]];
                b.offset = [d[2], d[3]];
                e.css({
                    visibility: "hidden",
                    position: "absolute",
                    top: f.top,
                    left: f.left
                }).show();
                f = j(e);
                if (!k(f)) {
                    if (f[2]) {
                        g.extend(b, a.top);
                        b.position[0] = "top";
                        e.addClass(c[0])
                    }
                    if (f[3]) {
                        g.extend(b, a.right);
                        b.position[1] = "right";
                        e.addClass(c[1])
                    }
                    if (f[0]) {
                        g.extend(b, a.bottom);
                        b.position[0] = "bottom";
                        e.addClass(c[2])
                    }
                    if (f[1]) {
                        g.extend(b, a.left);
                        b.position[1] = "left";
                        e.addClass(c[3])
                    }
                    if (f[0] || f[2]) b.offset[0] *= -1;
                    if (f[1] || f[3]) b.offset[1] *= -1
                }
                e.css({
                    visibility: "visible"
                }).hide()
            });
            h.onBeforeShow(function () {
                var e = this.getConf();
                this.getTip();
                setTimeout(function () {
                    e.position = [d[0], d[1]];
                    e.offset = [d[2], d[3]]
                }, 0)
            });
            h.onHide(function () {
                var e = this.getTip();
                e.removeClass(a.classNames)
            });
            ret = h
        });
        return a.api ? ret : this
    }
})(jQuery);
(function (e) {
    function p(f, c) {
        var b = e(c);
        return b.length < 2 ? b : f.parent().find(c)
    }
    function u(f, c) {
        var b = this,
            n = f.add(b),
            g = f.children(),
            l = 0,
            j = c.vertical;
        k || (k = b);
        if (g.length > 1) g = e(c.items, f);
        e.extend(b, {
            getConf: function () {
                return c
            },
            getIndex: function () {
                return l
            },
            getSize: function () {
                return b.getItems().size()
            },
            getNaviButtons: function () {
                return o.add(q)
            },
            getRoot: function () {
                return f
            },
            getItemWrap: function () {
                return g
            },
            getItems: function () {
                return g.children(c.item).not("." + c.clonedClass)
            },
            move: function (a, d) {
                return b.seekTo(l + a, d)
            },
            next: function (a) {
                return b.move(1, a)
            },
            prev: function (a) {
                return b.move(-1, a)
            },
            begin: function (a) {
                return b.seekTo(0, a)
            },
            end: function (a) {
                return b.seekTo(b.getSize() - 1, a)
            },
            focus: function () {
                return k = b
            },
            addItem: function (a) {
                a = e(a);
                if (c.circular) {
                    g.children("." + c.clonedClass + ":last").before(a);
                    g.children("." + c.clonedClass + ":first").replaceWith(a.clone().addClass(c.clonedClass))
                } else g.append(a);
                n.trigger("onAddItem", [a]);
                return b
            },
            seekTo: function (a, d, h) {
                a.jquery || (a *= 1);
                if (c.circular && a === 0 && l == -1 && d !== 0) return b;
                if (!c.circular && a < 0 || a > b.getSize() || a < -1) return b;
                var i = a;
                if (a.jquery) a = b.getItems().index(a);
                else i = b.getItems().eq(a);
                var r = e.Event("onBeforeSeek");
                if (!h) {
                    n.trigger(r, [a, d]);
                    if (r.isDefaultPrevented() || !i.length) return b
                }
                i = j ? {
                    top: -i.position().top
                } : {
                    left: -i.position().left
                };
                l = a;
                k = b;
                if (d === undefined) d = c.speed;
                g.animate(i, d, c.easing, h ||
                function () {
                    n.trigger("onSeek", [a])
                });
                return b
            }
        });
        e.each(["onBeforeSeek", "onSeek", "onAddItem"], function (a, d) {
            e.isFunction(c[d]) && e(b).bind(d, c[d]);
            b[d] = function (h) {
                h && e(b).bind(d, h);
                return b
            }
        });
        if (c.circular) {
            var s = b.getItems().slice(-1).clone().prependTo(g),
                t = b.getItems().eq(1).clone().appendTo(g);
            s.add(t).addClass(c.clonedClass);
            b.onBeforeSeek(function (a, d, h) {
                if (!a.isDefaultPrevented()) if (d == -1) {
                    b.seekTo(s, h, function () {
                        b.end(0)
                    });
                    return a.preventDefault()
                } else d == b.getSize() && b.seekTo(t, h, function () {
                    b.begin(0)
                })
            });
            b.seekTo(0, 0, function () {})
        }
        var o = p(f, c.prev).click(function () {
            b.prev()
        }),
            q = p(f, c.next).click(function () {
                b.next()
            });
        if (!c.circular && b.getSize() > 1) {
            b.onBeforeSeek(function (a, d) {
                setTimeout(function () {
                    if (!a.isDefaultPrevented()) {
                        o.toggleClass(c.disabledClass, d <= 0);
                        q.toggleClass(c.disabledClass, d >= b.getSize() - 1)
                    }
                }, 1)
            });
            c.initialIndex || o.addClass(c.disabledClass)
        }
        c.mousewheel && e.fn.mousewheel && f.mousewheel(function (a, d) {
            if (c.mousewheel) {
                b.move(d < 0 ? 1 : -1, c.wheelSpeed || 50);
                return false
            }
        });
        if (c.touch) {
            var m = {};
            g[0].ontouchstart = function (a) {
                a = a.touches[0];
                m.x = a.clientX;
                m.y = a.clientY
            };
            g[0].ontouchmove = function (a) {
                if (a.touches.length == 1 && !g.is(":animated")) {
                    var d = a.touches[0],
                        h = m.x - d.clientX;
                    d = m.y - d.clientY;
                    b[j && d > 0 || !j && h > 0 ? "next" : "prev"]();
                    a.preventDefault()
                }
            }
        }
        c.keyboard && e(document).bind("keydown.scrollable", function (a) {
            if (!(!c.keyboard || a.altKey || a.ctrlKey || e(a.target).is(":input"))) if (!(c.keyboard != "static" && k != b)) {
                var d = a.keyCode;
                if (j && (d == 38 || d == 40)) {
                    b.move(d == 38 ? -1 : 1);
                    return a.preventDefault()
                }
                if (!j && (d == 37 || d == 39)) {
                    b.move(d == 37 ? -1 : 1);
                    return a.preventDefault()
                }
            }
        });
        c.initialIndex && b.seekTo(c.initialIndex, 0, function () {})
    }
    e.tools = e.tools || {
        version: "1.2.5"
    };
    e.tools.scrollable = {
        conf: {
            activeClass: "active",
            circular: false,
            clonedClass: "cloned",
            disabledClass: "disabled",
            easing: "swing",
            initialIndex: 0,
            item: null,
            items: ".items",
            keyboard: true,
            mousewheel: false,
            next: ".next",
            prev: ".prev",
            speed: 400,
            vertical: false,
            touch: true,
            wheelSpeed: 0
        }
    };
    var k;
    e.fn.scrollable = function (f) {
        var c = this.data("scrollable");
        if (c) return c;
        f = e.extend({}, e.tools.scrollable.conf, f);
        this.each(function () {
            c = new u(e(this), f);
            e(this).data("scrollable", c)
        });
        return f.api ? c : this
    }
})(jQuery);
(function (b) {
    var f = b.tools.scrollable;
    f.autoscroll = {
        conf: {
            autoplay: true,
            interval: 3E3,
            autopause: true
        }
    };
    b.fn.autoscroll = function (c) {
        if (typeof c == "number") c = {
            interval: c
        };
        var d = b.extend({}, f.autoscroll.conf, c),
            g;
        this.each(function () {
            var a = b(this).data("scrollable");
            if (a) g = a;
            var e, h = true;
            a.play = function () {
                if (!e) {
                    h = false;
                    e = setInterval(function () {
                        a.next()
                    }, d.interval)
                }
            };
            a.pause = function () {
                e = clearInterval(e)
            };
            a.stop = function () {
                a.pause();
                h = true
            };
            d.autopause && a.getRoot().add(a.getNaviButtons()).hover(a.pause, a.play);
            d.autoplay && a.play()
        });
        return d.api ? g : this
    }
})(jQuery);
(function (d) {
    function p(b, g) {
        var h = d(g);
        return h.length < 2 ? h : b.parent().find(g)
    }
    var m = d.tools.scrollable;
    m.navigator = {
        conf: {
            navi: ".navi",
            naviItem: null,
            activeClass: "active",
            indexed: false,
            idPrefix: null,
            history: false
        }
    };
    d.fn.navigator = function (b) {
        if (typeof b == "string") b = {
            navi: b
        };
        b = d.extend({}, m.navigator.conf, b);
        var g;
        this.each(function () {
            function h(a, c, i) {
                e.seekTo(c);
                if (j) {
                    if (location.hash) location.hash = a.attr("href").replace("#", "")
                } else return i.preventDefault()
            }
            function f() {
                return k.find(b.naviItem || "> *")
            }
            function n(a) {
                var c = d("<" + (b.naviItem || "a") + "/>").click(function (i) {
                    h(d(this), a, i)
                }).attr("href", "#" + a);
                a === 0 && c.addClass(l);
                b.indexed && c.text(a + 1);
                b.idPrefix && c.attr("id", b.idPrefix + a);
                return c.appendTo(k)
            }
            function o(a, c) {
                a = f().eq(c.replace("#", ""));
                a.length || (a = f().filter("[href=" + c + "]"));
                a.click()
            }
            var e = d(this).data("scrollable"),
                k = b.navi.jquery ? b.navi : p(e.getRoot(), b.navi),
                q = e.getNaviButtons(),
                l = b.activeClass,
                j = b.history && d.fn.history;
            if (e) g = e;
            e.getNaviButtons = function () {
                return q.add(k)
            };
            f().length ? f().each(function (a) {
                d(this).click(function (c) {
                    h(d(this), a, c)
                })
            }) : d.each(e.getItems(), function (a) {
                n(a)
            });
            e.onBeforeSeek(function (a, c) {
                setTimeout(function () {
                    if (!a.isDefaultPrevented()) {
                        var i = f().eq(c);
                        !a.isDefaultPrevented() && i.length && f().removeClass(l).eq(c).addClass(l)
                    }
                }, 1)
            });
            e.onAddItem(function (a, c) {
                c = n(e.getItems().index(c));
                j && c.history(o)
            });
            j && f().history(o)
        });
        return b.api ? g : this
    }
})(jQuery);
(function (a) {
    function t(d, b) {
        var c = this,
            j = d.add(c),
            o = a(window),
            k, f, m, g = a.tools.expose && (b.mask || b.expose),
            n = Math.random().toString().slice(10);
        if (g) {
            if (typeof g == "string") g = {
                color: g
            };
            g.closeOnClick = g.closeOnEsc = false
        }
        var p = b.target || d.attr("rel");
        f = p ? a(p) : d;
        if (!f.length) throw "Could not find Overlay: " + p;
        d && d.index(f) == -1 && d.click(function (e) {
            c.load(e);
            return e.preventDefault()
        });
        a.extend(c, {
            load: function (e) {
                if (c.isOpened()) return c;
                var h = q[b.effect];
                if (!h) throw 'Overlay: cannot find effect : "' + b.effect + '"';
                b.oneInstance && a.each(s, function () {
                    this.close(e)
                });
                e = e || a.Event();
                e.type = "onBeforeLoad";
                j.trigger(e);
                if (e.isDefaultPrevented()) return c;
                m = true;
                g && a(f).expose(g);
                var i = b.top,
                    r = b.left,
                    u = f.outerWidth({
                        margin: true
                    }),
                    v = f.outerHeight({
                        margin: true
                    });
                if (typeof i == "string") i = i == "center" ? Math.max((o.height() - v) / 2, 0) : parseInt(i, 10) / 100 * o.height();
                if (r == "center") r = Math.max((o.width() - u) / 2, 0);
                h[0].call(c, {
                    top: i,
                    left: r
                }, function () {
                    if (m) {
                        e.type = "onLoad";
                        j.trigger(e)
                    }
                });
                g && b.closeOnClick && a.mask.getMask().one("click", c.close);
                b.closeOnClick && a(document).bind("click." + n, function (l) {
                    a(l.target).parents(f).length || c.close(l)
                });
                b.closeOnEsc && a(document).bind("keydown." + n, function (l) {
                    l.keyCode == 27 && c.close(l)
                });
                return c
            },
            close: function (e) {
                if (!c.isOpened()) return c;
                e = e || a.Event();
                e.type = "onBeforeClose";
                j.trigger(e);
                if (!e.isDefaultPrevented()) {
                    m = false;
                    q[b.effect][1].call(c, function () {
                        e.type = "onClose";
                        j.trigger(e)
                    });
                    a(document).unbind("click." + n).unbind("keydown." + n);
                    g && a.mask.close();
                    return c
                }
            },
            getOverlay: function () {
                return f
            },
            getTrigger: function () {
                return d
            },
            getClosers: function () {
                return k
            },
            isOpened: function () {
                return m
            },
            getConf: function () {
                return b
            }
        });
        a.each("onBeforeLoad,onStart,onLoad,onBeforeClose,onClose".split(","), function (e, h) {
            a.isFunction(b[h]) && a(c).bind(h, b[h]);
            c[h] = function (i) {
                i && a(c).bind(h, i);
                return c
            }
        });
        k = f.find(b.close || ".close");
        if (!k.length && !b.close) {
            k = a('<a class="close"></a>');
            f.prepend(k)
        }
        k.click(function (e) {
            c.close(e)
        });
        b.load && c.load()
    }
    a.tools = a.tools || {
        version: "1.2.5"
    };
    a.tools.overlay = {
        addEffect: function (d, b, c) {
            q[d] = [b, c]
        },
        conf: {
            close: null,
            closeOnClick: true,
            closeOnEsc: true,
            closeSpeed: "fast",
            effect: "default",
            fixed: !a.browser.msie || a.browser.version > 6,
            left: "center",
            load: false,
            mask: null,
            oneInstance: true,
            speed: "normal",
            target: null,
            top: "10%"
        }
    };
    var s = [],
        q = {};
    a.tools.overlay.addEffect("default", function (d, b) {
        var c = this.getConf(),
            j = a(window);
        if (!c.fixed) {
            d.top += j.scrollTop();
            d.left += j.scrollLeft()
        }
        d.position = c.fixed ? "fixed" : "absolute";
        this.getOverlay().css(d).fadeIn(c.speed, b)
    }, function (d) {
        this.getOverlay().fadeOut(this.getConf().closeSpeed, d)
    });
    a.fn.overlay = function (d) {
        var b = this.data("overlay");
        if (b) return b;
        if (a.isFunction(d)) d = {
            onBeforeLoad: d
        };
        d = a.extend(true, {}, a.tools.overlay.conf, d);
        this.each(function () {
            b = new t(a(this), d);
            s.push(b);
            a(this).data("overlay", b)
        });
        return d.api ? b : this
    }
})(jQuery);
(function (d) {
    function R(a, c) {
        return 32 - (new Date(a, c, 32)).getDate()
    }
    function S(a, c) {
        a = "" + a;
        for (c = c || 2; a.length < c;) a = "0" + a;
        return a
    }
    function T(a, c, j) {
        var q = a.getDate(),
            h = a.getDay(),
            r = a.getMonth();
        a = a.getFullYear();
        var f = {
            d: q,
            dd: S(q),
            ddd: B[j].shortDays[h],
            dddd: B[j].days[h],
            m: r + 1,
            mm: S(r + 1),
            mmm: B[j].shortMonths[r],
            mmmm: B[j].months[r],
            yy: String(a).slice(2),
            yyyy: a
        };
        c = c.replace(X, function (s) {
            return s in f ? f[s] : s.slice(1, s.length - 1)
        });
        return Y.html(c).html()
    }
    function v(a) {
        return parseInt(a, 10)
    }
    function U(a, c) {
        return a.getFullYear() === c.getFullYear() && a.getMonth() == c.getMonth() && a.getDate() == c.getDate()
    }
    function C(a) {
        if (a) {
            if (a.constructor == Date) return a;
            if (typeof a == "string") {
                var c = a.split("-");
                if (c.length == 3) return new Date(v(c[0]), v(c[1]) - 1, v(c[2]));
                if (!/^-?\d+$/.test(a)) return;
                a = v(a)
            }
            c = new Date;
            c.setDate(c.getDate() + a);
            return c
        }
    }
    function Z(a, c) {
        function j(b, e, g) {
            n = b;
            D = b.getFullYear();
            E = b.getMonth();
            G = b.getDate();
            g = g || d.Event("api");
            g.type = "change";
            H.trigger(g, [b]);
            if (!g.isDefaultPrevented()) {
                a.val(T(b, e.format, e.lang));
                a.data("date", b);
                h.hide(g)
            }
        }
        function q(b) {
            b.type = "onShow";
            H.trigger(b);
            d(document).bind("keydown.d", function (e) {
                if (e.ctrlKey) return true;
                var g = e.keyCode;
                if (g == 8) {
                    a.val("");
                    return h.hide(e)
                }
                if (g == 27) return h.hide(e);
                if (d(V).index(g) >= 0) {
                    if (!w) {
                        h.show(e);
                        return e.preventDefault()
                    }
                    var i = d("#" + f.weeks + " a"),
                        t = d("." + f.focus),
                        o = i.index(t);
                    t.removeClass(f.focus);
                    if (g == 74 || g == 40) o += 7;
                    else if (g == 75 || g == 38) o -= 7;
                    else if (g == 76 || g == 39) o += 1;
                    else if (g == 72 || g == 37) o -= 1;
                    if (o > 41) {
                        h.addMonth();
                        t = d("#" + f.weeks + " a:eq(" + (o - 42) + ")")
                    } else if (o < 0) {
                        h.addMonth(-1);
                        t = d("#" + f.weeks + " a:eq(" + (o + 42) + ")")
                    } else t = i.eq(o);
                    t.addClass(f.focus);
                    return e.preventDefault()
                }
                if (g == 34) return h.addMonth();
                if (g == 33) return h.addMonth(-1);
                if (g == 36) return h.today();
                if (g == 13) d(e.target).is("select") || d("." + f.focus).click();
                return d([16, 17, 18, 9]).index(g) >= 0
            });
            d(document).bind("click.d", function (e) {
                var g = e.target;
                if (!d(g).parents("#" + f.root).length && g != a[0] && (!L || g != L[0])) h.hide(e)
            })
        }
        var h = this,
            r = new Date,
            f = c.css,
            s = B[c.lang],
            k = d("#" + f.root),
            M = k.find("#" + f.title),
            L, I, J, D, E, G, n = a.attr("data-value") || c.value || a.val(),
            m = a.attr("min") || c.min,
            p = a.attr("max") || c.max,
            w;
        if (m === 0) m = "0";
        n = C(n) || r;
        m = C(m || c.yearRange[0] * 365);
        p = C(p || c.yearRange[1] * 365);
        if (!s) throw "Dateinput: invalid language: " + c.lang;
        if (a.attr("type") == "date") {
            var N = d("<input/>");
            d.each("class,disabled,id,maxlength,name,readonly,required,size,style,tabindex,title,value".split(","), function (b, e) {
                N.attr(e, a.attr(e))
            });
            a.replaceWith(N);
            a = N
        }
        a.addClass(f.input);
        var H = a.add(h);
        if (!k.length) {
            k = d("<div><div><a/><div/><a/></div><div><div/><div/></div></div>").hide().css({
                position: "absolute"
            }).attr("id", f.root);
            k.children().eq(0).attr("id", f.head).end().eq(1).attr("id", f.body).children().eq(0).attr("id", f.days).end().eq(1).attr("id", f.weeks).end().end().end().find("a").eq(0).attr("id", f.prev).end().eq(1).attr("id", f.next);
            M = k.find("#" + f.head).find("div").attr("id", f.title);
            if (c.selectors) {
                var z = d("<select/>").attr("id", f.month),
                    A = d("<select/>").attr("id", f.year);
                M.html(z.add(A))
            }
            for (var $ = k.find("#" + f.days), O = 0; O < 7; O++) $.append(d("<span/>").text(s.shortDays[(O + c.firstDay) % 7]));
            d("body").append(k)
        }
        if (c.trigger) L = d("<a/>").attr("href", "#").addClass(f.trigger).click(function (b) {
            h.show();
            return b.preventDefault()
        }).insertAfter(a);
        var K = k.find("#" + f.weeks);
        A = k.find("#" + f.year);
        z = k.find("#" + f.month);
        d.extend(h, {
            show: function (b) {
                if (!(a.attr("readonly") || a.attr("disabled") || w)) {
                    b = b || d.Event();
                    b.type = "onBeforeShow";
                    H.trigger(b);
                    if (!b.isDefaultPrevented()) {
                        d.each(W, function () {
                            this.hide()
                        });
                        w = true;
                        z.unbind("change").change(function () {
                            h.setValue(A.val(), d(this).val())
                        });
                        A.unbind("change").change(function () {
                            h.setValue(d(this).val(), z.val())
                        });
                        I = k.find("#" + f.prev).unbind("click").click(function () {
                            I.hasClass(f.disabled) || h.addMonth(-1);
                            return false
                        });
                        J = k.find("#" + f.next).unbind("click").click(function () {
                            J.hasClass(f.disabled) || h.addMonth();
                            return false
                        });
                        h.setValue(n);
                        var e = a.offset();
                        if (/iPad/i.test(navigator.userAgent)) e.top -= d(window).scrollTop();
                        k.css({
                            top: e.top + a.outerHeight({
                                margins: true
                            }) + c.offset[0],
                            left: e.left + c.offset[1]
                        });
                        if (c.speed) k.show(c.speed, function () {
                            q(b)
                        });
                        else {
                            k.show();
                            q(b)
                        }
                        return h
                    }
                }
            },
            setValue: function (b, e, g) {
                var i = v(e) >= -1 ? new Date(v(b), v(e), v(g || 1)) : b || n;
                if (i < m) i = m;
                else if (i > p) i = p;
                b = i.getFullYear();
                e = i.getMonth();
                g = i.getDate();
                if (e == -1) {
                    e = 11;
                    b--
                } else if (e == 12) {
                    e = 0;
                    b++
                }
                if (!w) {
                    j(i, c);
                    return h
                }
                E = e;
                D = b;
                g = new Date(b, e, 1 - c.firstDay);
                g = g.getDay();
                var t = R(b, e),
                    o = R(b, e - 1),
                    P;
                if (c.selectors) {
                    z.empty();
                    d.each(s.months, function (x, F) {
                        m < new Date(b, x + 1, -1) && p > new Date(b, x, 0) && z.append(d("<option/>").html(F).attr("value", x))
                    });
                    A.empty();
                    i = r.getFullYear();
                    for (var l = i + c.yearRange[0]; l < i + c.yearRange[1]; l++) m <= new Date(l + 1, -1, 1) && p > new Date(l, 0, 0) && A.append(d("<option/>").text(l));
                    z.val(e);
                    A.val(b)
                } else M.html(s.months[e] + " " + b);
                K.empty();
                I.add(J).removeClass(f.disabled);
                l = !g ? -7 : 0;
                for (var u, y; l < (!g ? 35 : 42); l++) {
                    u = d("<a/>");
                    if (l % 7 === 0) {
                        P = d("<div/>").addClass(f.week);
                        K.append(P)
                    }
                    if (l < g) {
                        u.addClass(f.off);
                        y = o - g + l + 1;
                        i = new Date(b, e - 1, y)
                    } else if (l >= g + t) {
                        u.addClass(f.off);
                        y = l - t - g + 1;
                        i = new Date(b, e + 1, y)
                    } else {
                        y = l - g + 1;
                        i = new Date(b, e, y);
                        if (U(n, i)) u.attr("id", f.current).addClass(f.focus);
                        else U(r, i) && u.attr("id", f.today)
                    }
                    m && i < m && u.add(I).addClass(f.disabled);
                    p && i > p && u.add(J).addClass(f.disabled);
                    u.attr("href", "#" + y).text(y).data("date", i);
                    P.append(u)
                }
                K.find("a").click(function (x) {
                    var F = d(this);
                    if (!F.hasClass(f.disabled)) {
                        d("#" + f.current).removeAttr("id");
                        F.attr("id", f.current);
                        j(F.data("date"), c, x)
                    }
                    return false
                });
                f.sunday && K.find(f.week).each(function () {
                    var x = c.firstDay ? 7 - c.firstDay : 0;
                    d(this).children().slice(x, x + 1).addClass(f.sunday)
                });
                return h
            },
            setMin: function (b, e) {
                m = C(b);
                e && n < m && h.setValue(m);
                return h
            },
            setMax: function (b, e) {
                p = C(b);
                e && n > p && h.setValue(p);
                return h
            },
            today: function () {
                return h.setValue(r)
            },
            addDay: function (b) {
                return this.setValue(D, E, G + (b || 1))
            },
            addMonth: function (b) {
                return this.setValue(D, E + (b || 1), G)
            },
            addYear: function (b) {
                return this.setValue(D + (b || 1), E, G)
            },
            hide: function (b) {
                if (w) {
                    b = d.Event();
                    b.type = "onHide";
                    H.trigger(b);
                    d(document).unbind("click.d").unbind("keydown.d");
                    if (b.isDefaultPrevented()) return;
                    k.hide();
                    w = false
                }
                return h
            },
            getConf: function () {
                return c
            },
            getInput: function () {
                return a
            },
            getCalendar: function () {
                return k
            },
            getValue: function (b) {
                return b ? T(n, b, c.lang) : n
            },
            isOpen: function () {
                return w
            }
        });
        d.each(["onBeforeShow", "onShow", "change", "onHide"], function (b, e) {
            d.isFunction(c[e]) && d(h).bind(e, c[e]);
            h[e] = function (g) {
                g && d(h).bind(e, g);
                return h
            }
        });
        a.bind("focus click", h.show).keydown(function (b) {
            var e = b.keyCode;
            if (!w && d(V).index(e) >= 0) {
                h.show(b);
                return b.preventDefault()
            }
            return b.shiftKey || b.ctrlKey || b.altKey || e == 9 ? true : b.preventDefault()
        });
        C(a.val()) && j(n, c)
    }
    d.tools = d.tools || {
        version: "1.2.5"
    };
    var W = [],
        Q, V = [75, 76, 38, 39, 74, 72, 40, 37],
        B = {};
    Q = d.tools.dateinput = {
        conf: {
            format: "mm/dd/yy",
            selectors: false,
            yearRange: [-5, 5],
            lang: "en",
            offset: [0, 0],
            speed: 0,
            firstDay: 0,
            min: undefined,
            max: undefined,
            trigger: false,
            css: {
                prefix: "cal",
                input: "date",
                root: 0,
                head: 0,
                title: 0,
                prev: 0,
                next: 0,
                month: 0,
                year: 0,
                days: 0,
                body: 0,
                weeks: 0,
                today: 0,
                current: 0,
                week: 0,
                off: 0,
                sunday: 0,
                focus: 0,
                disabled: 0,
                trigger: 0
            }
        },
        localize: function (a, c) {
            d.each(c, function (j, q) {
                c[j] = q.split(",")
            });
            B[a] = c
        }
    };
    Q.localize("en", {
        months: "January,February,March,April,May,June,July,August,September,October,November,December",
        shortMonths: "Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec",
        days: "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday",
        shortDays: "Sun,Mon,Tue,Wed,Thu,Fri,Sat"
    });
    var X = /d{1,4}|m{1,4}|yy(?:yy)?|"[^"]*"|'[^']*'/g,
        Y = d("<a/>");
    d.expr[":"].date = function (a) {
        var c = a.getAttribute("type");
        return c && c == "date" || !! d(a).data("dateinput")
    };
    d.fn.dateinput = function (a) {
        if (this.data("dateinput")) return this;
        a = d.extend(true, {}, Q.conf, a);
        d.each(a.css, function (j, q) {
            if (!q && j != "prefix") a.css[j] = (a.css.prefix || "") + (q || j)
        });
        var c;
        this.each(function () {
            var j = new Z(d(this), a);
            W.push(j);
            j = j.getInput().data("dateinput", j);
            c = c ? c.add(j) : j
        });
        return c ? c : this
    }
})(jQuery);
(function (e) {
    function F(d, a) {
        a = Math.pow(10, a);
        return Math.round(d * a) / a
    }
    function q(d, a) {
        if (a = parseInt(d.css(a), 10)) return a;
        return (d = d[0].currentStyle) && d.width && parseInt(d.width, 10)
    }
    function C(d) {
        return (d = d.data("events")) && d.onSlide
    }
    function G(d, a) {
        function h(c, b, f, j) {
            if (f === undefined) f = b / k * z;
            else if (j) f -= a.min;
            if (s) f = Math.round(f / s) * s;
            if (b === undefined || s) b = f * k / z;
            if (isNaN(f)) return g;
            b = Math.max(0, Math.min(b, k));
            f = b / k * z;
            if (j || !n) f += a.min;
            if (n) if (j) b = k - b;
            else f = a.max - f;
            f = F(f, t);
            var r = c.type == "click";
            if (D && l !== undefined && !r) {
                c.type = "onSlide";
                A.trigger(c, [f, b]);
                if (c.isDefaultPrevented()) return g
            }
            j = r ? a.speed : 0;
            r = r ?
            function () {
                c.type = "change";
                A.trigger(c, [f])
            } : null;
            if (n) {
                m.animate({
                    top: b
                }, j, r);
                a.progress && B.animate({
                    height: k - b + m.width() / 2
                }, j)
            } else {
                m.animate({
                    left: b
                }, j, r);
                a.progress && B.animate({
                    width: b + m.width() / 2
                }, j)
            }
            l = f;
            H = b;
            d.val(f);
            return g
        }
        function o() {
            if (n = a.vertical || q(i, "height") > q(i, "width")) {
                k = q(i, "height") - q(m, "height");
                u = i.offset().top + k
            } else {
                k = q(i, "width") - q(m, "width");
                u = i.offset().left
            }
        }

        function v() {
            o();
            g.setValue(a.value !== undefined ? a.value : a.min)
        }
        var g = this,
            p = a.css,
            i = e("<div><div/><a href='#'/></div>").data("rangeinput", g),
            n, l, u, k, H;
        d.before(i);
        var m = i.addClass(p.slider).find("a").addClass(p.handle),
            B = i.find("div").addClass(p.progress);
        e.each("min,max,step,value".split(","), function (c, b) {
            c = d.attr(b);
            if (parseFloat(c)) a[b] = parseFloat(c, 10)
        });
        var z = a.max - a.min,
            s = a.step == "any" ? 0 : a.step,
            t = a.precision;
        if (t === undefined) try {
            t = s.toString().split(".")[1].length
        } catch (I) {
            t = 0
        }
        if (d.attr("type") == "range") {
            var w = e("<input/>");
            e.each("class,disabled,id,maxlength,name,readonly,required,size,style,tabindex,title,value".split(","), function (c, b) {
                w.attr(b, d.attr(b))
            });
            w.val(a.value);
            d.replaceWith(w);
            d = w
        }
        d.addClass(p.input);
        var A = e(g).add(d),
            D = true;
        e.extend(g, {
            getValue: function () {
                return l
            },
            setValue: function (c, b) {
                o();
                return h(b || e.Event("api"), undefined, c, true)
            },
            getConf: function () {
                return a
            },
            getProgress: function () {
                return B
            },
            getHandle: function () {
                return m
            },
            getInput: function () {
                return d
            },
            step: function (c, b) {
                b = b || e.Event();
                var f = a.step == "any" ? 1 : a.step;
                g.setValue(l + f * (c || 1), b)
            },
            stepUp: function (c) {
                return g.step(c || 1)
            },
            stepDown: function (c) {
                return g.step(-c || -1)
            }
        });
        e.each("onSlide,change".split(","), function (c, b) {
            e.isFunction(a[b]) && e(g).bind(b, a[b]);
            g[b] = function (f) {
                f && e(g).bind(b, f);
                return g
            }
        });
        m.drag({
            drag: false
        }).bind("dragStart", function () {
            o();
            D = C(e(g)) || C(d)
        }).bind("drag", function (c, b, f) {
            if (d.is(":disabled")) return false;
            h(c, n ? b : f)
        }).bind("dragEnd", function (c) {
            if (!c.isDefaultPrevented()) {
                c.type = "change";
                A.trigger(c, [l])
            }
        }).click(function (c) {
            return c.preventDefault()
        });
        i.click(function (c) {
            if (d.is(":disabled") || c.target == m[0]) return c.preventDefault();
            o();
            var b = m.width() / 2;
            h(c, n ? k - u - b + c.pageY : c.pageX - u - b)
        });
        a.keyboard && d.keydown(function (c) {
            if (!d.attr("readonly")) {
                var b = c.keyCode,
                    f = e([75, 76, 38, 33, 39]).index(b) != -1,
                    j = e([74, 72, 40, 34, 37]).index(b) != -1;
                if ((f || j) && !(c.shiftKey || c.altKey || c.ctrlKey)) {
                    if (f) g.step(b == 33 ? 10 : 1, c);
                    else if (j) g.step(b == 34 ? -10 : -1, c);
                    return c.preventDefault()
                }
            }
        });
        d.blur(function (c) {
            var b = e(this).val();
            b !== l && g.setValue(b, c)
        });
        e.extend(d[0], {
            stepUp: g.stepUp,
            stepDown: g.stepDown
        });
        v();
        k || e(window).load(v)
    }
    e.tools = e.tools || {
        version: "1.2.5"
    };
    var E;
    E = e.tools.rangeinput = {
        conf: {
            min: 0,
            max: 100,
            step: "any",
            steps: 0,
            value: 0,
            precision: undefined,
            vertical: 0,
            keyboard: true,
            progress: false,
            speed: 100,
            css: {
                input: "range",
                slider: "slider",
                progress: "progress",
                handle: "handle"
            }
        }
    };
    var x, y;
    e.fn.drag = function (d) {
        document.ondragstart = function () {
            return false
        };
        d = e.extend({
            x: true,
            y: true,
            drag: true
        }, d);
        x = x || e(document).bind("mousedown mouseup", function (a) {
            var h = e(a.target);
            if (a.type == "mousedown" && h.data("drag")) {
                var o = h.position(),
                    v = a.pageX - o.left,
                    g = a.pageY - o.top,
                    p = true;
                x.bind("mousemove.drag", function (i) {
                    var n = i.pageX - v;
                    i = i.pageY - g;
                    var l = {};
                    if (d.x) l.left = n;
                    if (d.y) l.top = i;
                    if (p) {
                        h.trigger("dragStart");
                        p = false
                    }
                    d.drag && h.css(l);
                    h.trigger("drag", [i, n]);
                    y = h
                });
                a.preventDefault()
            } else try {
                y && y.trigger("dragEnd")
            } finally {
                x.unbind("mousemove.drag");
                y = null
            }
        });
        return this.data("drag", true)
    };
    e.expr[":"].range = function (d) {
        var a = d.getAttribute("type");
        return a && a == "range" || !! e(d).filter("input").data("rangeinput")
    };
    e.fn.rangeinput = function (d) {
        if (this.data("rangeinput")) return this;
        d = e.extend(true, {}, E.conf, d);
        var a;
        this.each(function () {
            var h = new G(e(this), e.extend(true, {}, d));
            h = h.getInput().data("rangeinput", h);
            a = a ? a.add(h) : h
        });
        return a ? a : this
    }
})(jQuery);
(function (e) {
    function t(a, b, c) {
        var k = a.offset().top,
            f = a.offset().left,
            l = c.position.split(/,?\s+/),
            p = l[0];
        l = l[1];
        k -= b.outerHeight() - c.offset[0];
        f += a.outerWidth() + c.offset[1];
        if (/iPad/i.test(navigator.userAgent)) k -= e(window).scrollTop();
        c = b.outerHeight() + a.outerHeight();
        if (p == "center") k += c / 2;
        if (p == "bottom") k += c;
        a = a.outerWidth();
        if (l == "center") f -= (a + b.outerWidth()) / 2;
        if (l == "left") f -= a;
        return {
            top: k,
            left: f
        }
    }
    function y(a) {
        function b() {
            return this.getAttribute("type") == a
        }
        b.key = "[type=" + a + "]";
        return b
    }
    function u(a, b, c) {
        function k(g, d, i) {
            if (!(!c.grouped && g.length)) {
                var j;
                if (i === false || e.isArray(i)) {
                    j = h.messages[d.key || d] || h.messages["*"];
                    j = j[c.lang] || h.messages["*"].en;
                    (d = j.match(/\$\d/g)) && e.isArray(i) && e.each(d, function (m) {
                        j = j.replace(this, i[m])
                    })
                } else j = i[c.lang] || i;
                g.push(j)
            }
        }
        var f = this,
            l = b.add(f);
        a = a.not(":button, :image, :reset, :submit");
        e.extend(f, {
            getConf: function () {
                return c
            },
            getForm: function () {
                return b
            },
            getInputs: function () {
                return a
            },
            reflow: function () {
                a.each(function () {
                    var g = e(this),
                        d = g.data("msg.el");
                    if (d) {
                        g = t(g, d, c);
                        d.css({
                            top: g.top,
                            left: g.left
                        })
                    }
                });
                return f
            },
            invalidate: function (g, d) {
                if (!d) {
                    var i = [];
                    e.each(g, function (j, m) {
                        j = a.filter("[name='" + j + "']");
                        if (j.length) {
                            j.trigger("OI", [m]);
                            i.push({
                                input: j,
                                messages: [m]
                            })
                        }
                    });
                    g = i;
                    d = e.Event()
                }
                d.type = "onFail";
                l.trigger(d, [g]);
                d.isDefaultPrevented() || q[c.effect][0].call(f, g, d);
                return f
            },
            reset: function (g) {
                g = g || a;
                g.removeClass(c.errorClass).each(function () {
                    var d = e(this).data("msg.el");
                    if (d) {
                        d.remove();
                        e(this).data("msg.el", null)
                    }
                }).unbind(c.errorInputEvent || "");
                return f
            },
            destroy: function () {
                b.unbind(c.formEvent + ".V").unbind("reset.V");
                a.unbind(c.inputEvent + ".V").unbind("change.V");
                return f.reset()
            },
            checkValidity: function (g, d) {
                g = g || a;
                g = g.not(":disabled");
                if (!g.length) return true;
                d = d || e.Event();
                d.type = "onBeforeValidate";
                l.trigger(d, [g]);
                if (d.isDefaultPrevented()) return d.result;
                var i = [];
                g.not(":radio:not(:checked)").each(function () {
                    var m = [],
                        n = e(this).data("messages", m),
                        v = r && n.is(":date") ? "onHide.v" : c.errorInputEvent + ".v";
                    n.unbind(v);
                    e.each(w, function () {
                        var o = this,
                            s = o[0];
                        if (n.filter(s).length) {
                            o = o[1].call(f, n, n.val());
                            if (o !== true) {
                                d.type = "onBeforeFail";
                                l.trigger(d, [n, s]);
                                if (d.isDefaultPrevented()) return false;
                                var x = n.attr(c.messageAttr);
                                if (x) {
                                    m = [x];
                                    return false
                                } else k(m, s, o)
                            }
                        }
                    });
                    if (m.length) {
                        i.push({
                            input: n,
                            messages: m
                        });
                        n.trigger("OI", [m]);
                        c.errorInputEvent && n.bind(v, function (o) {
                            f.checkValidity(n, o)
                        })
                    }
                    if (c.singleError && i.length) return false
                });
                var j = q[c.effect];
                if (!j) throw 'Validator: cannot find effect "' + c.effect + '"';
                if (i.length) {
                    f.invalidate(i, d);
                    return false
                } else {
                    j[1].call(f, g, d);
                    d.type = "onSuccess";
                    l.trigger(d, [g]);
                    g.unbind(c.errorInputEvent + ".v")
                }
                return true
            }
        });
        e.each("onBeforeValidate,onBeforeFail,onFail,onSuccess".split(","), function (g, d) {
            e.isFunction(c[d]) && e(f).bind(d, c[d]);
            f[d] = function (i) {
                i && e(f).bind(d, i);
                return f
            }
        });
        c.formEvent && b.bind(c.formEvent + ".V", function (g) {
            if (!f.checkValidity(null, g)) return g.preventDefault()
        });
        b.bind("reset.V", function () {
            f.reset()
        });
        a[0] && a[0].validity && a.each(function () {
            this.oninvalid = function () {
                return false
            }
        });
        if (b[0]) b[0].checkValidity = f.checkValidity;
        c.inputEvent && a.bind(c.inputEvent + ".V", function (g) {
            f.checkValidity(e(this), g)
        });
        a.filter(":checkbox, select").filter("[required]").bind("change.V", function (g) {
            var d = e(this);
            if (this.checked || d.is("select") && e(this).val()) q[c.effect][1].call(f, d, g)
        });
        var p = a.filter(":radio").change(function (g) {
            f.checkValidity(p, g)
        });
        e(window).resize(function () {
            f.reflow()
        })
    }
    e.tools = e.tools || {
        version: "1.2.5"
    };
    var z = /\[type=([a-z]+)\]/,
        A = /^-?[0-9]*(\.[0-9]+)?$/,
        r = e.tools.dateinput,
        B = /^([a-z0-9_\.\-\+]+)@([\da-z\.\-]+)\.([a-z\.]{2,6})$/i,
        C = /^(https?:\/\/)?[\da-z\.\-]+\.[a-z\.]{2,6}[#&+_\?\/\w \.\-=]*$/i,
        h;
    h = e.tools.validator = {
        conf: {
            grouped: false,
            effect: "default",
            errorClass: "invalid",
            inputEvent: null,
            errorInputEvent: "keyup",
            formEvent: "submit",
            lang: "en",
            message: "<div/>",
            messageAttr: "data-message",
            messageClass: "error",
            offset: [0, 0],
            position: "center right",
            singleError: false,
            speed: "normal"
        },
        messages: {
            "*": {
                en: "Please correct this value"
            }
        },
        localize: function (a, b) {
            e.each(b, function (c, k) {
                h.messages[c] = h.messages[c] || {};
                h.messages[c][a] = k
            })
        },
        localizeFn: function (a, b) {
            h.messages[a] = h.messages[a] || {};
            e.extend(h.messages[a], b)
        },
        fn: function (a, b, c) {
            if (e.isFunction(b)) c = b;
            else {
                if (typeof b == "string") b = {
                    en: b
                };
                this.messages[a.key || a] = b
            }
            if (b = z.exec(a)) a = y(b[1]);
            w.push([a, c])
        },
        addEffect: function (a, b, c) {
            q[a] = [b, c]
        }
    };
    var w = [],
        q = {
            "default": [function (a) {
                var b = this.getConf();
                e.each(a, function (c, k) {
                    c = k.input;
                    c.addClass(b.errorClass);
                    var f = c.data("msg.el");
                    if (!f) {
                        f = e(b.message).addClass(b.messageClass).appendTo(document.body);
                        c.data("msg.el", f)
                    }
                    f.css({
                        visibility: "hidden"
                    }).find("p").remove();
                    e.each(k.messages, function (l, p) {
                        e("<p/>").html(p).appendTo(f)
                    });
                    f.outerWidth() == f.parent().width() && f.add(f.find("p")).css({
                        display: "inline"
                    });
                    k = t(c, f, b);
                    f.css({
                        visibility: "visible",
                        position: "absolute",
                        top: k.top,
                        left: k.left
                    }).fadeIn(b.speed)
                })
            }, function (a) {
                var b = this.getConf();
                a.removeClass(b.errorClass).each(function () {
                    var c = e(this).data("msg.el");
                    c && c.css({
                        visibility: "hidden"
                    })
                })
            }]
        };
    e.each("email,url,number".split(","), function (a, b) {
        e.expr[":"][b] = function (c) {
            return c.getAttribute("type") === b
        }
    });
    e.fn.oninvalid = function (a) {
        return this[a ? "bind" : "trigger"]("OI", a)
    };
    h.fn(":email", "Please enter a valid email address", function (a, b) {
        return !b || B.test(b)
    });
    h.fn(":url", "Please enter a valid URL", function (a, b) {
        return !b || C.test(b)
    });
    h.fn(":number", "Please enter a numeric value.", function (a, b) {
        return A.test(b)
    });
    h.fn("[max]", "Please enter a value smaller than $1", function (a, b) {
        if (b === "" || r && a.is(":date")) return true;
        a = a.attr("max");
        return parseFloat(b) <= parseFloat(a) ? true : [a]
    });
    h.fn("[min]", "Please enter a value larger than $1", function (a, b) {
        if (b === "" || r && a.is(":date")) return true;
        a = a.attr("min");
        return parseFloat(b) >= parseFloat(a) ? true : [a]
    });
    h.fn("[required]", "Please complete this mandatory field.", function (a, b) {
        if (a.is(":checkbox")) return a.is(":checked");
        return !!b
    });
    h.fn("[pattern]", function (a) {
        var b = new RegExp("^" + a.attr("pattern") + "$");
        return b.test(a.val())
    });
    e.fn.validator = function (a) {
        var b = this.data("validator");
        if (b) {
            b.destroy();
            this.removeData("validator")
        }
        a = e.extend(true, {}, h.conf, a);
        if (this.is("form")) return this.each(function () {
            var c = e(this);
            b = new u(c.find(":input"), c, a);
            c.data("validator", b)
        });
        else {
            b = new u(this, this.eq(0).closest("form"), a);
            return this.data("validator", b)
        }
    }
})(jQuery);
(function () {
    function f(a, b) {
        if (b) for (var c in b) if (b.hasOwnProperty(c)) a[c] = b[c];
        return a
    }
    function l(a, b) {
        var c = [];
        for (var d in a) if (a.hasOwnProperty(d)) c[d] = b(a[d]);
        return c
    }
    function m(a, b, c) {
        if (e.isSupported(b.version)) a.innerHTML = e.getHTML(b, c);
        else if (b.expressInstall && e.isSupported([6, 65])) a.innerHTML = e.getHTML(f(b, {
            src: b.expressInstall
        }), {
            MMredirectURL: location.href,
            MMplayerType: "PlugIn",
            MMdoctitle: document.title
        });
        else {
            if (!a.innerHTML.replace(/\s/g, "")) {
                a.innerHTML = "<h2>Flash version " + b.version + " or greater is required</h2><h3>" + (g[0] > 0 ? "Your version is " + g : "You have no flash plugin installed") + "</h3>" + (a.tagName == "A" ? "<p>Click here to download latest version</p>" : "<p>Download latest version from <a href='" + k + "'>here</a></p>");
                if (a.tagName == "A") a.onclick = function () {
                    location.href = k
                }
            }
            if (b.onFail) {
                var d = b.onFail.call(this);
                if (typeof d == "string") a.innerHTML = d
            }
        }
        if (i) window[b.id] = document.getElementById(b.id);
        f(this, {
            getRoot: function () {
                return a
            },
            getOptions: function () {
                return b
            },
            getConf: function () {
                return c
            },
            getApi: function () {
                return a.firstChild
            }
        })
    }
    var i = document.all,
        k = "http://www.adobe.com/go/getflashplayer",
        n = typeof jQuery == "function",
        o = /(\d+)[^\d]+(\d+)[^\d]*(\d*)/,
        j = {
            width: "100%",
            height: "100%",
            id: "_" + ("" + Math.random()).slice(9),
            allowfullscreen: true,
            allowscriptaccess: "always",
            quality: "high",
            version: [3, 0],
            onFail: null,
            expressInstall: null,
            w3c: false,
            cachebusting: false
        };
    window.attachEvent && window.attachEvent("onbeforeunload", function () {
        __flash_unloadHandler = function () {};
        __flash_savedUnloadHandler = function () {}
    });
    window.flashembed = function (a, b, c) {
        if (typeof a == "string") a = document.getElementById(a.replace("#", ""));
        if (a) {
            if (typeof b == "string") b = {
                src: b
            };
            return new m(a, f(f({}, j), b), c)
        }
    };
    var e = f(window.flashembed, {
        conf: j,
        getVersion: function () {
            var a, b;
            try {
                b = navigator.plugins["Shockwave Flash"].description.slice(16)
            } catch (c) {
                try {
                    b = (a = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7")) && a.GetVariable("$version")
                } catch (d) {
                    try {
                        b = (a = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6")) && a.GetVariable("$version")
                    } catch (h) {}
                }
            }
            return (b = o.exec(b)) ? [b[1], b[3]] : [0, 0]
        },
        asString: function (a) {
            if (a === null || a === undefined) return null;
            var b = typeof a;
            if (b == "object" && a.push) b = "array";
            switch (b) {
            case "string":
                a = a.replace(new RegExp('(["\\\\])', "g"), "\\$1");
                a = a.replace(/^\s?(\d+\.?\d+)%/, "$1pct");
                return '"' + a + '"';
            case "array":
                return "[" + l(a, function (d) {
                    return e.asString(d)
                }).join(",") + "]";
            case "function":
                return '"function()"';
            case "object":
                b = [];
                for (var c in a) a.hasOwnProperty(c) && b.push('"' + c + '":' + e.asString(a[c]));
                return "{" + b.join(",") + "}"
            }
            return String(a).replace(/\s/g, " ").replace(/\'/g, '"')
        },
        getHTML: function (a, b) {
            a = f({}, a);
            var c = '<object width="' + a.width + '" height="' + a.height + '" id="' + a.id + '" name="' + a.id + '"';
            if (a.cachebusting) a.src += (a.src.indexOf("?") != -1 ? "&" : "?") + Math.random();
            c += a.w3c || !i ? ' data="' + a.src + '" type="application/x-shockwave-flash"' : ' classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"';
            c += ">";
            if (a.w3c || i) c += '<param name="movie" value="' + a.src + '" />';
            a.width = a.height = a.id = a.w3c = a.src = null;
            a.onFail = a.version = a.expressInstall = null;
            for (var d in a) if (a[d]) c += '<param name="' + d + '" value="' + a[d] + '" />';
            a = "";
            if (b) {
                for (var h in b) if (b[h]) {
                    d = b[h];
                    a += h + "=" + (/function|object/.test(typeof d) ? e.asString(d) : d) + "&"
                }
                a = a.slice(0, -1);
                c += '<param name="flashvars" value=\'' + a + "' />"
            }
            c += "</object>";
            return c
        },
        isSupported: function (a) {
            return g[0] > a[0] || g[0] == a[0] && g[1] >= a[1]
        }
    }),
        g = e.getVersion();
    if (n) {
        jQuery.tools = jQuery.tools || {
            version: "1.2.5"
        };
        jQuery.tools.flashembed = {
            conf: j
        };
        jQuery.fn.flashembed = function (a, b) {
            return this.each(function () {
                $(this).data("flashembed", flashembed(this, a, b))
            })
        }
    }
})();
(function (b) {
    function h(c) {
        if (c) {
            var a = d.contentWindow.document;
            a.open().close();
            a.location.hash = c
        }
    }
    var g, d, f, i;
    b.tools = b.tools || {
        version: "1.2.5"
    };
    b.tools.history = {
        init: function (c) {
            if (!i) {
                if (b.browser.msie && b.browser.version < "8") {
                    if (!d) {
                        d = b("<iframe/>").attr("src", "javascript:false;").hide().get(0);
                        b("body").append(d);
                        setInterval(function () {
                            var a = d.contentWindow.document;
                            a = a.location.hash;
                            g !== a && b.event.trigger("hash", a)
                        }, 100);
                        h(location.hash || "#")
                    }
                } else setInterval(function () {
                    var a = location.hash;
                    a !== g && b.event.trigger("hash", a)
                }, 100);
                f = !f ? c : f.add(c);
                c.click(function (a) {
                    var e = b(this).attr("href");
                    d && h(e);
                    if (e.slice(0, 1) != "#") {
                        location.href = "#" + e;
                        return a.preventDefault()
                    }
                });
                i = true
            }
        }
    };
    b(window).bind("hash", function (c, a) {
        a ? f.filter(function () {
            var e = b(this).attr("href");
            return e == a || e == a.replace("#", "")
        }).trigger("history", [a]) : f.eq(0).trigger("history", [a]);
        g = a
    });
    b.fn.history = function (c) {
        b.tools.history.init(this);
        return this.bind("history", c)
    }
})(jQuery);
(function (b) {
    function k() {
        if (b.browser.msie) {
            var a = b(document).height(),
                d = b(window).height();
            return [window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth, a - d < 20 ? d : a]
        }
        return [b(document).width(), b(document).height()]
    }
    function h(a) {
        if (a) return a.call(b.mask)
    }
    b.tools = b.tools || {
        version: "1.2.5"
    };
    var l;
    l = b.tools.expose = {
        conf: {
            maskId: "exposeMask",
            loadSpeed: "slow",
            closeSpeed: "fast",
            closeOnClick: true,
            closeOnEsc: true,
            zIndex: 9998,
            opacity: 0.8,
            startOpacity: 0,
            color: "#fff",
            onLoad: null,
            onClose: null
        }
    };
    var c, i, e, g, j;
    b.mask = {
        load: function (a, d) {
            if (e) return this;
            if (typeof a == "string") a = {
                color: a
            };
            a = a || g;
            g = a = b.extend(b.extend({}, l.conf), a);
            c = b("#" + a.maskId);
            if (!c.length) {
                c = b("<div/>").attr("id", a.maskId);
                b("body").append(c)
            }
            var m = k();
            c.css({
                position: "absolute",
                top: 0,
                left: 0,
                width: m[0],
                height: m[1],
                display: "none",
                opacity: a.startOpacity,
                zIndex: a.zIndex
            });
            a.color && c.css("backgroundColor", a.color);
            if (h(a.onBeforeLoad) === false) return this;
            a.closeOnEsc && b(document).bind("keydown.mask", function (f) {
                f.keyCode == 27 && b.mask.close(f)
            });
            a.closeOnClick && c.bind("click.mask", function (f) {
                b.mask.close(f)
            });
            b(window).bind("resize.mask", function () {
                b.mask.fit()
            });
            if (d && d.length) {
                j = d.eq(0).css("zIndex");
                b.each(d, function () {
                    var f = b(this);
                    /relative|absolute|fixed/i.test(f.css("position")) || f.css("position", "relative")
                });
                i = d.css({
                    zIndex: Math.max(a.zIndex + 1, j == "auto" ? 0 : j)
                })
            }
            c.css({
                display: "block"
            }).fadeTo(a.loadSpeed, a.opacity, function () {
                b.mask.fit();
                h(a.onLoad);
                e = "full"
            });
            e = true;
            return this
        },
        close: function () {
            if (e) {
                if (h(g.onBeforeClose) === false) return this;
                c.fadeOut(g.closeSpeed, function () {
                    h(g.onClose);
                    i && i.css({
                        zIndex: j
                    });
                    e = false
                });
                b(document).unbind("keydown.mask");
                c.unbind("click.mask");
                b(window).unbind("resize.mask")
            }
            return this
        },
        fit: function () {
            if (e) {
                var a = k();
                c.css({
                    width: a[0],
                    height: a[1]
                })
            }
        },
        getMask: function () {
            return c
        },
        isLoaded: function (a) {
            return a ? e == "full" : e
        },
        getConf: function () {
            return g
        },
        getExposed: function () {
            return i
        }
    };
    b.fn.mask = function (a) {
        b.mask.load(a);
        return this
    };
    b.fn.expose = function (a) {
        b.mask.load(a, this);
        return this
    }
})(jQuery);
(function (b) {
    function c(a) {
        switch (a.type) {
        case "mousemove":
            return b.extend(a.data, {
                clientX: a.clientX,
                clientY: a.clientY,
                pageX: a.pageX,
                pageY: a.pageY
            });
        case "DOMMouseScroll":
            b.extend(a, a.data);
            a.delta = -a.detail / 3;
            break;
        case "mousewheel":
            a.delta = a.wheelDelta / 120;
            break
        }
        a.type = "wheel";
        return b.event.handle.call(this, a, a.delta)
    }
    b.fn.mousewheel = function (a) {
        return this[a ? "bind" : "trigger"]("wheel", a)
    };
    b.event.special.wheel = {
        setup: function () {
            b.event.add(this, d, c, {})
        },
        teardown: function () {
            b.event.remove(this, d, c)
        }
    };
    var d = !b.browser.mozilla ? "mousewheel" : "DOMMouseScroll" + (b.browser.version < "1.9" ? " mousemove" : "")
})(jQuery);
(function ($) {
    $(".ui-autocomplete-input").live("autocompleteopen", function () {
        var autocomplete = $(this).data("autocomplete"),
            menu = autocomplete.menu;
        if (!autocomplete.options.selectFirst) {
            return;
        }
        if (autocomplete.term != $(this).val()) {
            return;
        }
        menu.options.blur = function (event, ui) {
            return
        }
        menu.activate($.Event({
            type: "mouseenter"
        }), menu.element.children().first());
    });
}(jQuery));
$(function () {
    hgFunctions.util.addTipsy();
});
$(function () {
    selectCompany = $("#selectCompanyOverlay").overlay({
        api: true,
        zIndex: 9997,
        mask: {
            color: '#000',
            loadSpeed: 200,
            opacity: 0.42
        },
        closeOnClick: true,
        fixed: false,
        load: false,
        onClose: function () {
            if (requestCompanyLink != null) {
                url = getContextualAjaxUrl(requestCompanyLink.attr('href'));
                if (lastAddCompanyName != null) {
                    data = {
                        companyName: lastAddCompanyName
                    };
                    url += '?' + $.param(data);
                    lastAddCompanyName = null;
                }
                $.get(url, function (response) {
                    container = requestCompanyLink.attr('rel') + ' .select-content';
                    $(container).html(response);
                    $('.sm-cancel', $(container)).click(function (el) {
                        requestCompany.close();
                        requestCompanyLink = null;
                    });
                    addRequestNewCompanySubmitHandler(container);
                    requestCompany.load();
                });
            }
        }
    });
    requestCompany = $('#requestAddCompanyOverlay').overlay({
        api: true,
        top: 160,
        zIndex: 9998,
        mask: {
            color: '#000',
            loadSpeed: 200,
            opacity: 0.42
        },
        closeOnClick: true,
        fixed: false,
        load: false
    });
    $('.sm-select-company').click(function (el) {
        el.preventDefault();
        link = $(this, el);
        $.get(getContextualAjaxUrl(link.attr('href')), function (response) {
            $(link.attr('rel') + " .select-content").html(response);
            companyChooseAddAutocomplete("#selectCompanyName", "#selectCompanySlug", false, true);
            companyChooseAddSubmitHandler();
            selectCompany.load();
        });
    });
    if ($('.sm-select-company-page').length > 0) {
        companyChooseAddAutocomplete("#selectCompanyName", "#selectCompanySlug", false, true);
    }
    toggleCompanyWebsite($('#selectCompanySlug, #entitySmid, #profile_employerSmid').val());
    $('#selectCompanyOverlay').delegate('.sm-request-company', 'click', function (e) {
        e.preventDefault();
        requestCompanyLink = $(this, e);
        selectCompany.close();
    });
});
var requestCompanyLink = null;
var lastAddCompanyName = null;

function ACCustomSelectHandler(item, smidFieldId, nameFieldId, companyname) {
    toggleCompanyWebsite(item.slug);
    form = $('#selectCompanyForm');
    container = form.closest('.select-content');
    if (item.slug == '_add') {
        $('#addCompanyForm_name').val(item.value);
        requestCompanyLink = $('.sm-request-company', container);
        lastAddCompanyName = item.value;
        selectCompany.close();
    } else {
        if (item && typeof form.attr('addtype') != 'undefined') {
            path = form.attr(form.attr('addtype'));
            path = path.replace("{company}", $(smidFieldId).val());
            window.location = path;
        }
    }
}

function toggleCompanyWebsite(companyVal) {
    if (companyVal == '_add') {
        $('.websiteHolder').show();
        $('.websiteHolder INPUT').removeAttr('disabled');
    } else if (typeof companyVal != 'undefined') {
        $('.websiteHolder').hide();
        $('.websiteHolder INPUT').attr('disabled', 'disabled');
    }
}

function companyChooseAddSubmitHandler() {
    $('#selectCompanyForm').submit(function (e) {
        e.preventDefault();
        form = $(this, e);
        container = form.closest('.select-content');
        text = $('#selectCompanyName', form).val();
        data = $.param({
            source: 'ajax',
            selectCompanyName: text
        });
        $.post(form.getAction(), data, function (response) {
            $(container).html(response);
            companyChooseAddAutocomplete("#selectCompanyName", "#selectCompanySlug", false, true);
            companyChooseAddSubmitHandler();
        });
    });
}

function addRequestNewCompanySubmitHandler(container) {
    $('FORM', $(container)).submit(function (e) {
        postedData = $(this, e).serialize();
        e.preventDefault();
        $.post(getContextualAjaxUrl($(this, e).attr('action')), postedData, function (data) {
            if (typeof data.json != 'undefined' && data.json.code.toLowerCase() == 'success') {
                $(container).html(data.json.message);
            } else {
                $(container).html(data);
                addRequestNewCompanySubmitHandler(container);
            }
        });
    });
    $('.sm-cancel', $(container)).click(function (e) {
        e.preventDefault();
        requestCompany.close();
    });
}
$(document).ready(function () {
    hover($("#smLogo"), "/static/images/private-company-stock-icon-hover.png", $("#smDetails"), true);
    $("#privateCompanyStock").hover(function () {
        hoverOffPCM();
        hover($("#pcmLogo"), contextPath + "static/images/private-company-stock-icon-hover.png", $("#pcmDetails"), true);
    }, function () {
        hoverOnPCM();
        hover($("#pcmLogo"), contextPath + "static/images/private-company-stock-icon-grey.png", $("#pcmDetails"), false);
    });
    $("#communityBanks").hover(function () {
        hoverOffPCM();
        hover($("#cbLogo"), contextPath + "static/images/com-bank-icon-hover.png", $("#cbDetails"), true);
    }, function () {
        hoverOnPCM();
        hover($("#cbLogo"), contextPath + "static/images/com-bank-icon-grey.png", $("#cbDetails"), false);
    });
    $("#fixedIncome").hover(function () {
        hoverOffPCM();
        hover($("#fiLogo"), contextPath + "static/images/fixed-income-icon-hover.png", $("#fiDetails"), true);
    }, function () {
        hoverOnPCM();
        hover($("#fiLogo"), contextPath + "static/images/fixed-income-icon-grey.png", $("#fiDetails"), false);
    });
    $("#bankruptcyClaims").hover(function () {
        hoverOffPCM();
        hover($("#bcLogo"), contextPath + "static/images/bankruptcy-claims-icon-hover.png", $("#bcDetails"), true);
    }, function () {
        hoverOnPCM();
        hover($("#bcLogo"), contextPath + "static/images/bankruptcy-claims-icon-grey.png", $("#bcDetails"), false);
    });
    $("#restrictedPublicEquity").hover(function () {
        hoverOffPCM();
        hover($("#rpeLogo"), contextPath + "static/images/restricted-public-equity-icon-hover.png", $("#rpeDetails"), true);
    }, function () {
        hoverOnPCM();
        hover($("#rpeLogo"), contextPath + "static/images/restricted-public-equity-icon-grey.png", $("#rpeDetails"), false);
    });
    $('#newMarket').slideDown(1000);
});

function hoverOnPCM() {
    hover($("#smLogo"), contextPath + "static/images/private-company-stock-icon-hover.png", $("#smDetails"), true);
}

function hoverOffPCM() {
    hover($("#smLogo"), contextPath + "static/images/private-company-stock-icon-grey.png", $("#smDetails"), false);
}

function hover(imgElement, newSrc, detailsDiv, bOn) {
    imgElement.attr("src", newSrc);
    if (bOn) {
        detailsDiv.css('display', 'block');
    } else {
        detailsDiv.hide();
    }
}
(function () {
    var small = "(a|an|and|as|at|but|by|en|for|if|in|of|on|or|the|to|v[.]?|via|vs[.]?)";
    var punct = "([!\"#$%&'()*+,./:;<=>?@[\\\\\\]^_`{|}~-]*)";
    this.titleCaps = function (title) {
        var parts = [],
            split = /[:.;?!] |(?: |^)["�]/g,
            index = 0;
        while (true) {
            var m = split.exec(title);
            parts.push(title.substring(index, m ? m.index : title.length).replace(/\b([A-Za-z][a-z.'�]*)\b/g, function (all) {
                return /[A-Za-z]\.[A-Za-z]/.test(all) ? all : upper(all);
            }).replace(RegExp("\\b" + small + "\\b", "ig"), lower).replace(RegExp("^" + punct + small + "\\b", "ig"), function (all, punct, word) {
                return punct + upper(word);
            }).replace(RegExp("\\b" + small + punct + "$", "ig"), upper));
            index = split.lastIndex;
            if (m) parts.push(m[0]);
            else break;
        }
        return parts.join("").replace(/ V(s?)\. /ig, " v$1. ").replace(/(['�])S\b/ig, "$1s").replace(/\b(AT&T|Q&A)\b/ig, function (all) {
            return all.toUpperCase();
        });
    };

    function lower(word) {
        return word.toLowerCase();
    }

    function upper(word) {
        return word.substr(0, 1).toUpperCase() + word.substr(1);
    }
})();