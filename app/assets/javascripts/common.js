(function ($) {
    'use strict';

    VoaCommon.getQueryString = function (name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)'),
            results = regex.exec(location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    };

    VoaCommon.getSegment = function(index) {
        return window.location.pathname.split('/')[index];
    };


    VoaCommon.anchorFocus = function () {
        if (window.location.hash) {
            var input = $(window.location.hash);
            if (input.hasClass('form-group')) {
                setTimeout(function () {
                    if (input.find('input').is(':radio')) {
                        input.find('input:checked').focus();
                    } else {
                        var html = input.find('input, textarea').val();
                        input.find('input, textarea').focus().val('').val(html);
                        if (input.find('input').hasClass('typeahead')) {
                            input.find('input.typeahead').val(input.closest('.form-group').find('input.typeahead:first').attr('value'));
                        }
                    }
                }, 100);
            } else if (input.hasClass('multi-fields-group')) {
                setTimeout(function () {
                    var find = input.find('textarea:first'),
                        element = find.val();
                    find.focus().val('').val(element);
                }, 100);
            } else {
                setTimeout(function () {
                    var find = input.closest('.form-group').find('input:first'),
                        element = find.val();
                    find.focus().val('').val(element);
                }, 100);

            }
        }
    };

    VoaCommon.linkShowManualAddress = function () {
        $('.showHide').click(function (e) {
            e.preventDefault();
            var element = $(this).closest('.postcode-lookup-group');
            if (element.find('.showHide-group').is(':visible')) {
                element.find('.showHide-group').css('display', 'none');
                element.find('.form-group').addClass('hidden');
                element.find('.form-group-lookup').css('display', 'block');
                element.find('.form-group-lookup input:first').focus();
            } else {
                element.find('.showHide-group').css('display', 'block');
                element.find('.form-group').removeClass('hidden');
                element.find('.form-group-lookup').css('display', 'none');
                element.find('.showHide-group input:first').focus();
            }
            $(this).html(element.find('.showHide-group').is(':visible') ? VoaMessages.textLabel('findPostcode') : VoaMessages.textLabel('enterManual'));
        });
    };

    VoaCommon.showAddressfieldsCondition = function (that) {
        if ($(that).find('.showHide-group .address-field-one input').val() !== '' ||
            $(that).find('.showHide-group .address-field-two input').val() !== '' ||
            $(that).find('.showHide-group .address-field-three input').val() !== '' ||
            $(that).find('.showHide-group .address-field-postcode input').val() !== '') {
            return true;
        } else {
            return false;
        }
    };

    VoaCommon.showAddressfields = function (that) {
        $(that).find('.showHide-group').css('display', 'inline-block');
        $(that).find('.showHide-group .form-group').removeClass('hidden');
        $(that).find('.form-group-lookup').css('display', 'none');
        $(that).find('.manual-address').text(VoaMessages.textLabel('findPostcode'));
    };

    VoaCommon.details = function(){

        $('details').each(function(){
            $(this).find('summary span').after('<span class="screenDetails visuallyhidden">' + VoaMessages.textLabel('labelReveal') + '</span>');
        });

        $('details').click(function(){
            if($(this).attr('open')){
                $(this).find('summary span.screenDetails').text(VoaMessages.textLabel('labelReveal'));
            }else{
                $(this).find('summary span.screenDetails').text(VoaMessages.textLabel('labelHide'));
            }
        });
    };

    VoaCommon.characterCount = function(){
        var maxLength = $('.charCount .chars').attr('data-max-length');

        function doMaxLength(that, e){
            var length = $(that).val().length;
            length = maxLength-length;
            $(that).closest('.form-group').find('.chars').text(length);
        }

        $('textarea').each(function(e){
            $(this).attr('maxlength', maxLength);
            doMaxLength(this, e);
        });

        $(document).on('keyup', 'textarea', function (e) {
            doMaxLength(this, e);
        });

        // < IE9
        $('textarea[maxlength]').keyup(function(){
            var text = $(this).val();
            var limit = $(this).attr('maxlength');
            if(text.length > limit){
                $(this).val(text.substr(0, limit));
            }
        });
    };



})(jQuery);
